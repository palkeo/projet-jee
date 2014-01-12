import java.util.logging.Logger;
import org.apache.commons.cli.ParseException;

public class Worker
{
    private Logger log;
    private WorkerConfig config;

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
        catch(ParseException e)
        {
            System.out.println(e);
        }
    }

    public Worker(WorkerConfig config)
    {
        this.log = Logger.getLogger("worker");
        this.config = config;
    }

    public void run()
    {
        log.info("Launching worker...");
    }
}
