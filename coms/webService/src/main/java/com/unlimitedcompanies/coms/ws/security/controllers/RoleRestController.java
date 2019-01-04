package com.unlimitedcompanies.coms.ws.security.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotChangedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotCreatedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotDeletedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.AuthService;
import com.unlimitedcompanies.coms.ws.config.RestLinks;
import com.unlimitedcompanies.coms.ws.security.reps.ErrorRep;
import com.unlimitedcompanies.coms.ws.security.reps.RoleCollectionResponse;
import com.unlimitedcompanies.coms.ws.security.reps.RoleDTO;
import com.unlimitedcompanies.coms.ws.security.reps.UserDetailedCollection;
import com.unlimitedcompanies.coms.ws.security.reps.UserDetailedDTO;

@RestController
public class RoleRestController
{
	@Autowired
	AuthService authService;
	
	private final String resource = "role";
	
	@RequestMapping(value = RestLinks.URI_BASE + resource + "s", method = RequestMethod.GET)
	public RoleCollectionResponse displayAllRoles(@RequestParam(name = "pag", required = false) Integer pag,
												  @RequestParam(name = "epp", required = false) Integer epp)
	{
		if (pag == null) pag = 1;
		if (epp == null) epp = 10;
		
		RoleCollectionResponse allRoles = new RoleCollectionResponse(authService.searchRolesByRange(pag, epp));
		Link baseLink = new Link(RestLinks.URL_BASE + resource).withRel("base_url");
		allRoles.add(baseLink);
		
		if (pag > 1)
		{
			int prev = pag - 1;
			allRoles.setPrev(prev);
			Link prevLink = new Link(RestLinks.URL_BASE + resource + "?pag=" + prev + "&epp=" + epp).withRel("previous");
			allRoles.add(prevLink);
		}
		
		if (authService.hasNextRole(pag + 1, epp))
		{
			int next = pag + 1;
			allRoles.setNext(next);
			Link nextLink = new Link(RestLinks.URL_BASE + resource + "?pag=" + next + "&epp=" + epp).withRel("next");
			allRoles.add(nextLink);
		}		

		for (RoleDTO nextRole : allRoles.getRoles())
		{
			Link link;
			try
			{
				link = linkTo(methodOn(RoleRestController.class).findRoleById(nextRole.getRoleId())).withSelfRel();
				nextRole.add(link);
			}
			catch (RecordNotFoundException e) {}			
		}
		
		return allRoles;
	}

	@RequestMapping(value = RestLinks.URI_BASE + resource + "/{id}", method = RequestMethod.GET)
	public RoleDTO findRoleById(@PathVariable int id) throws RecordNotFoundException
	{
		Role role = authService.searchRoleByRoleId(id);
		RoleDTO roleResponse = new RoleDTO(role);
		
		Link roleLink = linkTo(methodOn(RoleRestController.class).findRoleById(id)).withSelfRel();
		Link roleMembersLink = linkTo(methodOn(RoleRestController.class).findRoleMembersById(id)).withRel("role_members");
		Link roleNonMembers = linkTo(methodOn(RoleRestController.class).RoleNonMemberSearch(id, "")).withRel("role_nonMembers");
		roleResponse.add(roleLink, roleMembersLink, roleNonMembers);
		
		return roleResponse;
	}
	
	@RequestMapping(value = RestLinks.URI_BASE + resource + "/{id}/members", method = RequestMethod.GET)
	public UserDetailedCollection findRoleMembersById(@PathVariable int id) throws RecordNotFoundException
	{
		Role role = authService.searchRoleByIdWithMembers(id);
		UserDetailedCollection membersResponse = new UserDetailedCollection(role.getMembers());
		
		Link roleMembersLink = linkTo(methodOn(RoleRestController.class).findRoleMembersById(id)).withSelfRel();
		membersResponse.add(roleMembersLink);
		
		for (UserDetailedDTO member : membersResponse.getUsers())
		{
			Link memberLink = linkTo(methodOn(UserRestController.class).findUserById(member.getUserId())).withSelfRel();
			member.add(memberLink);
		}
		
		return membersResponse;
	}
	
