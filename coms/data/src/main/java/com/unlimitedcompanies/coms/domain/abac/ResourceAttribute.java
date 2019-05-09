package com.unlimitedcompanies.coms.domain.abac;

public enum ResourceAttribute
{
	PROJECT_NAME("projectName"), 
	P_MANAGERS("projectManagers"), 
	P_SUPERINTENDENTS("superintendents"), 
	P_FOREMEN("foremen"),
	USERNAME("username"),
	ROLE("roleName");
	
	private String field;
	
	private ResourceAttribute(String field)
	{
		this.field = field;
	}

	public String getField(String projectResourceName, String userResourceName)
	{	
		if (userResourceName != null && (this.equals(ROLE) || this.equals(USERNAME)))
		{
			return userResourceName + "." + this.field;
		}
		else if (projectResourceName != null)
		{
			return projectResourceName + "." + this.field;
		}
		else
		{
			return null;
		}
	}
}
