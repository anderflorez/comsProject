package com.unlimitedcompanies.coms.data.abac;

public enum ResourceAttribute
{
	PROJECTS("name"), 
	P_MANAGERS("manager"), 
	SUPERINTENDENTS("superintendent"), 
	FOREMEN("forman");
	
	private String projectField;
	
	private ResourceAttribute(String field)
	{
		this.projectField = field;
	}

	public String getProjectField()
	{		
//		if (resourceEntityName.equals("project"))
//		{
//			return "project." + projectField;	
//		}
//		else 
//		{
//			return resourceEntityName + ".project." + projectField;
//		}
		
		
		// New Version
		return this.projectField;
	}
}
