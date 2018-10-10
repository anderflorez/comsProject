package com.unlimitedcompanies.coms.webControllers.security;

import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.securityService.AuthService;
import com.unlimitedcompanies.coms.securityService.ContactService;
import com.unlimitedcompanies.coms.webFormObjects.UserForm;
import com.unlimitedcompanies.coms.webappSecurity.AuthenticatedUserDetail;

@Controller
public class ViewSecurityObjectController {
	
	@Autowired
	ContactService contactService;
	
	@Autowired
	AuthService authService;
	
	@Autowired
	AuthenticatedUserDetail authUser;
	
	@RequestMapping("/contacts")
	public ModelAndView showContacts(@RequestParam(name = "error", required = false) String error)
	{
		List<Contact> allContacts = contactService.searchAllContacts();
		ModelAndView mv = new ModelAndView("/pages/security/contactView.jsp");
		mv.addObject("contacts", allContacts);
		mv.addObject("user", authUser.getUser());
		if (error != null)
		{
			mv.addObject("error", error);
		}
		
		return mv;
	}
	
	@RequestMapping("/contactDetail")
	public ModelAndView showContactDetails(@RequestParam(name = "c") String id)
	{
		Contact contact;
		ModelAndView mv = new ModelAndView("/pages/security/contactDetails.jsp");
		try
		{
			contact = contactService.searchContactById(id);
		} 
		catch (NoResultException e)
		{
			contact = new Contact(null, null, null, null);
			mv.addObject("error", "The contact could not be found");
		}
		mv.addObject("contact", contact);
		return mv;
	}
	
	@RequestMapping("/users")
	public ModelAndView showUsers(@RequestParam(name = "error", required = false) String error)
	{
		List<User> allUsers = authService.searchAllUsers();
		ModelAndView mv = new ModelAndView("/pages/security/userView.jsp");
		mv.addObject("users", allUsers);
		mv.addObject("user", authUser.getUser());
		if (error != null)
		{
			mv.addObject("error", error);
		}
		return mv;
	}
	
	@RequestMapping("/userDetail")
	public ModelAndView showUserDetails(@RequestParam("u") String id)
	{
		// TODO: Check for possible invalid input for u
		
		int userId = Integer.valueOf(id);
		UserForm userForm;
		ModelAndView mv = new ModelAndView("/pages/security/userDetails.jsp");
		try
		{
			userForm = new UserForm(authService.searchUserByUserId(userId));
		} 
		catch (NoResultException e)
		{
			userForm = new UserForm();
			mv.addObject("error", "The user could not be found");
		}
		mv.addObject("userForm", userForm);
		return mv;
	}
	
//	@RequestMapping("/roles")
//	public ModelAndView showRoles()
//	{
//		List<Role> allRoles = authService.findAllRoles();
//		
//		return new ModelAndView("/pages/security/roleView.jsp", "roles", allRoles);
//	}
}
