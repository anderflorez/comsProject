package com.unlimitedcompanies.coms.webFormObjects;

import com.unlimitedcompanies.coms.domain.security.User;

public class UserForm
{
	private Integer userId;
	private String username;
	private String password1;
	private String password2;
	private boolean enabled;
	private String dateAdded;
	private String lastAccess;
	private Integer contactId;

	public UserForm() {}

	public UserForm(Integer userId, String username, boolean enabled, String dateAdded, String lastAccess, Integer contactId)
	{
		this.userId = userId;
		this.username = username;
		this.enabled = enabled;
		this.dateAdded = dateAdded;
		this.lastAccess = lastAccess;
		this.contactId = contactId;
	}

	public UserForm(User user)
	{
		this.userId = user.getUserId();
		this.username = user.getUsername();
		this.enabled = user.isEnabled();
		this.dateAdded = user.getClientLocalDateAdded();
		this.lastAccess = user.getClientLocalLastAccess();
		this.contactId = user.getContact().getContactId();
	}

	public Integer getUserId()
	{
		return userId;
	}

	public void setUserId(Integer userId)
	{
		this.userId = userId;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword1()
	{
		return password1;
	}

	public void setPassword1(String password1)
	{
		this.password1 = password1;
	}

	public String getPassword2()
	{
		return password2;
	}

	public void setPassword2(String password2)
	{
		this.password2 = password2;
	}
	
	public boolean getEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean status)
	{
		this.enabled = status;
	}

	public String getDateAdded()
	{
		return dateAdded;
	}

	public void setDateAdded(String dateAdded)
	{
		this.dateAdded = dateAdded;
	}

	public String getLastAccess()
	{
		return lastAccess;
	}

	public void setLastAccess(String lastAccess)
	{
		this.lastAccess = lastAccess;
	}

	public Integer getContactId()
	{
		return contactId;
	}

	public void setContactId(Integer contactId)
	{
		this.contactId = contactId;
	}
	
}
