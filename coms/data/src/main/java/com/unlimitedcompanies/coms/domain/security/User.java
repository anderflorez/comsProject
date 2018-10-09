package com.unlimitedcompanies.coms.domain.security;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
	private String password;
	private byte enabled;
	private Date dateAdded;
	private Date lastAccess;
	
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
		this.enabled = 1;
		this.dateAdded = new Date();
		this.lastAccess = this.dateAdded;
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

	public byte getEnabled()
	{
		return enabled;
	}

	public String getDateAdded()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MMMMM dd, yyyy");
		return sdf.format(this.dateAdded);
	}
	
	public String getDbDateAdded()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd");
		return dateFormat.format(this.dateAdded);
	}
	
	public Date getFullDateAdded()
	{
		return this.dateAdded;
	}

	public String getLastAccess()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MMMMM dd, yyyy 'at' HH:mm:ss z");
		return sdf.format(this.lastAccess);
	}
	
	public String getDbLastAccess()
	{
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
		return dateTimeFormat.format(this.lastAccess);
	}
	
	public Date getFullLastAccessDate()
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

	public void setEnabled(byte enabled)
	{
		this.enabled = enabled;
	}
	
	public void setDateAdded()
	{
		// Method Not Allowed
	}

	public void setLastAccess() 
	{
		// Method Not Allowed
	}
	
	public void setLastAccess(Date lastAccess)
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
