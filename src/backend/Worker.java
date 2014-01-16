package backend;

import java.util.Enumeration;
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
        catch(ParseException e) {
            System.out.println(e);
        }
        catch(SQLException e) {
            System.out.println(e);
        }
        catch(JMSException e) {
            System.out.println(e);
        }
        catch(UnknownHostException e) {
            System.out.println(e);
        }
        catch(IOException e) {
            System.out.println(e);
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

    public void run() throws SQLException, JMSException
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

                log.info(String.format("%s -- %s vs %s", rs.getString("game_name"), rs.getString("ai1_name"), rs.getString("ai2_name")));

                // save match state (running) and worker
                ps = dbConnection.prepareStatement("UPDATE match SET state=1, worker=? WHERE id=?");
                ps.setString(1, String.format("%s:%d",
                    getLANAddress() == null ? socket.getInetAddress().getHostName() : getLANAddress().getHostName(),
                    socket.getLocalPort()));
                ps.setInt(2, matchId);
                ps.executeUpdate();

                String[] aiFilenames = {rs.getString("ai1_filename"), rs.getString("ai2_filename")};

                // check and extract archives
                for(String filename : aiFilenames)
                {
                    String archive = config.getPlayersDirectory() + filename;
                    String archiveExtractDir = config.getExtractDirectory() + filename;
                    String aiDirectory = config.getExtractDirectory() + FilenameUtils.removeExtension(FilenameUtils.removeExtension(filename));

                    // check archive
                    File archiveFile = new File(archive);
                    if(!archiveFile.isFile())
                    {
                        log.info(String.format("Error: cannot find archive %s", archive));
                        return;
                    }

                    // copy to extract directory
                    File archiveExtractDirFile = new File(archiveExtractDir);
                    try
                    {
                        FileUtils.copyFile(archiveFile, archiveExtractDirFile);
                    }
                    catch(IOException e) {
                        log.info(String.format("Error: cannot copy archive %s to %s : %s", archive, archiveExtractDir, e));
                        return;
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
                            p = pb.start();
                            p.waitFor();
                        }
                        catch(IOException e) {
                            log.info(String.format("Error: cannot extract archive %s : %s", archive, e));
                            return;
                        }
                        catch(InterruptedException e) {
                            log.info(String.format("Error: cannot extract archive %s : %s", archive, e));
                            return;
                        }

                        if(p.exitValue() != 0)
                        {
                            log.info(String.format("Error: cannot extract archive %s (tar returned error code : %d)", archive, p.exitValue()));
                            return;
                        }
                    }
                }

                // instanciate game engine
                Class<?> gameEngineClass = null;

                try
                {
                    gameEngineClass = Class.forName(rs.getString("class_name"));
                }
                catch(ClassNotFoundException e) {
                    log.info(String.format("Error: %s", e));
                    return;
                }

                GameEngine gameEngine = null;

                try
                {
                    gameEngine = (GameEngine) gameEngineClass.newInstance();
                }
                catch(InstantiationException e) {
                    log.info(String.format("Error: cannot instanciate game engine : %s", e));
                    return;
                }
                catch(IllegalAccessException e) {
                    log.info(String.format("Error: cannot instanciate game engine : %s", e));
                    return;
                }

                // launch IAs

                // TODO : finish..
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
