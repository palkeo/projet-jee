package backend;
import org.apache.commons.cli.*;
import java.util.Collection;
import org.apache.activemq.ActiveMQConnection;

public class WorkerConfig
{
    private String databaseUrl;
    private String databaseLogin;
    private String databasePassword;
    private String playersDirectory;
    private String extractDirectory;
    private String gameServerHost;
    private int gameServerPort;
    private String jmsUrl;
    private String jmsLogin;
    private String jmsPassword;

    public WorkerConfig(String dbUrl, String dbLogin, String dbPassword, String playersDirectory, String extractDirectory, String gameServerHost, int gameServerPort, String jmsUrl, String jmsLogin, String jmsPassword)
    {
        assert(dbUrl != null);
        assert(playersDirectory != null);
        assert(extractDirectory != null);
        this.databaseUrl = dbUrl;
        this.databaseLogin = dbLogin;
        this.databasePassword = dbPassword;
        this.playersDirectory = playersDirectory;
        this.extractDirectory = extractDirectory;
        this.gameServerHost = gameServerHost;
        this.gameServerPort = gameServerPort;

        if(jmsUrl == null)
            this.jmsUrl = ActiveMQConnection.DEFAULT_BROKER_URL;
        else
            this.jmsUrl = jmsUrl;

        this.jmsLogin = jmsLogin;
        this.jmsPassword = jmsPassword;
    }

    /**
     * Parse command line
     *
     * @param String[] a list of arguments
     * @return WorkerConfig a WorkerConfig, or null if there is no need to launch a worker (-h option)
     */
    public static WorkerConfig fromCommandLine(String argv[]) throws ParseException
    {
        CommandLineParser parser = new BasicParser();
        Options options = new Options();

        // options
        options.addOption("h", "help", false, "Print this message");
        options.addOption(null, "db-url", true, "The database url for JDBC, like jdbc:postgresql://hostname:port/dbname");
        options.addOption(null, "db-login", true, "The database login");
        options.addOption(null, "db-password", true, "The database password");
        options.addOption(null, "players-dir", true, "The directory where are the players");
        options.addOption(null, "extract-dir", true, "The directory where player archives are extracted");
        options.addOption(null, "gameserver-host", true, "The host of the game server");

        Option port = new Option(null, "gameserver-port", true, "The port of the game server");
        port.setType(Integer.class);
        options.addOption(port);

        options.addOption(null, "jms-url", true, "The url of the jms server");
        options.addOption(null, "jms-login", true, "The login for the jms server");
        options.addOption(null, "jms-password", true, "The password for the jms server");

        CommandLine args = parser.parse(options, argv);

        if(args.hasOption("help"))
        {
            HelpFormatter formatter = new HelpFormatter();
            String header = "\nLaunch a worker.";
            formatter.printHelp("java Worker", header, options, null, true);
            return null;
        }

        // required options
        options.getOption("db-url").setRequired(true);
        options.getOption("gameserver-port").setRequired(true);
        options.getOption("players-dir").setRequired(true);
        options.getOption("extract-dir").setRequired(true);

        // because without that, it doesn't work..
        Options options2 = new Options();
        for(Object o : options.getOptions())
            options2.addOption((Option)o);

        args = parser.parse(options2, argv);

        return new WorkerConfig(
            args.getOptionValue("db-url"),
            args.getOptionValue("db-login"),
            args.getOptionValue("db-password"),
            args.getOptionValue("players-dir"),
            args.getOptionValue("extract-dir"),
            args.getOptionValue("gameserver-host"),
            ((Integer) args.getParsedOptionValue("gameserver-port")).intValue(),
            args.getOptionValue("jms-url"),
            args.getOptionValue("jms-login"),
            args.getOptionValue("jms-password")
        );
    }

    public String getDatabaseUrl()
    {
        return databaseUrl;
    }

    public String getDatabaseLogin()
    {
        return databaseLogin;
    }

    public String getDatabasePassword()
    {
        return databasePassword;
    }

    public String getPlayersDirectory()
    {
        return playersDirectory.endsWith("/") ? playersDirectory : playersDirectory + "/";
    }

    public String getExtractDirectory()
    {
        return extractDirectory.endsWith("/") ? extractDirectory : extractDirectory + "/";
    }

    public String getGameServerHost()
    {
        return gameServerHost;
    }

    public int getGameServerPort()
    {
        return gameServerPort;
    }

    public String getJmsUrl()
    {
        return jmsUrl;
    }

    public String getJmsLogin()
    {
        return jmsLogin;
    }

    public String getJmsPassword()
    {
        return jmsPassword;
    }
}
