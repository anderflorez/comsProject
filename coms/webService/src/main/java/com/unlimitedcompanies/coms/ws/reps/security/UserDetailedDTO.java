package com.unlimitedcompanies.coms.ws.reps.security;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.security.User;

@XmlRootElement(name = "userDetailed")
public class UserDetailedDTO extends ResourceSupport
{
	private Integer userId;
	private String username;
	private boolean enabled;
	private String dateAdded;
	private String lastAccess;
	private Integer contactId;
	private String firstName;
	private String middleName;
	private String lastName;
	private String email;
	
	public UserDetailedDTO() 
	{
		this.enabled = false;
	}

	public UserDetailedDTO(User user)
	{
		this.userId = user.getUserId();
		this.username = user.getUsername();
		this.enabled = user.isEnabled();
		this.dateAdded = user.getClientLocalDateAdded();
		this.lastAccess = user.getClientLocalLastAccess();
		
		if (user.getContact() != null)
		{
			this.contactId = user.getContact().getContactId();
			this.firstName = user.getContact().getFirstName();
			this.middleName = user.getContact().getMiddleName();
			this.lastName = user.getContact().getLastName();
			this.email = user.getContact().getEmail();
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

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getMiddleName()
	{
		return middleName;
	}

	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

}
