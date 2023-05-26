package de.hackathon.gpidclient.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import de.hackathon.gpidclient.config.Constants;
import de.hackathon.gpidclient.dto.Measurement;

public class TCPListener extends Thread
{
	
	ClientService clientService;
	
	public TCPListener(ClientService clientService)
	{
		this.clientService = clientService;
	}
	
	public void run()
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
}