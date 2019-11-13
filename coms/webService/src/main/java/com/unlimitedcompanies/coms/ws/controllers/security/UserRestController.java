package com.unlimitedcompanies.coms.ws.controllers.security;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.exceptions.IncorrectPasswordException;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotCreatedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotDeletedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.AuthService;
import com.unlimitedcompanies.coms.service.security.ContactService;
import com.unlimitedcompanies.coms.ws.config.RestLinks;
import com.unlimitedcompanies.coms.ws.reps.ErrorRep;
import com.unlimitedcompanies.coms.ws.reps.security.UserCollectionResponse;
import com.unlimitedcompanies.coms.ws.reps.security.UserDTO;
import com.unlimitedcompanies.coms.ws.reps.security.UserPasswordDTO;

@RestController
public class UserRestController
{
	@Autowired
	AuthService authService;
	
	@Autowired
	ContactService contactService;
	
	private final String resource = "user";
	
	@RequestMapping(value = RestLinks.URI_REST_BASE + "loggedUser", method = RequestMethod.GET)
	public UserDTO getUserInfo() throws RecordNotFoundException, NoResourceAccessException
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		
		User user = authService.searchUserByUsernameWithContact(userDetails.getUsername(), userDetails.getUsername());
		UserDTO loggedUser = new UserDTO(user);
		
		Link selfLink = linkTo(methodOn(UserRestController.class).findUserById(loggedUser.getUserId())).withSelfRel();
		Link contactLink = linkTo(methodOn(ContactRestController.class).findContactById(user.getContact().getContactId())).withRel("contact");
		loggedUser.add(selfLink, contactLink);
		
		return loggedUser;
	}
	
//	@RequestMapping(value = RestLinks.URI_REST_BASE + resource + "s", method = RequestMethod.GET)
//	public UserCollectionResponse allUsers(@RequestParam(name = "pag", required = false) Integer pag,
//										   @RequestParam(name = "epp", required = false) Integer epp)
//	{
//		if (pag == null) pag = 1;
//		if (epp == null) epp = 10;
//				
//		UserCollectionResponse foundUsers = new UserCollectionResponse(authService.searchUsersByRange(pag, epp));
//		Link baseLink = new Link(RestLinks.FULL_REST_URL_BASE + resource).withRel("base_url");
//		foundUsers.add(baseLink);
//
//		if (pag > 1)
//		{
//			int prev = pag - 1;
//			foundUsers.setPrevPage(prev);
//			Link prevLink = new Link(RestLinks.FULL_REST_URL_BASE + resource + "?pag=" + prev + "&epp=" + epp).withRel("previous");
//			foundUsers.add(prevLink);
//		}
//		
//		if (authService.hasNextUser(pag + 1, epp))
//		{
//			int next = pag + 1;
//			foundUsers.setNextPage(next);
//			Link nextLink = new Link(RestLinks.FULL_REST_URL_BASE + resource + "?pag=" + next + "&epp=" + epp).withRel("next");			
//			foundUsers.add(nextLink);
//		}		
//				
//		for (UserDTO nextUser : foundUsers.getUsers())
//		{
//			try
//			{
//				Link link = linkTo(methodOn(UserRestController.class).findUserById(nextUser.getUserId())).withSelfRel();
//				nextUser.add(link);
//			} catch (RecordNotFoundException e) {}
//		}
//		
//		return foundUsers;
//	}
//	
	@RequestMapping(value = RestLinks.URI_REST_BASE + resource + "/{id}", method = RequestMethod.GET)
	public UserDTO findUserById(@PathVariable Integer id) throws RecordNotFoundException, NoResourceAccessException
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		
		User user = authService.searchUserByIdWithContact(id, userDetails.getUsername());
		UserDTO userResponse = new UserDTO(user);
		
		Link userLink = linkTo(methodOn(UserRestController.class).findUserById(id)).withSelfRel();
		Link contactLink = linkTo(methodOn(ContactRestController.class).findContactById(userResponse.getUserId())).withRel("contact");
		userResponse.add(userLink, contactLink);
		
		return userResponse;
	}
	
