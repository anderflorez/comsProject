package com.unlimitedcompanies.coms.webFormObjects;

import com.unlimitedcompanies.coms.domain.security.User;

public class UserForm
{
	private Integer userId;
	private String username;
	private String password1;
	private String password2;
	private byte enabled;
	private String dateAdded;
	private String lastAccess;
	private int contactId;

	public UserForm() {}

	public UserForm(Integer userId, String username, byte enabled, String dateAdded, String lastAccess, int contactId)
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
		this.enabled = user.getEnabled();
		this.dateAdded = user.getDateAdded();
		this.lastAccess = user.getLastAccess();
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

	public byte getEnabled()
	{
		return enabled;
	}

	public void setEnabled(byte enabled)
	{
		this.enabled = enabled;
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

	public int getContactId()
	{
		return contactId;
	}

	public void setContactId(int contactId)
	{
		this.contactId = contactId;
	}
	
}
