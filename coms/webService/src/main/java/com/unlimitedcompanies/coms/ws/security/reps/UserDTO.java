package com.unlimitedcompanies.coms.ws.security.reps;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.security.User;

@XmlRootElement(name = "user")
public class UserDTO extends ResourceSupport
{
	private Integer userId;
	private String username;
	private char[] password;
	private boolean enabled;
	private String dateAdded;
	private String lastAccess;
	private Integer contactId;
	
	public UserDTO() 
	{
		this.password = null;
		this.enabled = false;
	}
	
	public UserDTO(User user) 
	{
		this.userId = user.getUserId();
		this.username = user.getUsername();
		this.password = null;
		this.enabled = user.isEnabled();
		this.dateAdded = user.getClientLocalDateAdded();
		this.lastAccess = user.getClientLocalLastAccess();
		if (user.getContact() != null)
		{
			this.contactId = user.getContact().getContactId();
		}
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

	public char[] getPassword()
	{
		return password;
	}

	public void setPassword(char[] password)
	{
		this.password = password;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
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

	public Integer getContactId()
	{
		return contactId;
	}

	public void setContactId(Integer contactId)
	{
		this.contactId = contactId;
	}
	
	public User getDomainUser()
	{
		User user = new User();
		user.setUserId(this.getUserId());
		user.setUsername(this.getUsername());
		user.setPassword(this.getPassword());
		user.setEnabled(this.isEnabled());
		user.setDateAdded(this.getDateAdded());
		user.setLastAccess(this.getLastAccess());
		
		return user;
	}
}
