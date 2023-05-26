package de.hackathon.gpidclient.dto;

import java.time.Instant;

public class Measurement
{
	Instant timestamp;
	double usage;
	
	
	public Measurement(Instant time, double use)
	{
		this.timestamp = time;
		this.usage = use;
	}

	
	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public double getUsage() {
		return usage;
	}

	public void setUsage(double usage) {
		this.usage = usage;
	}

	@Override
	public String toString()
	{
		long unixTimestamp = timestamp.getEpochSecond();
		return String.format("%s;%s", unixTimestamp, usage);
	}		
}