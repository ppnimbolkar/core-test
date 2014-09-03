package networking;

import java.io.*;
import java.net.*;
import java.util.*;

/**
* This is a multithreaded server that echo all client input.
*@version 1.15
*/

public class ServerThreaded
{
	public static void main(String[] args)
	{
		try
		{
			int i = 1;
			int port = 15999;
			ServerSocket s = new ServerSocket(port);
			
			while(true)
			{
				Socket incoming = s.accept();
				System.out.println("Spawning the number of client connected to the server running at "+ port " " + i);
				Runnable r = new ThreadedEchoHandler(incoming);
				Thread t = new Thread(r);
				t.start();
				i++;
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}

/**
*This class handles the client input for one server socket connection.
*/

class ThreadedEchoHandler implements Runnable
{
	/**
	 * Construct a handler
	 * @param i the incoming socket
	 * @param c the counter for the handlers (used in prompts)
	 */
	
	public ThreadedEchoHandler(Socket i)
	{
		incoming = i;
	}
	
	public void run()
	{
		try
		{
			try
			{
				InputStream inStream = incoming.getInputStream();
				OutputStream outStream = incoming.getOutputStream();
				
				Scanner in = new Scanner(inStream);
				PrintWriter out = new PrintWriter(outStream, true /* autoFlush */);
				
				out.println("Hey There! Enter BYE to EXIT.");
				
				//echo client input
				boolean done = false;
				while(!done && in.hasNextLine())
				{
					String line = in.nextLine();
					out.println("Echo: " + line);
					if(line.trim().equals("BYE"))
						done = true;
				}
			}
			finally
			{
				incoming.close();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private Socket incoming;
}
