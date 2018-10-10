package com.unlimitedcompanies.coms.webFormObjects;

import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.domain.security.exen.UserStatus;

public class UserForm
{
	private Integer userId;
	private String username;
	private String password1;
	private String password2;
	private UserStatus enabled;
	private String dateAdded;
	private String lastAccess;
	private String contactId;

	public UserForm() {}

	public UserForm(Integer userId, String username, UserStatus enabled, String dateAdded, String lastAccess, String contactId)
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
		this.enabled = user.getEnabledStatus();
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
	
	public String getEnabled()
	{
		if (this.enabled == null)
		{
			return "";
		}
		return enabled.toString();
	}

	public Integer getEnabledCode()
	{
		return enabled.getStatusCode();
	}
	
	public UserStatus getEnabledStatus()
	{
		return enabled;
	}

	public void setEnabled(String status)
	{
		int s = Integer.valueOf(status);
		this.enabled = UserStatus.getNewUserStatus(s);
	}
	
	public void setEnabled(UserStatus status)
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

	public String getContactId()
	{
		return contactId;
	}

	public void setContactId(String contactId)
	{
		this.contactId = contactId;
	}
	
}
