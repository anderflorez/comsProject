package com.unlimitedcompanies.coms.ws.security.reps;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.ws.appMgmt.RepFacade;

@XmlRootElement(name = "response")
public class ContactSingleResponse extends RepFacade
{	
	private List<ContactRep> contact;
	
	public ContactSingleResponse() {}

	public ContactSingleResponse(Contact contact)
	{
		this.contact = new ArrayList<>();
		this.contact.add(new ContactRep(contact));
	}

	public ContactRep getSingleContact()
	{
		if (!this.contact.isEmpty())
		{
			return this.contact.get(0);
		}
		return null;
	}

	public void setSingleContact(Contact contact)
	{
		this.contact.clear();
		this.contact.add(new ContactRep(contact));
	}

	@XmlElement(name = "contact")
	public List<ContactRep> getContact()
	{
		return this.contact;
	}

	public void setContact(List<ContactRep> contact)
	{
		this.contact = contact;
	}
	
}
