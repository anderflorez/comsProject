package com.unlimitedcompanies.coms.domain.security;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
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
	private char[] password;
	
	// status 0 - inactive
	// status 1 - active
	// status 2 - access denied
	@Column(name = "enabled")
	private UserStatus userStatus;
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
		this.contact = contact;
		this.userStatus = UserStatus.ACTIVE;
		this.dateAdded = ZonedDateTime.now(ZoneId.of("UTC"));
		this.lastAccess = ZonedDateTime.now(ZoneId.of("UTC"));
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
	
	public UserStatus getUserStatus()
	{
		return userStatus;
	}
	
	public Integer getUserStatusCode()
	{
		return userStatus.getStatusCode();
	}

	public void setUserStatus(UserStatus userStatus)
	{
		this.userStatus = userStatus;
	}
	
	public void setUserStatus(Integer userStatusCode)
	{
		this.userStatus = UserStatus.getNewUserStatus(userStatusCode);
	}

	public String getDateAdded()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");
		return this.dateAdded.format(formatter);
	}
	
	public String getClientLocalDateAdded()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");		
		return this.dateAdded.withZoneSameInstant(ZoneId.systemDefault()).format(formatter);
	}
	
	public ZonedDateTime getFullDateAdded()
	{
		return this.dateAdded;
	}
	
	public void setdateAdded(String dateTime)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
		ZonedDateTime accessTime = ZonedDateTime.parse(dateTime, formatter);
		this.lastAccess = accessTime;
	}
	
	public String getLastAccess()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");
		return this.lastAccess.format(formatter);
	}
	
	public String getClientLocalLastAccess()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a");
		return this.lastAccess.withZoneSameInstant(ZoneId.systemDefault()).format(formatter);
	}
	
	public ZonedDateTime getFullLastAccess()
	{
		return this.lastAccess;
	}
	
	public void setLastAccess(String dateTime)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");
		ZonedDateTime accessTime = ZonedDateTime.parse(dateTime, formatter);
		this.lastAccess = accessTime;
	}
	
	public void setLastAccess(ZonedDateTime lastAccess)
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
