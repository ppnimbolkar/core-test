package networking;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.text.Document;
/**
 * 
 * Server Console proto 1.00
 *
 */



public class ServerConsole implements Runnable
{
    
	JTextArea console;
    BufferedReader buffer;
    

    private ServerConsole(JTextArea console, PipedOutputStream pout)
    {
        this.console = console;

        try
        {
            PipedInputStream pin = new PipedInputStream( pout );
            buffer = new BufferedReader( new InputStreamReader(pin) );
        }
        catch(IOException e) {}
        
        
    }

    public void run()
    {
        String line = null;

        try
        {
            while ((line = buffer.readLine()) != null)
            {
//              displayPane.replaceSelection( line + "\n" );
                console.append( line + "\n" );
                console.setCaretPosition( console.getDocument().getLength() );
            }

            System.err.println("im here");
        }
        catch (IOException ioe)
        {
            JOptionPane.showMessageDialog(null,"Error redirecting output : "+ioe.getMessage());
        }
    }
    
    

    public static void redirectOutput(JTextArea console)
    {
        ServerConsole.redirectOut(console);
        ServerConsole.redirectErr(console);
    }

    public static void redirectOut(JTextArea console)
    {
        PipedOutputStream pos = new PipedOutputStream();
        System.setOut( new PrintStream(pos, true) );

        ServerConsole serverconsole = new ServerConsole(console, pos);
        new Thread(serverconsole).start();
    }

    public static void redirectErr(JTextArea console)
    {
        PipedOutputStream pos = new PipedOutputStream();
        System.setErr( new PrintStream(pos, true) );

        ServerConsole serverconsole = new ServerConsole(console, pos);
        new Thread(serverconsole).start();
    }

    public static void main(String[] args)
    {
    	JFrame frame;
    	final JTextArea console;
    	final JButton clear;
    	JScrollPane scrollpane;
    	Document document;
    	
    	try
    	{
    		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	}
    	catch(Exception ex){}
    	
    	
    	frame = new JFrame();
		frame.setTitle("Server Console prototype 1.00");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//for textarea as console
		console = new JTextArea();		
		console.setEditable(false);
		console.setForeground(Color.GREEN);
		console.setFont(new Font("Courier New",Font.PLAIN,16));
		console.setOpaque(true);
		console.setBackground(Color.BLACK);
		document = console.getDocument();
		
		clear = new JButton("clear");
		
		
		scrollpane = new JScrollPane(console);
		scrollpane.setOpaque(false);
		scrollpane.getViewport().setOpaque(false);
		scrollpane.setBorder(null);
		
		frame.add(clear,BorderLayout.SOUTH);
		frame.add(scrollpane,BorderLayout.CENTER);
		frame.setSize(460,360);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		
		clear.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				if(event.getSource()==clear)
				{
					console.setText("");
				}
			}
			
		}
		);
	
        ServerConsole.redirectOutput( console );
        
        try
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
		}

    }
}

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
