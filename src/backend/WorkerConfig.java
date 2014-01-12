import org.apache.commons.cli.*;

public class WorkerConfig
{
    private String databaseUrl;
    private String databaseLogin;
    private String databasePassword;

    public WorkerConfig(String dbUrl, String dbLogin, String dbPassword)
    {
        this.databaseUrl = dbUrl;
        this.databaseLogin = dbLogin;
        this.databasePassword = dbPassword;
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

        options.addOption("h", "help", false, "Print this message");
        options.addOption(null, "db-url", true, "The database url for JDBC, like jdbc:postgresql://hostname:port/dbname");
        options.addOption(null, "db-login", true, "The database login");
        options.addOption(null, "db-password", true, "The database password");

        CommandLine args = parser.parse(options, argv);

        if(args.hasOption("help"))
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java Worker", options, true);
            return null;
        }

        System.out.println(args.getOptionValue("db-url"));

        return null;
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
}
