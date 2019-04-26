package com.unlimitedcompanies.coms.data.abac;

public class PolicyResponse
{
	private boolean accessGranted;
	private String accessConditions;
	
	public PolicyResponse()
	{
		this.accessGranted = false;
		this.accessConditions = "";
	}

	public PolicyResponse(boolean accessGranted, String accessConditions)
	{
		this.accessGranted = accessGranted;
		this.accessConditions = accessConditions;
	}

	public boolean isAccessGranted()
	{
		return accessGranted;
	}

	public void setAccessGranted(boolean accessGranted)
	{
		this.accessGranted = accessGranted;
	}

	public String getAccessConditions()
	{
		return accessConditions;
	}

	public void setAccessConditions(String accessConditions)
	{
		this.accessConditions = accessConditions;
	}
	
	public void addAccessConditions(LogicOperator operator, String accessConditions)
	{
		this.accessConditions = this.accessConditions + " " + operator + " (" + accessConditions + ")";
	}
}
