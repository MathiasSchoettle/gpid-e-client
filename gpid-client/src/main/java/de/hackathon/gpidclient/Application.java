package de.hackathon.gpidclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import de.hackathon.gpidclient.config.Constants;
import de.hackathon.gpidclient.dto.Measurement;
import de.hackathon.gpidclient.service.BroadcastListener;
import de.hackathon.gpidclient.service.ClientService;
import de.hackathon.gpidclient.service.TCPListener;

public class Application
{
	static ClientService clientService;
	
	static BroadcastListener broadcastListener;
	
	static TCPListener tcpListener;
	
    public static void main(String[] args)
    {
    	//erstmal a zünftigs MOINSEN in die Runde
    	sendInitialBroadcastMessage();
    	   
    	//start collection in a Thread
    	clientService = new ClientService(); 
    	clientService.start();
    	
    	//also listen for broadcasts
    	broadcastListener = new BroadcastListener(); 
    	broadcastListener.start();

    	//listen for the TCP calls
    	tcpListener = new TCPListener(clientService); 
    	tcpListener.start();
    }
    
    //https://www.baeldung.com/java-broadcast-multicast
    public static void sendInitialBroadcastMessage()
    {
    	System.out.println("send broadcast greeting...");
    	sendBroadcast(Constants.BROADCAST_GREETING);
    	System.out.println("broadcast greeting has been sent");
    }
    
    public static void sendBroadcast(String message)
    {
    	System.out.println("send broadcast...");
    	try
    	{
    		DatagramSocket socket = new DatagramSocket();
        	socket.setBroadcast(true);
        	
        	byte[] buffer = message.getBytes();
        	
        	InetAddress address = InetAddress.getByName("192.168.3.255");
        	DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, Constants.PORT_NUMBER);
        	socket.send(packet);
            socket.close();
            System.out.println("broadcast has been sent");
    	}
    	catch(SocketException ex)
    	{
    		ex.printStackTrace();
    	}
    	catch(UnknownHostException ex)
    	{
    		ex.printStackTrace();
    	}
    	catch(IOException ex)
    	{
    		ex.printStackTrace();
    	}
    }
    
    public static void listen()
    {
    	System.out.println(String.format("Listening on port %s", Constants.PORT_NUMBER));
    	ServerSocket serverSocket = null;
    	Socket clientSocket = null;
    	
    	try
    	{
    		serverSocket = new ServerSocket(Constants.PORT_NUMBER);
			clientSocket = serverSocket.accept();
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);    		        
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    		String fromServer = null;    		
    		while ((fromServer = in.readLine()) != null)
    		{
    			System.out.println(fromServer);
    				
    		    if (fromServer.equals("COMMAND:SEND_DATATYPE"))
    		    {
    		    	//answer looks smth like this
    		    	//[1685118001;9.488, 1685118061;6.464]
    		    	List<Measurement> measurements = clientService.getMeasurements();
        			out.println(measurements);
    		    	

    		    	clientService.clearMeasurements();
    		    	System.out.println(String.format("tried to send %s", measurements));
    		    }   		       		   
    		    if (fromServer.equals("Bye.")) break;
    		}
    	}
    	catch(IOException ex)
    	{
			ex.printStackTrace();
		}
    	finally 
		{
	    try { serverSocket.close(); } catch (Exception e) { /* Ignored */ }
	    try { clientSocket.close(); } catch (Exception e) { /* Ignored */ }
		}
    }
    
    public static void test()
    {
    	System.out.println("test");
    	List<Measurement> measurements = clientService.getMeasurements();
		
    	
    	
    	Socket socket = null;
		try
		{
			InetAddress senderAddress = InetAddress.getByName("192.168.3.22");
			socket = new Socket(senderAddress, 42069);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()), 1024);
			
			//send description to server
			String message = String.format("I bims ein: %s", "Laptop");
			out.println(measurements);
			System.out.println(String.format("tried to send %s", measurements));
			
			String fromServer = null; 
			while ((fromServer = in.readLine()) != null)
    		{
    			System.out.println(fromServer);
    			out.println(measurements);
    		    System.out.println(String.format("tried to send %s", measurements));
    		}
		}
		catch(IOException e)
		{
			System.out.println("test failed!");
			e.printStackTrace();
		}
		finally
		{
			try { socket.close(); } catch (Exception e) { /* Ignored */ }
		}
    }
}