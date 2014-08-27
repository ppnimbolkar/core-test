package networking;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This is for creating a socket connection to the time-c.nist.gov and prints the time that server sends.
 * @version 1.00
 */
public class BasicSocket
{
	
	public static void main(String[] args)
	{
		try
		{
			Socket s = new Socket("time-c.nist.gov", 13);
			try
			{
				InputStream inStream = s.getInputStream();
				Scanner in = new Scanner(inStream);
				
				while(in.hasNextLine())
				{
					String line = in.nextLine();
					System.out.println(line);
				}
			}
			finally
			{
				s.close();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
