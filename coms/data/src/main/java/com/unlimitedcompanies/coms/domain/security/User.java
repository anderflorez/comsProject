package com.unlimitedcompanies.coms.domain.security;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name="users")
public class User
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;
	
	private String username;
	
	private char[] password;
	
	private Boolean enabled;
	private ZonedDateTime dateAdded;
	private ZonedDateTime lastAccess;
	
	@OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	@JoinColumn(name = "contactId_FK")
	private Contact contact;
	
	@ManyToMany
	@JoinTable(name = "users_roles", 
				joinColumns = {@JoinColumn(name = "userId_FK")}, 
				inverseJoinColumns = {@JoinColumn(name = "roleId_FK")})
	private Set<Role> roles = new HashSet<>();
	
	protected User() {}

	public User(String username, String password, Contact contact)
	{
		PasswordEncoder pe = new BCryptPasswordEncoder();
		this.password = pe.encode(password).toCharArray();
		
		this.username = username;
		this.enabled = true;
		this.contact = contact;
		if (contact.getUser() == null || !contact.getUser().equals(this))
		{
			contact.setUser(this);
		}
		this.dateAdded = ZonedDateTime.now(ZoneId.of("UTC"));
		this.lastAccess = ZonedDateTime.now(ZoneId.of("UTC"));
	}
	
	public Integer getUserId()
	{
		return userId;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}	
	
	public boolean isPassword(String password)
	{
		PasswordEncoder pe = new BCryptPasswordEncoder();
		String encodedPassword = "";
		for(char next : this.password)
		{
			encodedPassword += next;
		}
		return pe.matches(password, encodedPassword);
	}

	public void setPassword(String password)
	{
		PasswordEncoder pe = new BCryptPasswordEncoder();
		this.password = pe.encode(password).toCharArray();
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
	
//	private void setDateAdded(ZonedDateTime dateAdded)
//	{
//		this.dateAdded = dateAdded;
//	}
	
//	private void setDateAdded(String dateTime)
//	{
//		if (dateTime != null)
//		{
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
//			ZonedDateTime accessTime = ZonedDateTime.parse(dateTime, formatter);
//			this.dateAdded = accessTime;
//		}
//	}
	
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
		this.lastAccess = lastAccess.withZoneSameInstant(ZoneId.of("UTC"));
	}
	
//	private void setLastAccess(String dateTime)
//	{
//		if (dateTime != null)
//		{
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");
//			ZonedDateTime accessTime = ZonedDateTime.parse(dateTime, formatter);
//			this.lastAccess = accessTime;
//		}
//	}
	
	public Contact getContact()
	{
		return contact;
	}
	
	public void setContact(Contact contact)
	{
		this.contact = contact;
		if (contact.getUser() == null || !contact.getUser().equals(this))
		{
			contact.setUser(this);
		}
	}
	
	public Set<Role> getRoles()
	{
		return Collections.unmodifiableSet(this.roles);
	}
	
	public List<String> getRoleNames()
	{
		List<String> roleNames = new ArrayList<>();
		for (Role role : this.roles)
		{
			roleNames.add(role.getRoleName());
		}
		return roleNames;
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
		if (this.roles.contains(role))
		{
			this.roles.remove(role);
			role.removeUser(this);
		}
	}
	
	public void cleanRestrictedFields(List<String> restrictedFields)
	{
		if (restrictedFields.contains("userId"))
		{
			this.userId = null;
		}
		if (restrictedFields.contains("username"))
		{
			this.username = null;
		}
		if (restrictedFields.contains("password"))
		{
			this.password = null;
		}
		if (restrictedFields.contains("enabled"))
		{
			this.enabled = null;
		}
		if (restrictedFields.contains("dateAdded"))
		{
			this.dateAdded = null;
		}
		if (restrictedFields.contains("lastAccess"))
		{
			this.lastAccess = null;
		}
		if (restrictedFields.contains("contact"))
		{
			this.contact = null;
		}
		if (restrictedFields.contains("roles"))
		{
			this.roles = Collections.unmodifiableSet(new HashSet<>());
		}
	}
	
	public void cleanRestrictedFields(List<String> restrictedFields, User user)
	{
		if (restrictedFields.contains("userId"))
		{
			this.userId = user.getUserId();
		}
		if (restrictedFields.contains("username"))
		{
			this.username = user.getUsername();
		}
		if (restrictedFields.contains("password"))
		{
			this.password = user.password;
		}
		if (restrictedFields.contains("enabled"))
		{
			this.enabled = user.isEnabled();
		}
		if (restrictedFields.contains("dateAdded"))
		{
			this.dateAdded = user.getDateAdded();
		}
		if (restrictedFields.contains("lastAccess"))
		{
			this.lastAccess = user.getLastAccess();
		}
		if (restrictedFields.contains("contact"))
		{
			this.contact = user.getContact();
		}
		if (restrictedFields.contains("roles"))
		{
			this.roles = user.getRoles();
		}
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
