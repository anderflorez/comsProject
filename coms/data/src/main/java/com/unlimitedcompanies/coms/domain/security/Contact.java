package com.unlimitedcompanies.coms.domain.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import com.unlimitedcompanies.coms.domain.employee.Employee;
import com.unlimitedcompanies.coms.domain.security.exen.InvalidPhoneNumberException;

@Entity
@Table(name="contacts")
public class Contact
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Integer contactId;
	private String contactCharId;
	
	@NotEmpty
	private String firstName;
	private String middleName;
	private String lastName;
	private String email;
	
	@OneToMany(mappedBy = "contact", fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
	private List<ContactPhone> contactPhones;
	
	@OneToOne(mappedBy = "contact", fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
	private ContactAddress contactAddress;
	
	@OneToOne(mappedBy = "contact")
	private User user;
	
	@OneToOne(mappedBy = "contact")
	private Employee employee;
	
	protected Contact()
	{
		this.contactCharId = UUID.randomUUID().toString();
		this.contactPhones = new ArrayList<>();
	}
	
	public Contact(String firstName, String middleName, String lastName)
	{
		this.contactCharId = UUID.randomUUID().toString();
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.email = null;
		this.contactPhones = new ArrayList<>();
	}
	
	public Contact(String firstName, String middleName, String lastName, String email)
	{
		this.contactCharId = UUID.randomUUID().toString();
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.email = email;
		this.contactPhones = new ArrayList<>();
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
	
	public ContactAddress getAddress()
	{
		return contactAddress;
	}

	public void setAddress(String street, String city, String state, String zipCode)
	{
		ContactAddress address = new ContactAddress(street, city, state, zipCode, this);
		this.contactAddress = address;
	}
	
	public void removeAddress()
	{
		this.contactAddress = null;
	}

	public List<ContactPhone> getPhones()
	{
		if (this.contactPhones != null)
		{
			return Collections.unmodifiableList(contactPhones);			
		}
		return null;
	}
	
	public void addPhone(String phoneNumber, String extention, String phoneType) throws InvalidPhoneNumberException
	{
		ContactPhone phone = new ContactPhone(phoneNumber, extention, phoneType, this);
		if (this.contactPhones == null)
		{
			this.contactPhones = new ArrayList<>();
		}
		this.contactPhones.add(phone);
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

	public Employee getEmployee()
	{
		return employee;
	}

	public void setEmployee(Employee employee)
	{
		this.employee = employee;
	}
	
	public void cleanRestrictedFields(List<String> restrictions)
	{
		if (restrictions.contains("contactId"))
		{
			this.contactId = null;
		}
		if (restrictions.contains("contactCharId"))
		{
			this.contactCharId = null;
		}
		if (restrictions.contains("firstName"))
		{
			this.firstName = null;
		}
		if (restrictions.contains("middleName"))
		{
			this.middleName = null;
		}
		if (restrictions.contains("lastName"))
		{
			this.lastName = null;
		}
		if (restrictions.contains("email"))
		{
			this.email = null;
		}
		if (restrictions.contains("contactPhones"))
		{
			this.contactPhones = null;
		}
		if (restrictions.contains("contactAddress"))
		{
			this.contactAddress = null;
		}
		if (restrictions.contains("user"))
		{
			this.user = null;
		}
		if (restrictions.contains("employee"))
		{
			this.employee = null;
		}
	}
	
	public void cleanRestrictedFields(List<String> restrictions, Contact contact)
	{
		if (restrictions.contains("contactId"))
		{
			this.contactId = contact.getContactId();
		}
		if (restrictions.contains("contactCharId"))
		{
			this.contactCharId = contact.getContactCharId();
		}
		if (restrictions.contains("firstName"))
		{
			this.firstName = contact.getFirstName();
		}
		if (restrictions.contains("middleName"))
		{
			this.middleName = contact.getMiddleName();
		}
		if (restrictions.contains("lastName"))
		{
			this.lastName = contact.getLastName();
		}
		if (restrictions.contains("email"))
		{
			this.email = contact.getEmail();
		}
		if (restrictions.contains("contactPhones"))
		{
			this.contactPhones = contact.getPhones();
		}
		if (restrictions.contains("contactAddress"))
		{
			this.contactAddress = contact.getAddress();
		}		
		if (restrictions.contains("user"))
		{
			this.user = contact.getUser();
		}
		if (restrictions.contains("employee"))
		{
			this.employee = contact.getEmployee();
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
