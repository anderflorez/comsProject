package com.unlimitedcompanies.coms.domain.abac;

public class ResourceReadPolicy
{
	private boolean readGranted;
	private String readConditions;
	
	public ResourceReadPolicy()
	{
		this.readGranted = false;
		this.readConditions = null;
	}

	public boolean isReadGranted()
	{
		return readGranted;
	}

	public void setReadGranted(boolean readGranted)
	{
		this.readGranted = readGranted;
	}

	public String getReadConditions()
	{
		return readConditions;
	}

	public void setReadConditions(String readConditions)
	{
		this.readConditions = readConditions;
	}
	
	public void addReadConditions(LogicOperator operator, String readConditions)
	{
		this.readConditions = this.readConditions + " " + operator + " (" + readConditions + ")";
	}
}
