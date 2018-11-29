package com.unlimitedcompanies.coms.ws.security.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.unlimitedcompanies.coms.securityServiceExceptions.DuplicateContactEntryException;
import com.unlimitedcompanies.coms.ws.security.reps.ContactCollectionResponse;
import com.unlimitedcompanies.coms.ws.security.reps.ContactRep;
import com.unlimitedcompanies.coms.ws.security.reps.ContactSingleResponse;

@RestController
public class ContactRestController
{	
	@Autowired
	ContactService contactService;
	
	private final String baseURL = "http://localhost:8080/comsws/rest/contact/";
	private final String baseURI = "/rest/contact";
	private final String allContacts = baseURI + "s";
	private final String contactDetails = baseURI + "/{id}";
	
	@RequestMapping(value = allContacts, method = RequestMethod.GET)
	public ContactCollectionResponse returnAllContacts() throws ContactNotFoundException
	{
		// TODO: Need to support results by pages, eg. return 100 customers max per page
		
		List<Contact> foundContacts = contactService.searchAllContacts();
		ContactCollectionResponse allContacts = new ContactCollectionResponse(foundContacts);
		
		Link baseLink = new Link(baseURL).withRel("base_url");
		allContacts.add(baseLink);
		
		for (ContactRep next : allContacts.getContactCollection())
		{
			Link link = linkTo(methodOn(ContactRestController.class).findContactById(next.getContactId())).withSelfRel();
			next.add(link);
		}
		
		return allContacts;
	}

	@RequestMapping(value = contactDetails, method = RequestMethod.GET)
	public ContactSingleResponse findContactById(@PathVariable Integer id) throws ContactNotFoundException
	{
		Contact foundContact = contactService.searchContactById(id);
		ContactSingleResponse contact = new ContactSingleResponse(foundContact);
		Link link = linkTo(methodOn(ContactRestController.class).findContactById(id)).withSelfRel();
		contact.getSingleContact().add(link);
		return contact;
	}
	
	@RequestMapping(value = baseURI, method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public ContactSingleResponse saveNewContact(@RequestBody Contact newContact) 
			throws ContactNotFoundException, DuplicateContactEntryException
	{
		Contact contact = contactService.saveContact(newContact);
		ContactSingleResponse contactResponse = new ContactSingleResponse(contact);
		Link link = linkTo(methodOn(ContactRestController.class).findContactById(contact.getContactId())).withSelfRel();
		contactResponse.getSingleContact().add(link);
		return contactResponse;
	}
	
	@RequestMapping(value = baseURI, method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.OK)
	public ContactSingleResponse updateContact(@RequestBody Contact editedContact) throws ContactNotFoundException
	{
		Contact updatedContact = contactService.updateContact(editedContact);
		ContactSingleResponse contactResponse = new ContactSingleResponse(updatedContact);
		Link link = linkTo(methodOn(ContactRestController.class).findContactById(updatedContact.getContactId())).withSelfRel();
		contactResponse.getSingleContact().add(link);
		return contactResponse;
	}
	
	@RequestMapping(value = contactDetails, method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public ContactSingleResponse deleteContact(@PathVariable Integer id) throws ContactNotFoundException
	{		
		// TODO: improve method to respond with a better message - possibly an object of type message
		contactService.deleteContact(id);
		ContactSingleResponse contactResponse = new ContactSingleResponse();
		
		// TODO: Spring might throw DataIntegrityViolationException if trying to delete 
		//		 a contact that has other elements associated with it like a user
		
		return contactResponse;
	}
	
	@ExceptionHandler(ContactNotFoundException.class)
	public ResponseEntity<ContactSingleResponse> contactNotFoundExceptionHandler() 
	{
		ContactSingleResponse singleContact = new ContactSingleResponse();
		singleContact.setStatusCode(HttpStatus.NOT_FOUND.value());
		singleContact.addError("The contact could not be found");
		for (String next : singleContact.getErrors())
		{
			System.out.println("==============================> " + next);
		}
		return new ResponseEntity<>(singleContact, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(DuplicateContactEntryException.class)
	public ResponseEntity<ContactSingleResponse> contactUniqueConstraintViolationHandler()
	{
		ContactSingleResponse singleContact = new ContactSingleResponse();
		singleContact.setStatusCode(HttpStatus.BAD_REQUEST.value());
		singleContact.addError("The contact to be created or some of its information already exists");
		return new ResponseEntity<>(singleContact, HttpStatus.BAD_REQUEST);
	}
}
