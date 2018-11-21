package com.unlimitedcompanies.coms.ws.security.reps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.unlimitedcompanies.coms.domain.security.Contact;

@XmlRootElement(name = "contacts")
public class ContactCollectionRep
{
	private List<ContactRep> contacts;
	
	public ContactCollectionRep() {}
	
	public ContactCollectionRep(List<Contact> domainContacts)
	{
		contacts = new ArrayList<>();
		for (Contact contact : domainContacts)
		{
			System.out.println(contact.getContactId());
			this.contacts.add(new ContactRep(contact));
		}
	}

	@XmlElement(name = "contact")
	public List<ContactRep> getContacts()
	{
		return Collections.unmodifiableList(this.contacts);
	}

	public void setContacts(List<ContactRep> contacts)
	{
		this.contacts = contacts;
	}

}
