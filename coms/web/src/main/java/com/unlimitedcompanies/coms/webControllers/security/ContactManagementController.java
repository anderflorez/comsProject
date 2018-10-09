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
		Contact contact = new Contact(null, null, null, null);
		String error = null;
		
		if (cId.equals("0"))
		{
			ModelAndView mv = new ModelAndView("/pages/security/contactManagement.jsp");
			contact.removeContactId();
			mv.addObject("contact", contact);
			mv.addObject("error", error);
			return mv;
		}
		else
		{
			try
			{
				contact = contactService.searchContactById(cId);
			} catch (NoResultException e)
			{
				error = "The contact couldn't be found";
			}			
			ModelAndView mv = new ModelAndView("/pages/security/contactManagement.jsp");
			mv.addObject("contact", contact);
			mv.addObject("error", error);
			return mv;
		}
		
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processContact(Contact contact)
	{	
		if (!contact.getContactId().isEmpty() && contact.getContactId() != null && !contact.getFirstName().isEmpty())
		{
			try
			{
				contactService.updateContact(contact.getContactId(), contact);
			} 
			catch (NoResultException e)
			{
				ModelAndView mv = new ModelAndView("redirect:/contacts");
				mv.addObject("error", "The contact you are editing couldn't be found");
				return mv;
			}
			
			return new ModelAndView("redirect:/contactDetail?c=" + contact.getContactId());
		} 
		else if (!contact.getFirstName().isEmpty() && contact.getFirstName() != null)
		{
			Contact savedContact = contactService.saveContact(new Contact(contact));
			return new ModelAndView("redirect:/manageContact?c=" + savedContact.getContactId());
		}
		else 
		{
			ModelAndView mv = new ModelAndView("redirect:/contacts");
			mv.addObject("error", "Error: There is not enough information to save the contact");
			return mv;
		}
	}
}
