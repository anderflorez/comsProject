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
import com.unlimitedcompanies.coms.ws.security.reps.ContactDTO;
import com.unlimitedcompanies.coms.ws.security.reps.ErrorRep;

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
				
		for (ContactDTO nextContact : allContacts.getContactCollection())
		{
			Link link = linkTo(methodOn(ContactRestController.class).findContactById(nextContact.getContactId())).withSelfRel();
			nextContact.add(link);
		}
		
		return allContacts;
	}

	@RequestMapping(value = contactDetails, method = RequestMethod.GET)
	public ContactDTO findContactById(@PathVariable Integer id) throws ContactNotFoundException
	{
		Contact foundContact = contactService.searchContactById(id);
		ContactDTO contact = new ContactDTO(foundContact);
		Link link = linkTo(methodOn(ContactRestController.class).findContactById(id)).withSelfRel();
		contact.add(link);
		return contact;
	}
	
	@RequestMapping(value = baseURI, method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public ContactDTO saveNewContact(@RequestBody Contact newContact) 
			throws ContactNotFoundException, DuplicateContactEntryException
	{
		Contact contact = contactService.saveContact(newContact);
		ContactDTO contactResponse = new ContactDTO(contact);
		Link link = linkTo(methodOn(ContactRestController.class).findContactById(contact.getContactId())).withSelfRel();
		contactResponse.add(link);

		return contactResponse;
	}
	
	@RequestMapping(value = baseURI, method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.OK)
	public ContactDTO updateContact(@RequestBody Contact editedContact) throws ContactNotFoundException
	{
		Contact updatedContact = contactService.updateContact(editedContact);
		ContactDTO contactResponse = new ContactDTO(updatedContact);
		Link link = linkTo(methodOn(ContactRestController.class).findContactById(updatedContact.getContactId())).withSelfRel();
		contactResponse.add(link);
		return contactResponse;
	}
	
	@RequestMapping(value = contactDetails, method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void deleteContact(@PathVariable Integer id) 
			throws ContactNotFoundException, ContactNotDeletedException
	{		
		// TODO: Spring might throw DataIntegrityViolationException if trying to delete 
		//		 a contact that has other elements associated with it like a user
		
		contactService.deleteContact(id);
	}
	
	@ExceptionHandler(ContactNotFoundException.class)
	public ResponseEntity<ErrorRep> contactNotFoundExceptionHandler() 
	{
		ErrorRep errorResponse = new ErrorRep();
		errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
		errorResponse.addError("The contact could not be found");
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(DuplicateContactEntryException.class)
	public ResponseEntity<ErrorRep> contactUniqueConstraintViolationHandler()
	{
		ErrorRep errorResponse = new ErrorRep();
		errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
		errorResponse.addError("The contact to be created or some of its information already exists");
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ContactNotDeletedException.class)
	public ResponseEntity<ErrorRep> contactNotDeletedExceptionHandler()
	{
		ErrorRep errorResponse = new ErrorRep();
		errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		errorResponse.addError("The contact could not be deleted - Unknown error");
		errorResponse.addMessage("Please try again or contact your system administrator");
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
