package networking;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.StyledDocument;

/**
* Server console proto.
*@version 1.15
*/

public class ServerConsole implements ActionListener
{
	public static void main(String[] args)
	{
		/*try
		{
			int i = 1;
			ServerSocket s = new ServerSocket(555);
			
			while(true)
			{
				Socket incoming = s.accept();
				System.out.println("Spawning " + i);
				Runnable r = new ThreadedEchoHandle(incoming);
				Thread t = new Thread(r);
				t.start();
				i++;
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}*/
		new ServerConsole();
	}
	
	public JFrame frame;
	public JTextPane console;
	public JButton clear;
	public JScrollPane scrollpane;
	public StyledDocument document;
	public PrintStream standardOut;
	
	
	public ServerConsole() 
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception ex){}
		
				
		
		frame = new JFrame();
		frame.setTitle("Server Console prototype 1.00");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		console = new JTextPane();
		console.setEditable(false);
		console.setFont(new Font("Courier New",Font.PLAIN,14));
		console.setOpaque(false);
		
		document = console.getStyledDocument();
		
		clear = new JButton("clear");
		
		
		scrollpane = new JScrollPane(console);
		scrollpane.setOpaque(false);
		scrollpane.getViewport().setOpaque(false);
		scrollpane.setBorder(null);
		
		frame.add(clear,BorderLayout.SOUTH);
		frame.add(scrollpane,BorderLayout.CENTER);
		frame.getContentPane().setBackground(new Color(50,50,50));
		
		frame.setSize(660,360);
		frame.setLocationRelativeTo(null);
		
		frame.setResizable(false);
		frame.setVisible(true);
		
		clear.addActionListener(this);
		
		//PrintStream printStream = new PrintStream(new CustomOutputStream(console));
		standardOut = System.out;
		//System.setOut(printStream);
		//System.setErr(printStream);
		
		
		
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource()==clear)
		{
			console.setText("");
		}
	}


}

/**
*This class handles the client input for one server socket connection.
*/

class ThreadedEchoHandle implements Runnable
{
	/**
	 * Construct a handler
	 * @param i the incoming socket
	 * @param c the counter for the handlers (used in prompts)
	 */
	
	public ThreadedEchoHandle(Socket i)
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