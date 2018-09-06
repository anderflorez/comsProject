package com.unlimitedcompanies.coms.webControllers.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.securityService.ContactService;

@Controller
@RequestMapping("/manageContact")
public class ContactManagementController
{	
	@Autowired
	ContactService contactService;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showContactDetails(@RequestParam("c") String cId)
	{		
		Integer id = Integer.valueOf(cId);
		Contact contact;
		
		if (id > 0)
		{
			contact = contactService.findContactById(id);
			
		} else
		{
			contact = new Contact(null, null, null, null);
		}
		
		ModelAndView mv = new ModelAndView("/pages/security/contactManagement.jsp", "contact", contact);
		return mv;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processContact(Contact contact)
	{	
		if (contact.getContactId() != null)
		{
			contactService.updateContact(contact.getContactId(), contact);
			return new ModelAndView("/pages/security/contactManagement.jsp", "contact", contact);
		} else
		{
			contactService.saveContact(contact);
			if (contact.getEmail() != null)
			{
				Contact foundContact = contactService.findContactByEmail(contact.getEmail());
				return new ModelAndView("/pages/security/contactManagement.jsp", "contact", foundContact);
			}
			return new ModelAndView("/contacts");
		}
	}
}
