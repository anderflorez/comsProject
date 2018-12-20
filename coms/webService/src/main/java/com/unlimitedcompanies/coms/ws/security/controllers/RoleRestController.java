package com.unlimitedcompanies.coms.ws.security.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	
	@RequestMapping(value = "allRecords", method = RequestMethod.GET)
	public RoleCollectionResponse displayAllllRoles(@RequestParam(name = "pag", required = false) Integer pag,
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
		
		if (authService.hasNextUser(pag + 1, epp))
		{
			int next = pag + 1;
			allRoles.setNext(next);
			Link nextLink = new Link(baseURL + "?pag=" + next + "&epp=" + epp).withRel("next");
			allRoles.add(nextLink);
		}		
				
		for (RoleDTO nextRole : allRoles.getRoles())
		{
			Link link = linkTo(methodOn(RoleRestController.class).findRoleById(nextRole.getRoleId())).withSelfRel();
			nextRole.add(link);
		}
		
		return allRoles;
	}

	@RequestMapping(value = recordDetails, method = RequestMethod.GET)
	private RoleDTO findRoleById(String roleId)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
