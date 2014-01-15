package backend;

import java.util.logging.Logger;
import org.apache.commons.cli.ParseException;
import java.sql.*;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Worker
{
    private Logger log;
    private WorkerConfig config;
    private java.sql.Connection dbConnection;
    private QueueConnection jmsConnection;
    private QueueReceiver jmsReceiver;

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
    }

    public Worker(WorkerConfig config) throws SQLException, JMSException
    {
        this.log = Logger.getLogger("worker");
        this.config = config;

        // connection to database
        log.info("Connection to database...");
        dbConnection = DriverManager.getConnection(config.getDatabaseUrl(), config.getDatabaseLogin(), config.getDatabasePassword());

        // connection to jms server
        log.info("Connection to jms server...");
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
    }

    public void run() throws SQLException, JMSException
    {
        log.info("Running worker...");

        try
        {
            while(true)
            {
                log.info("Waiting match...");
                System.out.println(jmsReceiver.receive());
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
}
