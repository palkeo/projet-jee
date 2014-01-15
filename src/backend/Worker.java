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
                    + "ia1.id AS ia1_id, ia1.filename AS ia1_filename, ia1.name AS ia1_name, "
                    + "ia2.id AS ia2_id, ia2.filename AS ia2_filename, ia2.name AS ia2_name "
                    + "FROM match "
                    + "LEFT JOIN game ON game.id=match.game "
                    + "LEFT JOIN ia AS ia1 ON ia1.id=match.ia1 "
                    + "LEFT JOIN ia AS ia2 ON ia2.id=match.ia2 "
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

                log.info(String.format("%s -- %s vs %s", rs.getString("game_name"), rs.getString("ia1_name"), rs.getString("ia2_name")));

                // save match state (running) and worker
                ps = dbConnection.prepareStatement("UPDATE match SET state=1, worker=? WHERE id=?");
                ps.setString(1, String.format("%s:%d",
                    getLANAddress() == null ? socket.getInetAddress().getHostName() : getLANAddress().getHostName(),
                    socket.getLocalPort()));
                ps.setInt(2, matchId);
                ps.executeUpdate();

                // check for archives
                String[] iaFilenames = {rs.getString("ia1_filename"), rs.getString("ia2_filename")};

                for(String filename : iaFilenames)
                {
                    File f = new File(config.getPlayersDirectory() + filename);
                    if(!f.isFile())
                    {
                        log.info(String.format("Error: cannot find archive %s", config.getPlayersDirectory() + filename));
                        return;
                    }
                }

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