	@RequestMapping(value = RestLinks.URI_BASE + resource + "/{id}/nonmembers", method = RequestMethod.GET)
	public UserDetailedCollection RoleNonMemberSearch(@PathVariable int id, @RequestParam String query)
	{
		UserDetailedCollection usersResponse = new UserDetailedCollection(authService.searchRoleNonMembers(id, query));
		
		try
		{
			Link roleMembersLink = linkTo(methodOn(RoleRestController.class).findRoleMembersById(id)).withSelfRel();
			usersResponse.add(roleMembersLink);
			for (UserDetailedDTO member : usersResponse.getUsers())
			{
				Link memberLink = linkTo(methodOn(UserRestController.class).findUserById(member.getUserId())).withSelfRel();
				member.add(memberLink);
			}
		}
		catch (RecordNotFoundException e) {}
		
		return usersResponse;
	}

	@RequestMapping(value = RestLinks.URI_BASE + resource, method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public RoleDTO saveNewRole(@RequestBody RoleDTO role) throws RecordNotCreatedException
	{		
		Role newrole = new Role(role.getRoleName());
		Role createdRole = authService.saveRole(newrole);
		RoleDTO roleResponse = new RoleDTO(createdRole);
		
		try
		{
			Link roleLink = linkTo(methodOn(RoleRestController.class).findRoleById(createdRole.getRoleId())).withSelfRel();
			roleResponse.add(roleLink);
		}
		catch (RecordNotFoundException e) {}
		
		return roleResponse;
	}
	
	@RequestMapping(value = RestLinks.URI_BASE + resource, method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.OK)
	public RoleDTO updateRole(@RequestBody RoleDTO editedRole) throws RecordNotFoundException, RecordNotChangedException
	{
		Role role = new Role(editedRole.getRoleName());
		role.setRoleId(editedRole.getRoleId());
		role = authService.updateRole(role);
		
		RoleDTO updatedRole = new RoleDTO(role);
		
		Link roleLink = linkTo(methodOn(RoleRestController.class).findRoleById(role.getRoleId())).withSelfRel();
		updatedRole.add(roleLink);
		
		return updatedRole;
	}
	
	@RequestMapping(value = RestLinks.URI_BASE + resource + "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void deleteRole(@PathVariable int id) throws RecordNotFoundException, RecordNotDeletedException
	{		
		// TODO: Make sure to throw an exception if the role is not deleted
		authService.deleteRole(id);
	}
	
	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<ErrorRep> roleNotFoundExceptionHandler(RecordNotFoundException e)
	{
		ErrorRep error = new ErrorRep();
		error.setStatusCode(HttpStatus.NOT_FOUND.value());
		error.addError(e.getMessage());
		
		MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add("comsAPI", RestLinks.URL_BASE + resource);
		
		return new ResponseEntity<>(error, headers, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(RecordNotCreatedException.class)
	public ResponseEntity<ErrorRep> roleNotCreatedExceptionHandler()
	{
		ErrorRep error = new ErrorRep();
		error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		error.addError("The new role could not be created");
		
		MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add("comsAPI", RestLinks.URL_BASE + resource);
		
		return new ResponseEntity<>(error, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(RecordNotChangedException.class)
	public ResponseEntity<ErrorRep> roleNotEditedExceptionHandler(RecordNotChangedException e)
	{
		ErrorRep error = new ErrorRep();
		error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		error.addError(e.getMessage());
		
		MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add("comsAPI", RestLinks.URL_BASE + resource);
		
		return new ResponseEntity<>(error, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(RecordNotDeletedException.class)
	public ResponseEntity<ErrorRep> roleNotDeletedExceptionHandler(RecordNotDeletedException e)
	{
		ErrorRep errorResponse = new ErrorRep();
		errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		errorResponse.addError(e.getMessage());
		errorResponse.addMessage("The role could not be deleted. Please try again or contact your system administrator");
		
		MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add("comsAPI", RestLinks.URL_BASE + resource);
		
		return new ResponseEntity<>(errorResponse, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
