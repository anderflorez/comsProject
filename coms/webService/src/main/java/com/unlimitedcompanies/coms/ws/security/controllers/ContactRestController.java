package com.unlimitedcompanies.coms.ws.security.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
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
import com.unlimitedcompanies.coms.ws.security.reps.ContactCollectionResponse;
import com.unlimitedcompanies.coms.ws.security.reps.ContactRep;
import com.unlimitedcompanies.coms.ws.security.reps.ContactSingleResponse;

@RestController
public class ContactRestController
{	
	@Autowired
	ContactService contactService;
	
	@ExceptionHandler(ContactNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "The contact could not be found")
	public ContactSingleResponse contactNotFoundExceptionHandler() 
	{
//		ContactSingleResponse contactNotFoundError = new ContactSingleResponse();
//		contactNotFoundError.setErrorCode(HttpStatus.NOT_FOUND.value());
//		contactNotFoundError.setErrorMessage("The contact could not be found");
//		return new ResponseEntity<>(contactNotFoundError, HttpStatus.OK);
		
		ContactSingleResponse singleContact = new ContactSingleResponse();
		singleContact.setErrorCode(HttpStatus.NOT_FOUND.value());
		singleContact.setErrorMessage("The contact could not be found");
		return singleContact;
	}
	
	@RequestMapping(value="/rest/contacts", method = RequestMethod.GET)
	public ContactCollectionResponse returnAllContacts() throws ContactNotFoundException
	{
		// TODO: Need to support results by pages, eg. return 100 customers max per page
		
		List<Contact> foundContacts = contactService.searchAllContacts();
		ContactCollectionResponse allContacts = new ContactCollectionResponse(foundContacts);
		
		for (ContactRep next : allContacts.getContactCollection())
		{
			Link link = linkTo(methodOn(ContactRestController.class).findContactById(next.getContactId())).withSelfRel();
			next.add(link);
		}
		
		return allContacts;
	}

	@RequestMapping(value = "/rest/contact/{id}", method = RequestMethod.GET)
	public ContactSingleResponse findContactById(@PathVariable Integer id) throws ContactNotFoundException
	{
		Contact foundContact = contactService.searchContactById(id);
		ContactSingleResponse contact = new ContactSingleResponse(foundContact);
		Link link = linkTo(methodOn(ContactRestController.class).findContactById(id)).withSelfRel();
		contact.getSingleContact().add(link);
		return contact;
	}
	
	@RequestMapping(value = "/rest/contacts", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public Contact saveNewContact(@RequestBody Contact newContact)
	{
		return contactService.saveContact(newContact);
	}

}
