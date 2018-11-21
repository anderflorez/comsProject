package com.unlimitedcompanies.coms.ws.security.reps;

import javax.xml.bind.annotation.XmlRootElement;

import com.unlimitedcompanies.coms.domain.security.Contact;

@XmlRootElement(name = "contact")
public class ContactRep
{
	private String contactId;
	private String firstName;
	private String middleName;
	private String lastName;
	private String email;
	
	public ContactRep() {}
	
	public ContactRep(Contact contact)
	{
		this.contactId = contact.getContactId();
		this.firstName = contact.getFirstName();
		this.middleName = contact.getMiddleName();
		this.lastName = contact.getLastName();
		this.email = contact.getEmail();
	}

	public String getContactId()
	{
		return contactId;
	}

	public void setContactId(String contactId)
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
