package com.unlimitedcompanies.coms.ws.security.reps;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Role;
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
	private Contact contact;
	private List<Role> roles;
	
	public UserDTO() 
	{
		this.password = null;
		this.enabled = false;
		this.roles = new ArrayList<>();
	}
	
	public UserDTO(User user) 
	{
		this.userId = user.getUserId();
		this.username = user.getUsername();
		this.password = null;
		this.enabled = user.isEnabled();
		this.dateAdded = user.getClientLocalDateAdded();
		this.lastAccess = user.getClientLocalLastAccess();
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

	public Contact getContact()
	{
		return contact;
	}

	public void setContact(Contact contact)
	{
		this.contact = contact;
	}

	@XmlElement(name = "role")
	public List<Role> getRoles()
	{
		return roles;
	}

	public void setRoles(List<Role> roles)
	{
		this.roles = roles;
	}
	
}
