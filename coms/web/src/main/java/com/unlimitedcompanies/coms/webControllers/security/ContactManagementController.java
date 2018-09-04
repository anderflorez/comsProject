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
	public ModelAndView showContact(@RequestParam("c") String cId)
	{
		Integer id = Integer.valueOf(cId);
		
		Contact contact = contactService.findContactById(id);
		ModelAndView mv = new ModelAndView("/pages/security/contactManagement.jsp", "contactObject", contact);
		
		return mv;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processContact(Contact contact)
	{	
		System.out.println("========The contact id is: " + contact.getEmail());
		contactService.updateContact(contact.getContactId(), contact);
		return new ModelAndView("/contactDetails?c=" + contact.getContactId());
	}
}
