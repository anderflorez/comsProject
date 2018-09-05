package com.unlimitedcompanies.coms.webControllers.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.securityService.ContactService;

@Controller
public class ViewSecurityObjectController {
	
	@Autowired
	ContactService contactService;
	
	@RequestMapping("/contacts")
	public ModelAndView showContacts()
	{
		List<Contact> allContacts = contactService.findAllContacts();
		
		ModelAndView mav = new ModelAndView("/pages/security/contactView.jsp");
		mav.addObject("contacts", allContacts);
		
		return mav;
	}
	
}
