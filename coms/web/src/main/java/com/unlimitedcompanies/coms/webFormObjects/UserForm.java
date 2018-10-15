package com.unlimitedcompanies.coms.webFormObjects;

import java.time.format.DateTimeFormatter;

import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.domain.security.exen.UserStatus;

public class UserForm
{
	private Integer userId;
	private String username;
	private String password1;
	private String password2;
	private String enabled;
	private String dateAdded;
	private String lastAccess;
	private String contactId;

	public UserForm() {}

	public UserForm(Integer userId, String username, String enabled, String dateAdded, String lastAccess, String contactId)
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
		this.enabled = user.getEnabledStatus().toString();
		this.dateAdded = user.getFullDateAdded().format(DateTimeFormatter.RFC_1123_DATE_TIME);
		this.lastAccess = user.getFullLastAccess().format(DateTimeFormatter.RFC_1123_DATE_TIME);
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
		return enabled;
	}

	public Integer getEnabledCode()
	{
		Integer code;
		if (this.enabled == null || this.enabled.isEmpty())
		{
			code = null;
		}
		else {
			if (this.enabled.equals("Inactive")) code = 0;
			else if (this.enabled.equals("Active")) code = 1;
			else if (this.enabled.equals("Denied")) code = 2;
			else code = null;
		}
		return code;
	}
	
	public UserStatus getEnabledStatus()
	{
		UserStatus status;
		if (this.enabled == null || this.enabled.isEmpty())
		{
			status = null;
		}
		else {
			if (this.enabled.equals("Inactive")) status = UserStatus.INACTIVE;
			else if (this.enabled.equals("Active")) status = UserStatus.ACTIVE;
			else if (this.enabled.equals("Denied")) status = UserStatus.DENIED;
			else status = null;
		}
		return status;
	}

	public void setEnabled(String status)
	{
		this.enabled = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
	}
	
	public void setEnabled(UserStatus status)
	{
		this.enabled = status.toString();
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
