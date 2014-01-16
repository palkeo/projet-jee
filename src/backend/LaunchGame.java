package backend;

import java.net.*;
import org.apache.commons.cli.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import backend.InvalidMoveException;

public class LaunchGame
{
    public static void main(String argv[])
    {
        try
        {
            CommandLineParser parser = new BasicParser();
            Options options = new Options();

            // options
            options.addOption("h", "help", false, "Print this message");
            options.addOption(null, "host", true, "The host of the game server");

            Option portOption = new Option(null, "port", true, "The port of the game server");
            portOption.setType(Number.class);
            options.addOption(portOption);

            CommandLine args = parser.parse(options, argv);

            if(args.hasOption("help"))
            {
                HelpFormatter formatter = new HelpFormatter();
                String header = "\nLaunch a game server.";
                formatter.printHelp("java LaunchGame <game>", header, options, null, true);
                return;
            }

            // required options
            options.getOption("port").setRequired(true);

            // because without that, it doesn't work..
            Options options2 = new Options();
            for(Object o : options.getOptions())
                options2.addOption((Option)o);

            args = parser.parse(options2, argv);

            String host = args.getOptionValue("host");
            int port = ((Number) args.getParsedOptionValue("port")).intValue();
            String game = args.getArgs()[0];

            // listening
            ServerSocket socket = null;

            if(host != null)
                socket = new ServerSocket(port, 0, InetAddress.getByName(host));
            else
                socket = new ServerSocket(port);

            System.out.println(String.format("Listening on %s:%d", host == null ? "localhost" : host, port));

            // instanciate game engine
            Class<?> gameEngineClass = Class.forName(game);
            GameEngine gameEngine = (GameEngine) gameEngineClass.newInstance();

            // waiting players
            Socket[] playerSockets = {null, null};
            BufferedReader[] playerReaders = {null, null};
            Integer winner = null;

            for(int i=0; i<=1; i++)
            {
                System.out.println(String.format("Waiting player %d", i+1));
                playerSockets[i] = socket.accept();
                playerReaders[i] = new BufferedReader(new InputStreamReader(playerSockets[i].getInputStream(), "ISO-8859-1"));
            }

            System.out.println("Launching match");
            JsonNode move = null;
            do
            {
                int currentPlayer = gameEngine.getCurrentPlayer();
                Socket s = playerSockets[currentPlayer];

                // send state
                String gameState = gameEngine.getState() + "\n";
                s.getOutputStream().write(gameState.getBytes("ISO-8859-1"));

                // receive move
                String line = playerReaders[currentPlayer].readLine();
                ObjectMapper mapper = new ObjectMapper();
                move = mapper.readTree(line);
            }
            while(!gameEngine.play(move));

            System.out.println("Match finished !");
            System.out.println(String.format("Player 1 : %d", gameEngine.getScore(0)));
            System.out.println(String.format("Player 2 : %d", gameEngine.getScore(1)));
        }
        catch(ParseException | IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvalidMoveException e) {
            System.out.println(e);
        }
    }
}
