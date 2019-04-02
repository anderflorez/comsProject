package com.unlimitedcompanies.coms.domain.security;

import java.util.List;

public class UserAttributes
{
	private User user;
	
	public UserAttributes(User userWithAttributes)
	{
		this.user = userWithAttributes;		
	}
	
	public String getUsername()
	{
		return user.getUsername();
	}
	
	public List<Role> getRoles()
	{
		return user.getRoles();
	}
}
