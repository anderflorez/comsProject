package com.unlimitedcompanies.coms.ws.security.reps;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")
public class UserDetailsRep
{
	String username;
	
	public UserDetailsRep()	{}

	public UserDetailsRep(String username)
	{
		this.username = username;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}
}
