package backend;

import java.net.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class AI
{
    private int id;
    private String name;
    private String filename;
    private int elo;

    private Process process;
    private Socket socket;
    private BufferedReader bufferedReader;

    public AI(int id, String name, String filename, int elo)
    {
        this.id = id;
        this.name = name;
        this.filename = filename;
        this.elo = elo;

        this.process = null;
        this.socket = null;
        this.bufferedReader = null;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getFilename()
    {
        return filename;
    }

    public int getElo()
    {
        return elo;
    }

    public Process getProcess()
    {
        return process;
    }

    public Socket getSocket()
    {
        return socket;
    }

    public BufferedReader getBufferedReader()
    {
        return bufferedReader;
    }

    public void setProcess(Process process)
    {
        this.process = process;
    }

    public void setSocket(Socket socket) throws IOException
    {
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "ISO-8859-1"));
    }

    public void killProcess()
    {
        if(process != null)
            process.destroy();
    }

    public void closeSocket()
    {
        if(socket != null)
        {
            try {
                socket.close();
            }
            catch(IOException e) {}
        }
    }
}
