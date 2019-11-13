package com.unlimitedcompanies.coms.ws.controllers.security;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotDeletedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.AuthService;
import com.unlimitedcompanies.coms.service.security.ContactService;
import com.unlimitedcompanies.coms.ws.config.RestLinks;
import com.unlimitedcompanies.coms.ws.reps.ErrorRep;
import com.unlimitedcompanies.coms.ws.reps.security.ContactCollectionResponse;
import com.unlimitedcompanies.coms.ws.reps.security.ContactDTO;

@RestController
public class ContactRestController
{
	@Autowired
	ContactService contactService;
	
	@Autowired
	AuthService authService;
	
//	@RequestMapping(value = RestLinks.URI_REST_BASE + "contacts", method = RequestMethod.GET)
//	public ContactCollectionResponse allContacts(@RequestParam(name = "pag", required = false) Integer pag,
//												 @RequestParam(name = "epp", required = false) Integer epp)
//	{
//		if (pag == null) pag = 1;
//		if (epp == null) epp = 10;
//		
//		List<Contact> foundContacts = contactService.searchContactsByRange(pag, epp);
//		ContactCollectionResponse allContacts = new ContactCollectionResponse(foundContacts);
//		
//		Link baseLink = null;
//		try
//		{
//			baseLink = linkTo(methodOn(ContactRestController.class).saveNewContact(null)).withRel("base_contact");
//			Link selfLink = linkTo(methodOn(ContactRestController.class).allContacts(pag, epp)).withSelfRel();
//			allContacts.add(selfLink);
//		}
//		catch (DuplicateRecordException e1) {}
//		
//		if (pag > 1)
//		{
//			int prev = pag - 1;
//			allContacts.setPrevPage(prev);
//			Link prevLink = new Link(baseLink.getHref() + "?pag=" + prev + "&epp=" + epp).withRel("previous");
//			allContacts.add(prevLink);
//		}
//		
//		if (contactService.hasNextContact(pag + 1, epp))
//		{
//			int next = pag + 1;
//			allContacts.setNextPage(next);
//			Link nextLink = new Link(baseLink.getHref() + "?pag=" + next + "&epp=" + epp).withRel("next");			
//			allContacts.add(nextLink);
//		}		
//				
//		for (ContactDTO nextContact : allContacts.getContactCollection())
//		{
//			Link link;
//			try
//			{
//				link = linkTo(methodOn(ContactRestController.class).findContactById(nextContact.getContactId())).withSelfRel();
//				nextContact.add(link);
//			} catch (RecordNotFoundException e) {}
//		}
//		
//		return allContacts;
//	}
//
	@RequestMapping(value = RestLinks.URI_REST_BASE + "contact/{id}", method = RequestMethod.GET)
	public ContactDTO findContactById(@PathVariable Integer id) throws RecordNotFoundException, NoResourceAccessException
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		
		Contact foundContact = contactService.searchContactById(id, userDetails.getUsername());
		ContactDTO contact = new ContactDTO(foundContact);
		Link selfLink = linkTo(methodOn(ContactRestController.class).findContactById(id)).withSelfRel();
		contact.add(selfLink);
		return contact;
	}
	
//	@RequestMapping(value = RestLinks.URI_REST_BASE + "contact", method = RequestMethod.POST)
//	@ResponseStatus(value = HttpStatus.CREATED)
//	public ContactDTO saveNewContact(@RequestBody Contact newContact) 
//			throws DuplicateRecordException
//	{
//		Contact contact = contactService.saveContact(newContact);
//		ContactDTO contactResponse = new ContactDTO(contact);
//		Link link;
//		try
//		{
//			link = linkTo(methodOn(ContactRestController.class).findContactById(contact.getContactId())).withSelfRel();
//			contactResponse.add(link);
//		} catch (RecordNotFoundException e) {}
//
//		return contactResponse;
//	}
//	
//	@RequestMapping(value = RestLinks.URI_REST_BASE + "contact", method = RequestMethod.PUT)
//	@ResponseStatus(value = HttpStatus.OK)
//	public ContactDTO updateContact(@RequestBody Contact editedContact) throws RecordNotFoundException
//	{
//		Contact updatedContact = contactService.updateContact(editedContact);
//		ContactDTO contactResponse = new ContactDTO(updatedContact);
//		Link link = linkTo(methodOn(ContactRestController.class).findContactById(updatedContact.getContactId())).withSelfRel();
//		contactResponse.add(link);
//		return contactResponse;
//	}
//	
//	@RequestMapping(value = RestLinks.URI_REST_BASE + "contact/{id}", method = RequestMethod.DELETE)
//	@ResponseStatus(value = HttpStatus.NO_CONTENT)
//	public void deleteContact(@PathVariable Integer id) throws RecordNotFoundException, RecordNotDeletedException
//	{		
//		// TODO: Spring might throw DataIntegrityViolationException if trying to delete 
//		//		 a contact that has other elements associated with it like a user
//		
//		contactService.deleteContact(id);
//	}
//	
	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<ErrorRep> recordNotFoundExceptionHandler(RecordNotFoundException e) 
	{
		ErrorRep errorResponse = new ErrorRep();
		errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
		errorResponse.addError(e.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(NoResourceAccessException.class)
	public ResponseEntity<ErrorRep> noResourceAccessExceptionHandler(NoResourceAccessException e)
	{
		ErrorRep errorResponse = new ErrorRep();
		errorResponse.setStatusCode(HttpStatus.FORBIDDEN.value());
		errorResponse.addError("You are not authorized to access this resource. Please contact your system administrator");
		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
	}
	
//	@ExceptionHandler(DuplicateRecordException.class)
//	public ResponseEntity<ErrorRep> contactUniqueConstraintViolationHandler()
//	{
//		ErrorRep errorResponse = new ErrorRep();
//		errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
//		errorResponse.addError("The contact to be created or some of its information already exists");
//		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//	}
//	
//	@ExceptionHandler(RecordNotDeletedException.class)
//	public ResponseEntity<ErrorRep> contactNotDeletedExceptionHandler(RecordNotDeletedException e)
//	{
//		ErrorRep errorResponse = new ErrorRep();
//		errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//		errorResponse.addError("Unknown error");
//		errorResponse.addError(e.getMessage());
//		errorResponse.addMessage("Please try again or contact your system administrator");
//		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//	}
}
