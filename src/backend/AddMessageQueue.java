package backend;

import org.apache.commons.cli.*;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class AddMessageQueue
{
    public static void main(String argv[])
    {
        try
        {
            CommandLineParser parser = new BasicParser();
            Options options = new Options();

            // options
            options.addOption("h", "help", false, "Print this message");
            options.addOption(null, "jms-url", true, "The url of the jms server");
            options.addOption(null, "jms-login", true, "The login for the jms server");
            options.addOption(null, "jms-password", true, "The password for the jms server");

            CommandLine args = parser.parse(options, argv);

            if(args.hasOption("help"))
            {
                HelpFormatter formatter = new HelpFormatter();
                String header = "\nAdd a message to the JMS Queue.";
                formatter.printHelp("java AddMessageQueue <message..>", header, options, null, true);
                return;
            }

            String url = args.getOptionValue("jms-url", ActiveMQConnection.DEFAULT_BROKER_URL);
            String login = args.getOptionValue("jms-login");
            String password = args.getOptionValue("jms-password");
            ActiveMQConnectionFactory factory;
           
            if(login != null)
               factory = new ActiveMQConnectionFactory(login, password, url);
            else
               factory = new ActiveMQConnectionFactory(url);

            QueueConnection connection = factory.createQueueConnection();
            connection.start();
            QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("Matches");
            QueueSender sender = session.createSender(queue);

            for(String message : args.getArgs())
            {
                sender.send(session.createTextMessage(message));
            }

            connection.close();
        }
        catch(ParseException e) {
            System.out.println(e);
        }
        catch(JMSException e) {
            System.out.println(e);
        }
    }
}
