package com.unlimitedcompanies.coms.webControllers.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.securityService.AuthService;
import com.unlimitedcompanies.coms.securityService.ContactService;
import com.unlimitedcompanies.coms.webappSecurity.AuthenticatedUserDetail;

@Controller
public class ViewSecurityObjectController {
	
	@Autowired
	ContactService contactService;
	
	@Autowired
	AuthService authService;
	
	@Autowired
	AuthenticatedUserDetail userDetail;
	
	@RequestMapping("/contacts")
	public ModelAndView showContacts()
	{
		List<Contact> allContacts = contactService.searchAllContacts();
		ModelAndView mv = new ModelAndView("/pages/security/contactView.jsp");
		mv.addObject("contacts", allContacts);
		mv.addObject("user", userDetail.getUser());
		
		return mv;
	}
	
//	@RequestMapping("/users")
//	public ModelAndView showUsers()
//	{
//		List<User> allUsers = authService.findAllUsers();
//		
//		return new ModelAndView("/pages/security/userView.jsp", "users", allUsers);
//	}
//	
//	@RequestMapping("/roles")
//	public ModelAndView showRoles()
//	{
//		List<Role> allRoles = authService.findAllRoles();
//		
//		return new ModelAndView("/pages/security/roleView.jsp", "roles", allRoles);
//	}
}
