package com.unlimitedcompanies.coms.ws.security.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.securityService.AuthService;
import com.unlimitedcompanies.coms.ws.security.reps.UserCollectionResponse;
import com.unlimitedcompanies.coms.ws.security.reps.UserDTO;

@RestController
public class UserRestController
{
	@Autowired
	AuthService authService;
	
	private final String baseURL = "http://localhost:8080/comsws/rest/user/";
	private final String baseURI = "/rest/user";
	private final String allUsers = baseURI + "s";
	private final String userDetails = baseURI + "/{id}";
	
	@RequestMapping("/rest/loggedUser")
	public UserDTO getUserInfo()
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		User user = authService.searchUserByUsernameWithContact(userDetails.getUsername());
		UserDTO loggedUser = new UserDTO(user);
		loggedUser.setContact(user.getContact());
		
		return loggedUser;
	}
	
	@RequestMapping(value = allUsers, method = RequestMethod.GET)
	public UserCollectionResponse allUsers(@RequestParam(name = "pag", required = false) Integer pag,
										   @RequestParam(name = "epp", required = false) Integer epp)
	{
		if (pag == null) pag = 1;
		if (epp == null) epp = 10;
		
		UserCollectionResponse foundUsers = new UserCollectionResponse(authService.searchUsersByRange(pag, epp));
		Link baseLink = new Link(baseURL).withRel("base_url");
		foundUsers.add(baseLink);

		if (pag > 1)
		{
			int prev = pag - 1;
			foundUsers.setPrevPage(prev);
			Link prevLink = new Link(baseURL + "?pag=" + prev + "&epp=" + epp).withRel("previous");
			foundUsers.add(prevLink);
		}
		
		if (authService.hasNextUser(pag + 1, epp))
		{
			int next = pag + 1;
			foundUsers.setNextPage(next);
			Link nextLink = new Link(baseURL + "?pag=" + next + "&epp=" + epp).withRel("next");			
			foundUsers.add(nextLink);
		}		
				
		for (UserDTO nextUser : foundUsers.getUsers())
		{
			Link link = linkTo(methodOn(UserRestController.class).findUserById(nextUser.getUserId())).withSelfRel();
			nextUser.add(link);
		}
		
		return foundUsers;
	}

	@RequestMapping(value = userDetails, method = RequestMethod.GET)
	public UserDTO findUserById(@PathVariable Integer id)
	{
//		Contact foundContact = contactService.searchContactById(id);
//		ContactSingleResponse contact = new ContactSingleResponse(foundContact);
//		Link link = linkTo(methodOn(ContactRestController.class).findContactById(id)).withSelfRel();
//		contact.getSingleContact().add(link);
//		return contact;
		return null;
	}
//	
//	@RequestMapping(value = baseURI, method = RequestMethod.POST)
//	@ResponseStatus(value = HttpStatus.CREATED)
//	public ContactSingleResponse saveNewContact(@RequestBody Contact newContact) 
//			throws ContactNotFoundException, DuplicateContactEntryException
//	{
//		Contact contact = contactService.saveContact(newContact);
//		ContactSingleResponse contactResponse = new ContactSingleResponse(contact);
//		Link link = linkTo(methodOn(ContactRestController.class).findContactById(contact.getContactId())).withSelfRel();
//		contactResponse.getSingleContact().add(link);
//		contactResponse.setSuccess("The contact has been created successfully");
//		return contactResponse;
//	}
//	
//	@RequestMapping(value = baseURI, method = RequestMethod.PUT)
//	@ResponseStatus(value = HttpStatus.OK)
//	public ContactSingleResponse updateContact(@RequestBody Contact editedContact) throws ContactNotFoundException
//	{
//		Contact updatedContact = contactService.updateContact(editedContact);
//		ContactSingleResponse contactResponse = new ContactSingleResponse(updatedContact);
//		Link link = linkTo(methodOn(ContactRestController.class).findContactById(updatedContact.getContactId())).withSelfRel();
//		contactResponse.getSingleContact().add(link);
//		contactResponse.setSuccess("The contact has been updated successfully");
//		return contactResponse;
//	}
//	
//	@RequestMapping(value = userDetails, method = RequestMethod.DELETE)
//	@ResponseStatus(value = HttpStatus.OK)
//	public ContactSingleResponse deleteContact(@PathVariable Integer id) 
//			throws ContactNotFoundException, ContactNotDeletedException
//	{		
//		// TODO: Spring might throw DataIntegrityViolationException if trying to delete 
//		//		 a contact that has other elements associated with it like a user
//		
//		contactService.deleteContact(id);
//		
//		ContactSingleResponse response = new ContactSingleResponse();
//		response.setStatusCode(HttpStatus.OK.value());
//		response.setSuccess("The contact has been deleted successfully");
//		
//		return response;
//	}
//	
//	@ExceptionHandler(ContactNotFoundException.class)
//	public ResponseEntity<ContactSingleResponse> contactNotFoundExceptionHandler() 
//	{
//		ContactSingleResponse singleContact = new ContactSingleResponse();
//		singleContact.setStatusCode(HttpStatus.NOT_FOUND.value());
//		singleContact.addError("The contact could not be found");
//		return new ResponseEntity<>(singleContact, HttpStatus.NOT_FOUND);
//	}
//	
//	@ExceptionHandler(DuplicateContactEntryException.class)
//	public ResponseEntity<ContactSingleResponse> contactUniqueConstraintViolationHandler()
//	{
//		ContactSingleResponse singleContact = new ContactSingleResponse();
//		singleContact.setStatusCode(HttpStatus.BAD_REQUEST.value());
//		singleContact.addError("The contact to be created or some of its information already exists");
//		return new ResponseEntity<>(singleContact, HttpStatus.BAD_REQUEST);
//	}
//	
//	@ExceptionHandler(ContactNotDeletedException.class)
//	public ResponseEntity<ContactSingleResponse> contactNotDeletedExceptionHandler()
//	{
//		ContactSingleResponse singleContact = new ContactSingleResponse();
//		singleContact.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//		singleContact.addError("The contact could not be deleted - Unknown error");
//		singleContact.addMessage("Please try again or contact your system administrator");
//		return new ResponseEntity<>(singleContact, HttpStatus.INTERNAL_SERVER_ERROR);
//	}
}
