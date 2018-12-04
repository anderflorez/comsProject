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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.securityService.ContactService;
import com.unlimitedcompanies.coms.securityServiceExceptions.ContactNotDeletedException;
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
	public ContactCollectionResponse allContacts(@RequestParam(name = "pag", required = false) Integer pag,
												 @RequestParam(name = "epp", required = false) Integer epp) 
			throws ContactNotFoundException
	{
		if (pag == null) pag = 1;
		if (epp == null) epp = 10;
		
		List<Contact> foundContacts = contactService.searchContactsByRange(pag, epp);
		ContactCollectionResponse allContacts = new ContactCollectionResponse(foundContacts);
		
		Link baseLink = new Link(baseURL).withRel("base_url");
		allContacts.add(baseLink);
		
		if (pag > 1)
		{
			int prev = pag - 1;
			allContacts.setPrevPage(prev);
			Link prevLink = new Link(baseURL + "?pag=" + prev + "&epp=" + epp).withRel("previous");
			allContacts.add(prevLink);
		}
		
		if (contactService.hasNextContact(pag + 1, epp))
		{
			int next = pag + 1;
			allContacts.setNextPage(next);
			Link nextLink = new Link(baseURL + "?pag=" + next + "&epp=" + epp).withRel("next");			
			allContacts.add(nextLink);
		}		
				
		for (ContactRep nextContact : allContacts.getContactCollection())
		{
			Link link = linkTo(methodOn(ContactRestController.class).findContactById(nextContact.getContactId())).withSelfRel();
			nextContact.add(link);
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
		contactResponse.setSuccess("The contact has been created successfully");
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
		contactResponse.setSuccess("The contact has been updated successfully");
		return contactResponse;
	}
	
	@RequestMapping(value = contactDetails, method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public ContactSingleResponse deleteContact(@PathVariable Integer id) 
			throws ContactNotFoundException, ContactNotDeletedException
	{		
		// TODO: Spring might throw DataIntegrityViolationException if trying to delete 
		//		 a contact that has other elements associated with it like a user
		
		contactService.deleteContact(id);
		
		ContactSingleResponse response = new ContactSingleResponse();
		response.setStatusCode(HttpStatus.OK.value());
		response.setSuccess("The contact has been deleted successfully");
		
		return response;
	}
	
	@ExceptionHandler(ContactNotFoundException.class)
	public ResponseEntity<ContactSingleResponse> contactNotFoundExceptionHandler() 
	{
		ContactSingleResponse singleContact = new ContactSingleResponse();
		singleContact.setStatusCode(HttpStatus.NOT_FOUND.value());
		singleContact.addError("The contact could not be found");
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
	
	@ExceptionHandler(ContactNotDeletedException.class)
	public ResponseEntity<ContactSingleResponse> contactNotDeletedExceptionHandler()
	{
		ContactSingleResponse singleContact = new ContactSingleResponse();
		singleContact.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		singleContact.addError("The contact could not be deleted - Unknown error");
		singleContact.addMessage("Please try again or contact your system administrator");
		return new ResponseEntity<>(singleContact, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
