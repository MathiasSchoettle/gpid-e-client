package de.hackathon.gpidclient.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import de.hackathon.gpidclient.config.Constants;

public class BroadcastListener extends Thread
{
	public BroadcastListener()
	{
		
	}
	
	public void run()
	{
		DatagramSocket socket = null;
		try
		{
			socket = new DatagramSocket(Constants.PORT_NUMBER);

            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            System.out.println("Waiting for UDP broadcast...");

            while (true)
            {
                // Receive UDP packet
                socket.receive(packet);

                // Extract the data from the packet
                String message = new String(packet.getData(), 0, packet.getLength());
                InetAddress senderAddress = packet.getAddress();
                int senderPort = packet.getPort();

                // Process the received data
                System.out.println("Received broadcast from " + senderAddress + ":" + senderPort);
                System.out.println("Message: " + message);
                
                if(message.equals("Show me who you are!"))
                {
                	//found a server!
                	answerServer(senderAddress, Constants.PORT_NUMBER);
                }
            }
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try { socket.close(); } catch (Exception e) { /* Ignored */ }
		}
		
	}
	
	public void answerServer(InetAddress senderAddress, int port)
	{
		//open TCP connection to server
		System.out.println(String.format("Send back answer to Server %s:%s", senderAddress, port));
		Socket socket = null;
		try
		{
			socket = new Socket(senderAddress, port);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			//send description to server
			String message = String.format("I bims ein: %s", "Laptop");
			out.println(message);
			System.out.println(String.format("Answer has been sent: %s", message));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try { socket.close(); } catch (Exception e) { /* Ignored */ }
		}
	}
}