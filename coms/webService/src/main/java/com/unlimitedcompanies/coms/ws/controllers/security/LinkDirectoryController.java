package com.unlimitedcompanies.coms.ws.controllers.security;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.AbacService;
import com.unlimitedcompanies.coms.ws.config.RestLinks;
import com.unlimitedcompanies.coms.ws.controllers.abac.PolicyRestController;
import com.unlimitedcompanies.coms.ws.reps.ResourceLink;
import com.unlimitedcompanies.coms.ws.reps.ResourceLinkCollection;

@RestController
public class LinkDirectoryController
{
	@Autowired
	AbacService abacService;
	
	@RequestMapping(value = RestLinks.URI_REST_BASE + "directory", method = RequestMethod.GET)
	public ResourceLinkCollection getDirectory()
	{
		/**
		 * This method provides a collection containing the user accessible resource 
		 * names with their corresponding links
		 */
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		
		List<String> resourceNames = abacService.allawedResources(userDetails.getUsername());
		ResourceLinkCollection resources = new ResourceLinkCollection();
		
		resourceNames.forEach(resourceName -> {
			
			try
			{
				switch(resourceName)
				{
					case "AbacPolicy":
						Link policyLink = linkTo(methodOn(PolicyRestController.class).getAllPolicies(null, null)).withRel(resourceName).expand();
						resources.addResources(new ResourceLink(policyLink.getRel(), policyLink.getHref()));
						break;
					case "Contact":
						resources.addResources(new ResourceLink("Contact", null));
						break;
					case "User":
						resources.addResources(new ResourceLink("User", null));
						break;
					case "Role":
						resources.addResources(new ResourceLink("Role", null));
						break;
				}
			}
			catch (Exception e) {}
		});	
		
		
//		List<Link> links = new ArrayList<>();
//		try
//		{
//			links.add(new Link(RestLinks.URI_REST_BASE).withRel("base_rest"));
//			links.add(linkTo(methodOn(ContactRestController.class).saveNewContact(null)).withRel("base_contact"));
//			links.add(linkTo(methodOn(UserRestController.class).saveNewUser(null)).withRel("base_user"));
//			links.add(linkTo(methodOn(RoleRestController.class).saveNewRole(null)).withRel("base_role"));
//		}
//		catch (Exception e) {}
//		
//		for (Link link : links)
//		{
//			resources.addResources(new ResourceLink(link.getRel(), link.getHref()));
//		}
		
		return resources;
	}
}
