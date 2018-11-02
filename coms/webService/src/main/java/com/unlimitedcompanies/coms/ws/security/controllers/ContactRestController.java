package com.unlimitedcompanies.coms.ws.security.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.securityService.ContactService;
import com.unlimitedcompanies.coms.securityServiceExceptions.ContactNotFoundException;
import com.unlimitedcompanies.coms.ws.security.reps.ContactCollectionRepresentation;
import com.unlimitedcompanies.coms.ws.security.reps.ContactNotFoundErrorInformation;

@RestController
public class ContactRestController
{	
	@Autowired
	ContactService contactService;
	
	@ExceptionHandler(ContactNotFoundException.class)
	public ResponseEntity<ContactNotFoundErrorInformation> rulesForContactNotFound() 
	{
		ContactNotFoundErrorInformation error = new ContactNotFoundErrorInformation("The contact wasn't found.");
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.put("message", new ArrayList<String>());
		map.get("message").add(error.getMessage());
		
		return new ResponseEntity<>(error, map, HttpStatus.NOT_FOUND);

	}
	
	// This method only returns contacts
	@RequestMapping(value="/rest/contacts")
	public ContactCollectionRepresentation returnAllContacts()
	{
		List<Contact> allContacts = contactService.searchAllContacts();
		return new ContactCollectionRepresentation(allContacts);
	}

	@RequestMapping(value = "/rest/contact/{id}")
	public Contact findCustomerById(@PathVariable String id) throws ContactNotFoundException
	{
		return contactService.searchContactById(id);
	}
	
	@RequestMapping(value = "/rest/contacts", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public Contact saveNewContact(@RequestBody Contact newContact)
	{
		return contactService.saveContact(newContact);
	}

}
