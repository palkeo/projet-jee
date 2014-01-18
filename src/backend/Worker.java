package backend;

import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.cli.ParseException;
import java.sql.*;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.net.*;
import java.io.IOException;
import java.io.File;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import backend.*;

public class Worker
{
    private Logger log;
    private WorkerConfig config;
    private java.sql.Connection dbConnection;
    private QueueConnection jmsConnection;
    private QueueReceiver jmsReceiver;
    private ServerSocket socket;

    public static void main(String argv[])
    {
        try
        {
            WorkerConfig config = WorkerConfig.fromCommandLine(argv);

            if(config != null)
            {
                Worker worker = new Worker(config);
                worker.run();
            }
        }
        catch(ParseException | SQLException | JMSException | IOException e) {
            e.printStackTrace();
        }
    }

    public Worker(WorkerConfig config) throws SQLException, JMSException, UnknownHostException, IOException
    {
        this.log = Logger.getLogger("worker");
        this.config = config;

        // connection to database
        log.info("Connection to database");
        dbConnection = DriverManager.getConnection(config.getDatabaseUrl(), config.getDatabaseLogin(), config.getDatabasePassword());

        // connection to jms server
        log.info("Connection to jms server");
        ActiveMQConnectionFactory factory;

        if(config.getJmsLogin() != null)
           factory = new ActiveMQConnectionFactory(config.getJmsLogin(), config.getJmsPassword(), config.getJmsUrl());
        else
           factory = new ActiveMQConnectionFactory(config.getJmsUrl());

        jmsConnection = factory.createQueueConnection();
        jmsConnection.start();
        QueueSession session = jmsConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("Matches");
        jmsReceiver = session.createReceiver(queue);

        // listening
        if(config.getGameServerHost() != null)
        {
            socket = new ServerSocket(config.getGameServerPort(), 0, InetAddress.getByName(config.getGameServerHost()));
        }
        else
        {
            socket = new ServerSocket(config.getGameServerPort());
        }
        log.info(String.format("Listening on %s:%d",
            config.getGameServerHost() == null ? "localhost" : config.getGameServerHost(),
            config.getGameServerPort()));
    }

