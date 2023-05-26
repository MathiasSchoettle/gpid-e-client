package de.hackathon.gpidclient.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hackathon.gpidclient.config.Constants;
import de.hackathon.gpidclient.dto.Measurement;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PowerSource;

public class ClientService extends Thread
{

	List<Measurement> measurements;
	
	public ClientService()
	{
		this.measurements = new ArrayList<Measurement>();
	}
	
	public void run()
	{
    	//create a Timer which runs the routine in a given interval		
    	Timer t = new Timer();

    	t.scheduleAtFixedRate(
    			new TimerTask()
    			{
    			    public void run()
    			    {
    			    	collectData();
    			    }
    			},
    			100,						//run first after 5 seconds
    			Constants.CLIENT_INTERVAL);	//run every minute
	}
	
	public void collectData()
	{
		System.out.println("performing power measurement...");
		Instant time = Instant.now();
		double usage =  0.0;

		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();
		List<PowerSource> ps = hal.getPowerSources();

		for(PowerSource ps1 : ps)
		{
			double powerusage = ps1.getPowerUsageRate();
			
			//powerusage is a value in mW, so we divide by 1000
			//the value can be negative if we use the battery, if it gets charged its positive, so we take the absolute			
			//if the battery is full and plugged in, we get 0.0
			powerusage = Math.abs(powerusage/1000);
			
			//add up all values
			usage += powerusage;
		}

		Measurement measurement = new Measurement(time, usage);
		this.measurements.add(measurement);
		System.out.println("power measurement done.");
		System.out.println(String.format("measurement: %s", measurement));
	}
	
	public List<Measurement> getMeasurements()
	{
		return measurements;
	}
	
	public void clearMeasurements()
	{
		this.measurements = new ArrayList<Measurement>();
	}
}