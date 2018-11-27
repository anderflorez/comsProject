package com.unlimitedcompanies.coms.ws.security.reps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.ws.appMgmt.RepFacade;

@XmlRootElement(name = "response")
public class ContactCollectionResponse extends RepFacade
{
	@XmlElement(name = "contact")
	private List<ContactRep> contactCollection;
	
	public ContactCollectionResponse() {}
	
	public ContactCollectionResponse(List<Contact> domainContacts)
	{
		this.contactCollection = new ArrayList<>();
		for (Contact contact : domainContacts)
		{
			this.contactCollection.add(new ContactRep(contact));
		}
	}

	public List<ContactRep> getContactCollection()
	{
		return Collections.unmodifiableList(this.contactCollection);
	}

	public void setContactCollection(List<Contact> domainContacts)
	{
		this.contactCollection.clear();
		for (Contact contact : domainContacts)
		{
			this.contactCollection.add(new ContactRep(contact));
		}
	}

}
