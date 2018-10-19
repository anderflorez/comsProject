package com.unlimitedcompanies.coms.webservice.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.securityService.ContactService;
import com.unlimitedcompanies.coms.securityServiceExceptions.ContactNotFoundException;

@RestController
public class ContactRestController
{	
	@Autowired
	ContactService contactService;
	
	@ExceptionHandler(ContactNotFoundException.class)
	public ResponseEntity<ContactNotFoundErrorInformation> rulesForContactNotFound() 
	{
		ContactNotFoundErrorInformation error = new ContactNotFoundErrorInformation("The contact wasn't found.");
		return new ResponseEntity<ContactNotFoundErrorInformation>(error, HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(value = "/contact/{id}")
	public Contact findCustomerById(@PathVariable String id) throws ContactNotFoundException
	{
		return contactService.searchContactById(id);
	}
	
	// This method only returns contacts
	@RequestMapping(value="/contacts")
	public ContactCollectionRepresentation returnAllContacts()
	{
		List<Contact> allContacts = contactService.searchAllContacts();
		return new ContactCollectionRepresentation(allContacts);
	}

}
