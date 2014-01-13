package backend;
import org.apache.commons.cli.*;
import java.util.Collection;

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

        // options
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

        // required options
        options.getOption("db-url").setRequired(true);

        // because without that, it doesn't work..
        Options options2 = new Options();
        for(Object o : options.getOptions())
            options2.addOption((Option)o);

        args = parser.parse(options2, argv);

        return new WorkerConfig(
            args.getOptionValue("db-url"),
            args.getOptionValue("db-login"),
            args.getOptionValue("db-password")
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
}
