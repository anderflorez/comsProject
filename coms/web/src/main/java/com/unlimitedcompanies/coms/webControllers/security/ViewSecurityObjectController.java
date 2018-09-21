package com.unlimitedcompanies.coms.webControllers.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.securityService.AuthenticationService;
import com.unlimitedcompanies.coms.securityService.ContactService;

@Controller
public class ViewSecurityObjectController {
	
	@Autowired
	ContactService contactService;
	
	@Autowired
	AuthenticationService authenticationService;
	
//	@RequestMapping("/contacts")
//	public ModelAndView showContacts()
//	{
//		List<Contact> allContacts = contactService.findAllContacts();
//		
//		return new ModelAndView("/pages/security/contactView.jsp", "contacts", allContacts);
//	}
//	
//	@RequestMapping("/users")
//	public ModelAndView showUsers()
//	{
//		List<User> allUsers = authenticationService.findAllUsers();
//		
//		return new ModelAndView("/pages/security/userView.jsp", "users", allUsers);
//	}
//	
//	@RequestMapping("/roles")
//	public ModelAndView showRoles()
//	{
//		List<Role> allRoles = authenticationService.findAllRoles();
//		
//		return new ModelAndView("/pages/security/roleView.jsp", "roles", allRoles);
//	}
}
