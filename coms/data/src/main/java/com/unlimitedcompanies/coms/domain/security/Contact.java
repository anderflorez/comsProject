package com.unlimitedcompanies.coms.domain.security;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name="contacts")
public class Contact
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Integer contactId;
	
	@NotEmpty
	private String firstName;
	private String middleName;
	private String lastName;
	private String email;
	private String contactCharId;
	
	@OneToOne(mappedBy = "contact")
	private User user;
	
	protected Contact()
	{
		this.contactCharId = UUID.randomUUID().toString();
	}
	
	public Contact(String firstName, String middleName, String lastName)
	{
		this.contactCharId = UUID.randomUUID().toString();
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.email = null;
	}
	
	public Contact(String firstName, String middleName, String lastName, String email)
	{
		this.contactCharId = UUID.randomUUID().toString();
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.email = email;
	}
	
	public Integer getContactId()
	{
		return contactId;
	}
	
	public String getContactCharId()
	{
		return contactCharId;
	}
	
	public void setContactCharId(String contactCharId)
	{
		this.contactCharId = contactCharId;
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
	
	public User getUser()
	{
		return user;
	}
	
	public void setUser(User user)
	{
		this.user = user;
		if (user.getContact() == null || !user.getContact().equals(this))
		{
			user.setContact(this);
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Contact other = (Contact) obj;
		if (email == null)
		{
			if (other.email != null) 
			{
				return false;
			}
			else
			{
				if (firstName == null)
				{
					if (other.firstName != null) return false;
				}
				else if (!firstName.equals(other.firstName)) return false;
				if (lastName == null)
				{
					if (other.lastName != null) return false;
				}
				else if (!lastName.equals(other.lastName)) return false;
			}
		}
		else if (!email.equals(other.email)) return false;
		return true;
	}

}
