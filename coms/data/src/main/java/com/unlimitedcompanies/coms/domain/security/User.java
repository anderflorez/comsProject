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

import com.unlimitedcompanies.coms.domain.security.exen.UserStatus;

@Entity
@Table(name="user")
public class User
{
	@Id
	private Integer userId;
	private String username;
	private String password;
	
	// status 0 - inactive
	// status 1 - active
	// status 2 - access denied
	private UserStatus enabled;
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

	public User(String username, String password, Contact contact)
	{
		this.userId = null;
		this.username = username;
		this.password = password;
		this.contact = contact;
		this.enabled = UserStatus.ACTIVE;
		this.dateAdded = ZonedDateTime.now(ZoneId.of("America/New_York"));
		this.lastAccess = ZonedDateTime.now(ZoneId.of("America/New_York"));
	}

	public Integer getUserId()
	{
		return userId;
	}

	public String getUsername()
	{
		return username;
	}

	public String getPassword()
	{
		return password;
	}

	public Integer getEnabled()
	{
		return enabled.getStatusCode();
	}
	
	public UserStatus getEnabledStatus()
	{
		return enabled;
	}

	public String getDBDateAdded()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
		return this.dateAdded.format(formatter);
	}
	
	public String getFormatDateAdded()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
		return this.dateAdded.format(formatter);
	}
	
	public ZonedDateTime getFullDateAdded()
	{
		return this.dateAdded;
	}
	
	public String getDBLastAccess()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");
		return this.lastAccess.format(formatter);
	}
	
	public String getFormatLastAccess()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a");
		return this.lastAccess.format(formatter);
	}

	public ZonedDateTime getFullLastAccess()
	{
		return this.lastAccess;
	}

	public Contact getContact()
	{
		return contact;
	}
	
	public List<Role> getRoles()
	{
		return Collections.unmodifiableList(this.roles);
	}
	
	public void setUserId(Integer userId)
	{
		this.userId = userId;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public void setEnabled(Integer status)
	{
		this.enabled = UserStatus.getNewUserStatus(status);
	}
	
	public void setEnabled(UserStatus status)
	{
		this.enabled = status;
	}
	
	public void setLastAccess(ZonedDateTime lastAccess)
	{
		this.lastAccess = lastAccess;
	}
	
	public void setContact(Contact contact)
	{
		this.contact = contact;
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
