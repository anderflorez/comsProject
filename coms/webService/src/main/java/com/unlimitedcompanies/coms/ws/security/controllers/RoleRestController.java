package com.unlimitedcompanies.coms.ws.security.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
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
import com.unlimitedcompanies.coms.ws.security.reps.RoleCollectionResponse;
import com.unlimitedcompanies.coms.ws.security.reps.RoleDTO;

@RestController
public class RoleRestController
{
	@Autowired
	AuthService authService;
	
	private final String resource = "role";
	private final String baseURL = "http://localhost:8080/comsws/rest/role/";
	private final String baseURI = "/rest/" + resource;
	private final String allRecords = baseURI + "s";
	private final String recordDetails = baseURI + "/{id}";
	
	@RequestMapping(value = allRecords, method = RequestMethod.GET)
	public RoleCollectionResponse displayAllRoles(@RequestParam(name = "pag", required = false) Integer pag,
												  @RequestParam(name = "epp", required = false) Integer epp)
	{
		if (pag == null) pag = 1;
		if (epp == null) epp = 10;
		
		RoleCollectionResponse allRoles = new RoleCollectionResponse(authService.searchRolesByRange(pag, epp));
		Link baseLink = new Link(baseURL).withRel("base_url");
		allRoles.add(baseLink);
		
		if (pag > 1)
		{
			int prev = pag - 1;
			allRoles.setPrev(prev);
			Link prevLink = new Link(baseURL + "?pag=" + prev + "&epp=" + epp).withRel("previous");
			allRoles.add(prevLink);
		}
		
		if (authService.hasNextRole(pag + 1, epp))
		{
			int next = pag + 1;
			allRoles.setNext(next);
			Link nextLink = new Link(baseURL + "?pag=" + next + "&epp=" + epp).withRel("next");
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

	@RequestMapping(value = recordDetails, method = RequestMethod.GET)
	public RoleDTO findRoleById(@PathVariable int roleId) throws RecordNotFoundException
	{
		Role role = authService.searchRoleByRoleId(roleId);
		RoleDTO roleResponse = new RoleDTO(role);
		
		Link roleLink = linkTo(methodOn(RoleRestController.class).findRoleById(roleId)).withSelfRel();
		roleResponse.add(roleLink);
		
		return roleResponse;
	}

	@RequestMapping(value = baseURI, method = RequestMethod.POST)
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
	
	@RequestMapping(value = baseURI, method = RequestMethod.PUT)
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
	
	@RequestMapping(value = recordDetails, method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void deleteRole(@PathVariable int roleId) throws RecordNotFoundException, RecordNotDeletedException
	{
		// TODO: Make sure to throw an exception if the role is not deleted
		authService.deleteRole(roleId);
	}
	
	
	// TODO: Add an exception handler for record not found
	// TODO: Add an exception handler for record not created
	// TODO: Add an exception handler for record not deleted
}
