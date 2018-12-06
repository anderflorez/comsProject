package com.unlimitedcompanies.coms.domain.security;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="user")
public class User
{
	@Id
	private Integer userId;
	private String username;
	private char[] password;
	private boolean enabled;
	// TODO: Test the methods related to the ZonedDateTime attributes
	private ZonedDateTime dateAdded;
	private ZonedDateTime lastAccess;
	
	@ManyToOne
	@JoinColumn(name="contact_FK")
	private Contact contact;
	
	@ManyToMany
	@JoinTable(name = "user_role", 
				joinColumns = {@JoinColumn(name = "user_FK")}, 
				inverseJoinColumns = {@JoinColumn(name = "role_FK")})
	private List<Role> roles = new ArrayList<>();
	
	public User() {}

	public User(String username, char[] password, Contact contact)
	{
		this.userId = null;
		this.username = username;
		this.password = password;
		this.enabled = true;
		this.contact = contact;
		this.dateAdded = ZonedDateTime.now(ZoneId.of("UTC"));
		this.lastAccess = ZonedDateTime.now(ZoneId.of("UTC"));
	}
	
	public User(Integer userId, String username, boolean enabled, ZonedDateTime dateAdded, ZonedDateTime lastAccess)
	{
		this.userId = userId;
		this.username = username;
		this.password = null;
		this.enabled = enabled;
		this.dateAdded = dateAdded;
		this.lastAccess = lastAccess;
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

	public ZonedDateTime getDateAdded()
	{
		return this.dateAdded;
	}
	
	public String getClientLocalDateAdded()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");		
		return this.dateAdded.withZoneSameInstant(ZoneId.systemDefault()).format(formatter);
	}
	
	public void setDateAdded(ZonedDateTime dateAdded)
	{
		this.dateAdded = dateAdded;
	}
	
	public void setDateAdded(String dateTime)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
		ZonedDateTime accessTime = ZonedDateTime.parse(dateTime, formatter);
		this.dateAdded = accessTime;
	}
	
	public ZonedDateTime getLastAccess()
	{
		return this.lastAccess;
	}

	public String getDBFormatedLastAccess()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");
		return this.lastAccess.format(formatter);
	}
	
	public String getClientLocalLastAccess()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a");
		return this.lastAccess.withZoneSameInstant(ZoneId.systemDefault()).format(formatter);
	}
	
	public void setLastAccess(ZonedDateTime lastAccess)
	{
		this.lastAccess = lastAccess;
	}
	
	public void setLastAccess(String dateTime)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");
		ZonedDateTime accessTime = ZonedDateTime.parse(dateTime, formatter);
		this.lastAccess = accessTime;
	}
	
	public Contact getContact()
	{
		return contact;
	}
	
	public void setContact(Contact contact)
	{
		this.contact = contact;
	}
	
	public List<Role> getRoles()
	{
		return Collections.unmodifiableList(this.roles);
	}
	
	public void addRole(Role role)
	{
		if (!roles.contains(role))
		{
			this.roles.add(role);
			role.addUser(this);
		}
	}
	
	public void removeRole(Role role)
	{
		this.roles.remove(role);
		role.getMembers().remove(this);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (username == null)
		{
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return this.username + " - " + contact.getFirstName() + " " + contact.getLastName();
	}
}
