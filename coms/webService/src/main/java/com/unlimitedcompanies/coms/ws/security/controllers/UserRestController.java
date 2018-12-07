package com.unlimitedcompanies.coms.ws.security.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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

import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotCreatedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotDeletedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.AuthService;
import com.unlimitedcompanies.coms.ws.security.reps.ErrorRep;
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
			try
			{
				Link link = linkTo(methodOn(UserRestController.class).findUserById(nextUser.getUserId())).withSelfRel();
				nextUser.add(link);
			} catch (RecordNotFoundException e) {}
		}
		
		return foundUsers;
	}

	@RequestMapping(value = userDetails, method = RequestMethod.GET)
	public UserDTO findUserById(@PathVariable Integer id) throws RecordNotFoundException
	{
		UserDTO foundUser = new UserDTO(authService.searchUserByUserId(id));
		Link link = linkTo(methodOn(UserRestController.class).findUserById(id)).withSelfRel();
		foundUser.add(link);
		return foundUser;
	}
	
	@RequestMapping(value = baseURI, method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public UserDTO saveNewContact(@RequestBody User newUser) throws RecordNotFoundException, RecordNotCreatedException
	{
		UserDTO createdUser = new UserDTO(authService.saveUser(newUser));
		Link link = linkTo(methodOn(UserRestController.class).findUserById(createdUser.getUserId())).withSelfRel();
		createdUser.add(link);
		return createdUser;
	}
	
	@RequestMapping(value = baseURI, method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.OK)
	public UserDTO updateContact(@RequestBody User editedUser) throws RecordNotFoundException
	{
		UserDTO updatedUser = new UserDTO(authService.updateUser(editedUser));
		Link link = linkTo(methodOn(UserRestController.class).findUserById(updatedUser.getUserId())).withSelfRel();
		updatedUser.add(link);
		return updatedUser;
	}
	
	@RequestMapping(value = userDetails, method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void deleteContact(@PathVariable Integer id) throws RecordNotFoundException, RecordNotDeletedException 
	{
		// TODO: Spring might throw DataIntegrityViolationException if trying to delete 
		//		 a contact that has other elements associated with it like a user
		
		authService.deleteUser(id.intValue());
	}
	
	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<ErrorRep> userNotFoundExceptionHandler() 
	{
		ErrorRep error = new ErrorRep();
		error.setStatusCode(HttpStatus.NOT_FOUND.value());
		error.addError("The user could not be found");
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler({RecordNotCreatedException.class, RecordNotDeletedException.class})
	public ResponseEntity<ErrorRep> userNotCreatedExceptionHandler()
	{
		ErrorRep error = new ErrorRep();
		error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		error.addError("The new user could not be created");
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