    public void run() throws SQLException, JMSException, IOException
    {
        log.info("Running worker");

        try
        {
            while(true)
            {
                // waiting match id
                log.info("Waiting match...");
                TextMessage message = (TextMessage)jmsReceiver.receive();
                int matchId = Integer.parseInt(message.getText());
                log.info(String.format("Launching match %d", matchId));

                // fetch match information
                PreparedStatement ps = dbConnection.prepareStatement("SELECT "
                    + "match.state, game.name AS game_name, game.class_name, "
                    + "ai1.id AS ai1_id, ai1.filename AS ai1_filename, ai1.name AS ai1_name, ai1.elo AS ai1_elo, "
                    + "ai2.id AS ai2_id, ai2.filename AS ai2_filename, ai2.name AS ai2_name, ai2.elo AS ai2_elo "
                    + "FROM match "
                    + "LEFT JOIN game ON game.id=match.game "
                    + "LEFT JOIN ai AS ai1 ON ai1.id=match.ai1 "
                    + "LEFT JOIN ai AS ai2 ON ai2.id=match.ai2 "
                    + "WHERE match.id=?");
                ps.setInt(1, matchId);
                ResultSet rs = ps.executeQuery();

                if(!rs.next())
                {
                    log.info(String.format("Error: cannot find match %d", matchId));
                    return;
                }

                if(rs.getInt("state") != 0)
                {
                    log.info(String.format("Error: match already %s", rs.getInt("state") == 1 ? "running" : "finished"));
                    return;
                }

                String gameName = rs.getString("game_name");
                String gameClass = rs.getString("class_name");
                AI[] ais = {
                    new AI(rs.getInt("ai1_id"), rs.getString("ai1_name"), rs.getString("ai1_filename"), rs.getInt("ai1_elo")),
                    new AI(rs.getInt("ai2_id"), rs.getString("ai2_name"), rs.getString("ai2_filename"), rs.getInt("ai2_elo")),
                };

                log.info(String.format("%s - %s vs %s", gameName, ais[0].getName(), ais[1].getName()));

                // save match state (running) and worker
                saveMatchRunning(matchId);

                // instantiate game engine
                GameEngine gameEngine = null;
                try
                {
                    gameEngine = instantiateGameEngine(gameClass);
                }
                catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    log.info(String.format("Error: cannot instanciate game engine : %s", e));
                    return;
                }

                // check and extract archives
                if(!extractArchive(ais[0]) || !extractArchive(ais[1]))
                    return;

                // launch AIs
                if(!launch(ais[0]) || !launch(ais[1]))
                    return;

                // generate match
                MatchResult result = generateMatch(gameEngine, matchId, ais);

                // finish
                for(int i=0; i<=1; i++)
                {
                    ais[i].killProcess();
                    ais[i].closeSocket();
                }

                // save results
                log.info(String.format("AI %s won", ais[result.getWinner()].getName()));
                saveResult(gameEngine, result, matchId);
                saveElo(result.getWinner(), ais);
            }
        }
        finally {
            log.info("Exiting...");
            try {
                dbConnection.close();
                jmsConnection.close();
            }
            catch(Throwable ignore) {}
        }
    }

    private void saveMatchRunning(int matchId) throws SQLException
    {
        PreparedStatement ps = dbConnection.prepareStatement("UPDATE match SET state=1, worker=? WHERE id=?");
        ps.setString(1, String.format("%s:%d",
            getLANAddress() == null ? socket.getInetAddress().getHostName() : getLANAddress().getHostName(),
            socket.getLocalPort()));
        ps.setInt(2, matchId);
        ps.executeUpdate();
    }

    public GameEngine instantiateGameEngine(String gameClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Class<?> gameEngineClass = Class.forName(gameClass);
        return (GameEngine) gameEngineClass.newInstance();
    }

    public boolean extractArchive(AI ai)
    {
        String archive = config.getPlayersDirectory() + ai.getFilename();
        String archiveExtractDir = config.getExtractDirectory() + ai.getFilename();
        String aiDirectory = config.getExtractDirectory() + FilenameUtils.removeExtension(FilenameUtils.removeExtension(ai.getFilename()));

        // check for archive
        File archiveFile = new File(archive);
        if(!archiveFile.isFile())
        {
            log.info(String.format("Error: cannot find archive %s of AI %s", archive, ai.getName()));
            return false;
        }

        // copy to extract directory
        File archiveExtractDirFile = new File(archiveExtractDir);
        try
        {
            FileUtils.copyFile(archiveFile, archiveExtractDirFile);
        }
        catch(IOException e) {
            log.info(String.format("Error: cannot copy archive %s to %s : %s", archive, archiveExtractDir, e));
            return false;
        }

        // extract archive
        File aiDirectoryFile = new File(aiDirectory);
        if(!aiDirectoryFile.isDirectory())
        {
            aiDirectoryFile.mkdir();
            ProcessBuilder pb = new ProcessBuilder("tar", "xf", archiveExtractDir, "-C", aiDirectory);
            Process p = null;

            try
            {
                log.info(String.format("extracting archive %s", archiveExtractDir));
                p = pb.start();
                p.waitFor();
            }
            catch(IOException | InterruptedException e) {
                log.info(String.format("Error: cannot extract archive %s : %s", archive, e));
                return false;
            }

            if(p.exitValue() != 0)
            {
                log.info(String.format("Error: cannot extract archive %s (tar returned error code : %d)", archive, p.exitValue()));
                return false;
            }
        }

        return true;
    }

    private boolean launch(AI ai)
    {
        String aiDirectory = config.getExtractDirectory() + FilenameUtils.removeExtension(FilenameUtils.removeExtension(ai.getFilename()));
        File aiDirectoryFile = new File(aiDirectory);
        File launch = new File(aiDirectory + "/launch");

        if(!launch.isFile())
        {
            List<File> files = (List<File>) FileUtils.listFilesAndDirs(aiDirectoryFile, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            if(files.size() == 1 && files.get(0).isDirectory())
            {
                launch = new File(aiDirectory + "/" + files.get(0).getName() + "/launch");
            }
        }

        if(!launch.isFile())
        {
            log.info(String.format("Error: cannot find launch file for AI %s", ai.getName()));
            return false;
        }

        // launch it
        ProcessBuilder pb = new ProcessBuilder("bash", launch.getAbsolutePath(), "localhost", String.valueOf(config.getGameServerPort()));
        pb.directory(launch.getParentFile());

        try
        {
            log.info(String.format("running %s localhost %d", launch.getAbsolutePath(), config.getGameServerPort()));
            ai.setProcess(pb.start());
        }
        catch(IOException e) {
            log.info(String.format("Error: cannot start AI %s : %s", launch.getAbsolutePath(), e));
            return false;
        }

        try
        {
            ai.setSocket(socket.accept());
        }
        catch(IOException e) {
            log.info(String.format("Error: %s", e));
            return false;
        }

        return true;
    }

    private void saveTurn(int matchId, int turnId, JsonNode state) throws SQLException
    {
        PreparedStatement ps = dbConnection.prepareStatement("INSERT INTO turn(state, turn, match) VALUES(?, ?, ?)");
        ps.setString(1, state.toString());
        ps.setInt(2, turnId);
        ps.setInt(3, matchId);
        ps.executeUpdate();
    }

    private MatchResult generateMatch(GameEngine gameEngine, int matchId, AI[] ais) throws SQLException
    {
        Integer winner = null;
        int currentPlayer = gameEngine.getCurrentPlayer();
        saveTurn(matchId, 0, gameEngine.getState());

        try
        {
            while(winner == null)
            {
                currentPlayer = gameEngine.getCurrentPlayer();
                Socket s = ais[currentPlayer].getSocket();

                // send state
                String gameState = gameEngine.getState() + "\n";
                s.getOutputStream().write(gameState.getBytes("ISO-8859-1"));

                // receive move
                String line = ais[currentPlayer].getBufferedReader().readLine();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode move = mapper.readTree(line);

                if(gameEngine.play(move))
                {
                    winner = gameEngine.getScore(0) > gameEngine.getScore(1) ? 0 : 1;
                }
                saveTurn(matchId, gameEngine.getCurrentTurn(), gameEngine.getState());
            }
        }
        catch(IOException | InvalidMoveException e) {
            log.info(String.format("Error: %s", e));
            return new MatchResult(currentPlayer == 0 ? 1 : 0, e);
        }

        return new MatchResult(winner.intValue(), null);
    }

    private void saveResult(GameEngine gameEngine, MatchResult result, int matchId) throws SQLException
    {
        PreparedStatement ps = dbConnection.prepareStatement("UPDATE match SET score1=?, score2=?, state=2, error=? WHERE id=?");
        ps.setInt(1, gameEngine.getScore(0));
        ps.setInt(2, gameEngine.getScore(1));

        if(result.getError() != null)
            ps.setString(3, result.getError().getMessage());
        else
            ps.setString(3, null);

        ps.setInt(4, matchId);
        ps.executeUpdate();
    }

    private void saveElo(int winner, AI[] ais) throws SQLException
    {
        if(ais[0].getId() == ais[1].getId())
            return;

        int d = ais[0].getElo() - ais[1].getElo();

        for(int i=0; i<=1; i++)
        {
            // count matches
            PreparedStatement ps = dbConnection.prepareStatement("SELECT COUNT(*) FROM match WHERE (ai1=? OR ai2=?) AND state=2");
            ps.setInt(1, ais[i].getId());
            ps.setInt(2, ais[i].getId());
            ResultSet count = ps.executeQuery();
            count.next();
            int nbMatches = count.getInt(1);

            // calculate elo point
            int score = winner == i ? 1 : 0;
            int elo = ais[i].getElo();
            int k = 10;

            if(nbMatches <= 30)
                k = 30;
            else if(elo <= 2400)
                k = 15;

            double p = 1.0 / (1.0 + Math.pow(10, - (double)d / 400.0));
            elo += (int)((double)k * ((double)score - p));
            d = -d;

            // update db
            ps = dbConnection.prepareStatement("UPDATE ai SET elo=? WHERE id=?");
            ps.setInt(1, elo);
            ps.setInt(2, ais[i].getId());
            ps.executeUpdate();
        }
    }

    public static InetAddress getLANAddress()
    {
        try
        {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements())
            {
                Enumeration<InetAddress> addresses = interfaces.nextElement().getInetAddresses();
                while (addresses.hasMoreElements())
                {
                    InetAddress addr = addresses.nextElement();
                    if(addr.getHostAddress().startsWith("192.168."))
                        return addr;
                }
            }
        }
        catch(SocketException e) {}

        return null;
    }
}
