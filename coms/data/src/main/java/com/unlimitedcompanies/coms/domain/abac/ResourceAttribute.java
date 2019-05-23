package com.unlimitedcompanies.coms.domain.abac;

public enum ResourceAttribute
{
	PROJECT_NAME("projectName"), 
	P_MANAGERS("projectManagers"), 
	P_SUPERINTENDENTS("superintendents"), 
	P_FOREMEN("foremen");
	
	private String field;
	
	private ResourceAttribute(String field)
	{
		this.field = field;
	}

	public String getField(String projectResourceName)
	{	
		if (projectResourceName != null)
		{
			return projectResourceName + "." + this.field;
		}
		else
		{
			return null;
		}
	}
}
