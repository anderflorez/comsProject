package com.unlimitedcompanies.coms.webControllers.security;

import javax.persistence.NoResultException;

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
		Contact contact = null;
		String error = null;
		
		// TODO: show error if object is not found
		try
		{
			contact = contactService.findContactById(cId);
		} catch (NoResultException e)
		{
			error = "The contact couldn't be found";
		}
		
		ModelAndView mv = new ModelAndView("/pages/security/contactManagement.jsp");
		mv.addObject("contact", contact);
		mv.addObject("error", error);
		
		return mv;
	}
	
//	@RequestMapping(method = RequestMethod.POST)
//	public ModelAndView processContact(Contact contact)
//	{	
//		if (contact.getContactId() != null)
//		{
//			contactService.updateContact(contact.getContactId(), contact);
//			return new ModelAndView("redirect:/manageContact?c=" + contact.getContactId());
//		} else
//		{
//			contactService.saveContact(contact);
//			if (contact.getEmail() != null)
//			{
//				Contact foundContact = contactService.findContactByEmail(contact.getEmail());
//				return new ModelAndView("redirect:/manageContact?c=" + foundContact.getContactId());
//			}
//			return new ModelAndView("redirect:/contacts");
//		}
//	}
}
