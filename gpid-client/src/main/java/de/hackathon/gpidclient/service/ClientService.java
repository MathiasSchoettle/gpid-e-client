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
			
			if(System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0)
			{
				powerusage = powerusage/1000000000;	//as the library is intended for Android, the values are shit for linux
			}
			
			powerusage = 263647848.37;
			//löppt immer noch nicht
			if(powerusage > 1000)
			{
				//take first 3 digits and divide by 100
				// Convert the double value to a string
				String numberString = Double.toString(powerusage);

				numberString = numberString.replace(".", "");
				// Extract the first three digits
				String firstThreeDigits = numberString.substring(0, 3);

				// Concatenate the first digit with a decimal point and the remaining digits
				String result = firstThreeDigits.charAt(0) + "." + firstThreeDigits.substring(1);

				// Parse the result back to a double if needed
				powerusage = Double.parseDouble(result);
			}
			
			//add up all values
			usage += powerusage;
		}

		Measurement measurement = new Measurement(time, usage);
		
		//limit size of list to 10 or so, idk
		if(measurements.size() >= Constants.MAX_SIZE_REPORTLIST)
		{
			//just remove first, should work
			measurements.remove(0);
		}
		
		this.measurements.add(measurement);
		System.out.println("power measurement done.");
		System.out.println(String.format("measurement: %s", measurement));
		System.out.println(String.format("measurement list size: %s", measurements.size()));
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