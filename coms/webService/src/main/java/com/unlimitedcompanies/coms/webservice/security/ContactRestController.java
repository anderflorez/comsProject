package com.unlimitedcompanies.coms.webservice.security;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.securityService.ContactService;

@RestController
public class ContactRestController
{	
	@Autowired
	ContactService contactService;
	
	@RequestMapping("/contact/{id}")
	public Contact findCustomerById(@PathVariable String id)
	{
		//TODO: handle contact not found execption
		try
		{
			return contactService.searchContactById(id);
		} catch (NoResultException e)
		{
			e.printStackTrace();
			return null;
		}
	}

}
