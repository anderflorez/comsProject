package com.unlimitedcompanies.coms.ws.controllers.security;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unlimitedcompanies.coms.ws.config.RestLinks;
import com.unlimitedcompanies.coms.ws.reps.ResourceLink;
import com.unlimitedcompanies.coms.ws.reps.ResourceLinkCollection;

@RestController
public class LinkDirectoryController
{
	@RequestMapping(value = RestLinks.URI_BASE + "directory")
	public ResourceLinkCollection getDirectory()
	{
		ResourceLinkCollection resources = new ResourceLinkCollection();
		List<Link> links = new ArrayList<>();
		try
		{
			links.add(new Link("http://localhost/comsws/rest/").withRel("base_rest"));
//			links.add(linkTo(methodOn(ContactRestController.class).saveNewContact(null)).withRel("base_contact"));
//			links.add(linkTo(methodOn(UserRestController.class).saveNewUser(null)).withRel("base_user"));
//			links.add(linkTo(methodOn(RoleRestController.class).saveNewRole(null)).withRel("base_role"));
		}
		catch (Exception e) {}
		
		for (Link link : links)
		{
			resources.addResources(new ResourceLink(link.getRel(), link.getHref()));
		}
		
		return resources;
	}
}
