package backend;
import java.sql.*;
import java.util.logging.Logger;
import org.apache.commons.cli.ParseException;

public class Worker
{
    private Logger log;
    private WorkerConfig config;
    private Connection con;

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
    }

    public Worker(WorkerConfig config) throws SQLException
    {
        this.log = Logger.getLogger("worker");
        this.config = config;

        // connection to database
        con = DriverManager.getConnection(config.getDatabaseUrl(), config.getDatabaseLogin(), config.getDatabasePassword());
    }

    public void run() throws SQLException
    {
        log.info("Launching worker...");

        try
        {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM test");

            while(rs.next())
            {
                for(int i=1; i <= rs.getMetaData().getColumnCount(); i++)
                {
                    System.out.println("column " + i + " = " + rs.getObject(i));
                }
            }
        }
        finally {
            try {
                con.close();
            }
            catch(Throwable ignore) {}
        }

        log.info("Exiting...");
    }
}
