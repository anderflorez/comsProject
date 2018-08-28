package com.unlimitedcompanies.coms.webControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.securityService.ContactService;

@Controller
@RequestMapping("/contacts")
public class Contacts
{	
	@Autowired
	ContactService contactService;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showContacts()
	{
		List<Contact> allContacts = contactService.findAllContacts();
		
		ModelAndView mav = new ModelAndView("/app/contacts.jsp");
		mav.addObject("contact", new Contact(null, null, null, null));
		mav.addObject("contacts", allContacts);
		
		return mav;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView manageContacts(@RequestParam("newContactId") String conId, Contact newContact)
	{
		Integer editContactId;
		try
		{
			editContactId = Integer.valueOf(conId);
		} catch (NumberFormatException e)
		{
			editContactId = null;
		}
		
		System.out.println("=========New Contact: " + newContact.getFirstName());
		System.out.println("=========New Contact ID: " + editContactId);
		
		return showContacts();
	}
}
