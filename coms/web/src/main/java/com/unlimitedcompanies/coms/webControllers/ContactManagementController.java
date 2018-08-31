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
public class ContactManagementController
{	
	@Autowired
	ContactService contactService;
	
	@RequestMapping("/contacts")
	public ModelAndView showContacts()
	{
		List<Contact> allContacts = contactService.findAllContacts();
		
		ModelAndView mav = new ModelAndView("/app/contactView.jsp");
		mav.addObject("contacts", allContacts);
		
		return mav;
	}
	
	@RequestMapping("/contactDetails")
	public ModelAndView processContacts(@RequestParam("objectId") String cId)
	{
		Integer detailedContactId;
		try
		{
			detailedContactId = Integer.valueOf(cId);
			Contact contact = contactService.findContactById(detailedContactId);
			ModelAndView mv = new ModelAndView();
			
			
			
			
			
			
			
			
			
			
		} catch (NumberFormatException e)
		{
			detailedContactId = null;
			//TODO: Send an error to be displayed in a modal
		}
		
		if (detailedContactId != null)
		{
			
		}
		else
		{
			contactService.saveContact(contactObject);
		}
		
		return showContacts();
	}
}