//	@RequestMapping(value = RestLinks.URI_REST_BASE + resource, method = RequestMethod.POST)
//	@ResponseStatus(value = HttpStatus.CREATED)
//	public UserDTO saveNewUser(@RequestBody UserDTO newUser) throws RecordNotFoundException, RecordNotCreatedException
//	{
//		Contact contact = contactService.searchContactById(newUser.getContactId());
//		User user = new User(newUser.getUsername(), newUser.getPassword(), contact);
//		UserDTO createdUser = new UserDTO(authService.saveUser(user));
//		
//		Link userLink = linkTo(methodOn(UserRestController.class).findUserById(createdUser.getUserId())).withSelfRel();
//		createdUser.add(userLink);
//		
//		return createdUser;
//	}
//	
//	@RequestMapping(value = RestLinks.URI_REST_BASE + resource, method = RequestMethod.PUT)
//	@ResponseStatus(value = HttpStatus.OK)
//	public UserDTO updateUser(@RequestBody UserDTO editedUser) throws RecordNotFoundException
//	{		
//		// TODO: Create a new exception to be thrown when the next line does not update the record - probably in services
//		User user = new User(editedUser.getUserId(), editedUser.getUsername(), editedUser.isEnabled());
//		UserDTO updatedUser = new UserDTO(authService.updateUser(user));
//		
//		Link userLink = linkTo(methodOn(UserRestController.class).findUserById(updatedUser.getUserId())).withSelfRel();
//		updatedUser.add(userLink);
//		
//		return updatedUser;
//	}
//	
//	@RequestMapping(value = RestLinks.URI_REST_BASE + resource + "/password", method = RequestMethod.PUT)
//	@ResponseStatus(value = HttpStatus.NO_CONTENT)
//	public int chagePassword(@RequestBody UserPasswordDTO password) throws RecordNotFoundException, 
//																			IncorrectPasswordException, 
//																			RecordNotChangedException
//	{
//		// TODO: Create a method that allows admin to change any user's password
//		
//		authService.changeUserPassword(password.getUserId(), password.getOldPassword(), password.getNewPassword());
//		return 0;
//	}
//	
//	@RequestMapping(value = RestLinks.URI_REST_BASE + resource + "/{id}", method = RequestMethod.DELETE)
//	@ResponseStatus(value = HttpStatus.NO_CONTENT)
//	public void deleteUser(@PathVariable Integer id) throws RecordNotFoundException, RecordNotDeletedException 
//	{
//		// TODO: Spring might throw DataIntegrityViolationException if trying to delete 
//		//		 a user that has other elements associated with it
//		
//		authService.deleteUser(id.intValue());
//	}
//	
	
	@ExceptionHandler(NoResourceAccessException.class)
	public ResponseEntity<ErrorRep> noResourceAccessExceptionHandler(NoResourceAccessException e)
	{
		ErrorRep errorResponse = new ErrorRep();
		errorResponse.setStatusCode(HttpStatus.FORBIDDEN.value());
		errorResponse.addError("You are not authorized to access this resource. Please contact your system administrator");
		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<ErrorRep> recordNotFoundExceptionHandler(RecordNotFoundException e) 
	{
		ErrorRep error = new ErrorRep();
		error.setStatusCode(HttpStatus.NOT_FOUND.value());
		error.addError(e.getMessage());
		
		MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add("comsAPI", RestLinks.FULL_REST_URL_BASE + resource);
		
		return new ResponseEntity<>(error, headers, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(RecordNotCreatedException.class)
	public ResponseEntity<ErrorRep> userNotCreatedExceptionHandler()
	{
		ErrorRep error = new ErrorRep();
		error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		error.addError("The new user could not be created");
		
		MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add("comsAPI", RestLinks.FULL_REST_URL_BASE + resource);
		
		return new ResponseEntity<>(error, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
//	@ExceptionHandler(RecordNotChangedException.class)
//	public ResponseEntity<ErrorRep> userNotEditedExceptionHandler(RecordNotChangedException e)
//	{
//		ErrorRep error = new ErrorRep();
//		error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//		error.addError(e.getMessage());
//		
//		MultiValueMap<String, String> headers = new HttpHeaders();
//		headers.add("comsAPI", RestLinks.FULL_REST_URL_BASE + resource);
//		
//		return new ResponseEntity<>(error, headers, HttpStatus.INTERNAL_SERVER_ERROR);
//	}
	
	@ExceptionHandler({IncorrectPasswordException.class})
	public ResponseEntity<ErrorRep> incorrectPasswordExceptionHandler()
	{
		ErrorRep error = new ErrorRep();
		error.setStatusCode(HttpStatus.UNAUTHORIZED.value());
		error.addError("The password could not be changed");
		error.addError("The user password you have provided is incorrect");
		
		MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add("comsAPI", RestLinks.FULL_REST_URL_BASE + resource);
		
		return new ResponseEntity<>(error, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(RecordNotDeletedException.class)
	public ResponseEntity<ErrorRep> userNotDeletedExceptionHandler(RecordNotDeletedException e)
	{
		ErrorRep errorResponse = new ErrorRep();
		errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		errorResponse.addError(e.getMessage());
		errorResponse.addMessage("The user could not be deleted. Please try again or contact your system administrator");
		
		MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add("comsAPI", RestLinks.FULL_REST_URL_BASE + resource);
		
		return new ResponseEntity<>(errorResponse, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
