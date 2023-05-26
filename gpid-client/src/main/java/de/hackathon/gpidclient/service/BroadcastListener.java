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
	boolean stopped;
	
	public BroadcastListener()
	{
		stopped = false;
	}
	
	public void run()
	{
		DatagramSocket socket = null;
		try
		{
			socket = new DatagramSocket(Constants.PORT_NUMBER);
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while(!stopped)
            {
                //Receive UDP packet
                socket.receive(packet);

                //Extract the data from the packet
                String message = new String(packet.getData(), 0, packet.getLength());
                InetAddress senderAddress = packet.getAddress();

                // Process the received data
                if(message.equals(Constants.SHOWME))
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
		Socket socket = null;
		try
		{
			socket = new Socket(senderAddress, port);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			//send description to server
			out.println(Constants.SYSDECRIPTION_LAPTOP);
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
	
	public void stopListeningBroadcast()
	{
		stopped = false;
	}
}