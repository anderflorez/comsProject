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
import com.unlimitedcompanies.coms.securityServiceExceptions.ContactNotFoundException;
import com.unlimitedcompanies.coms.webappSecurity.AuthenticatedUserDetail;

@Controller
@RequestMapping("/manageContact")
public class ContactManagementController
{
	@Autowired
	AuthenticatedUserDetail authUser;
	
	@Autowired
	ContactService contactService;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showContactDetails(@RequestParam("c") String cId)
	{	
		ModelAndView mv = new ModelAndView("/pages/security/contactManagement.jsp");
		Contact contact = new Contact(null, null, null, null);
		String error = null;
		
		if (cId.equals("0"))
		{
			contact.removeContactId();
		}
		else
		{
			try
			{
				contact = contactService.searchContactById(cId);
			} catch (ContactNotFoundException e)
			{
				error = "The contact couldn't be found";
			}
			
		}
		
		mv.addObject("contact", contact);
		mv.addObject("error", error);
		mv.addObject("user", authUser.getUser());
		return mv;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processContact(Contact contact)
	{	
		if (!contact.getContactId().isEmpty() && contact.getContactId() != null && !contact.getFirstName().isEmpty())
		{
			// This is a request to update an existing contact information
			try
			{
				contactService.updateContact(contact.getContactId(), contact);
			} 
			catch (NoResultException e)
			{
				ModelAndView mv = new ModelAndView("redirect:/contacts");
				mv.addObject("error", "The contact you are editing couldn't be found");
				mv.addObject("user", authUser.getUser());
				return mv;
			}
			
			ModelAndView mv = new ModelAndView("redirect:/contactDetail");
			mv.addObject("c", contact.getContactId());
			mv.addObject("user", authUser.getUser());
			return mv;
		} 
		else if ((contact.getContactId().isEmpty() || contact.getContactId() == null) && !contact.getFirstName().isEmpty())
		{
			// This is a request to create a new contact
			Contact savedContact = contactService.saveContact(new Contact(contact));
			ModelAndView mv = new ModelAndView("redirect:/contactDetail");
			mv.addObject("c", savedContact.getContactId());
			mv.addObject("user", authUser.getUser());
			return mv;
		}
		else 
		{
			ModelAndView mv = new ModelAndView("redirect:/contacts");
			mv.addObject("error", "Error: There is not enough information to save the contact");
			mv.addObject("user", authUser.getUser());
			return mv;
		}
	}
}
