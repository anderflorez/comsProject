package com.unlimitedcompanies.coms.ws.security.reps;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserRep
{
	private String username;
	private String userFirstName;
	private String userLastName;
	
	public UserRep()	{}

	public UserRep(String username, String userFirstName, String userLastName)
	{
		this.username = username;
		this.userFirstName = userFirstName;
		this.userLastName = userLastName;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getUserFirstName()
	{
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName)
	{
		this.userFirstName = userFirstName;
	}

	public String getUserLastName()
	{
		return userLastName;
	}

	public void setUserLastName(String userLastName)
	{
		this.userLastName = userLastName;
	}
	
}
