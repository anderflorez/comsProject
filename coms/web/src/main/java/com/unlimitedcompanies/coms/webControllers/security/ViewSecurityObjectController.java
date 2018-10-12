package com.unlimitedcompanies.coms.webControllers.security;

import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.securityService.AuthService;
import com.unlimitedcompanies.coms.securityService.ContactService;
import com.unlimitedcompanies.coms.webFormObjects.UserForm;
import com.unlimitedcompanies.coms.webappSecurity.AuthenticatedUserDetail;

@Controller
public class ViewSecurityObjectController
{	
	@Autowired
	AuthenticatedUserDetail authUser;
	
	@Autowired
	ContactService contactService;
	
	@Autowired
	AuthService authService;
	
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
			try
			{
				authService.searchUserByContact(contact);
				mv.addObject("contactUser", true);
			} catch (NoResultException e) {}
		} 
		catch (NoResultException e)
		{
			contact = new Contact(null, null, null, null);
			mv.addObject("error", "The contact could not be found");
		}
		mv.addObject("contact", contact);
		mv.addObject("user", authUser.getUser());
		return mv;
	}
	
	@RequestMapping(value = "/deleteContact", method = RequestMethod.POST)
	public ModelAndView deleteContact(String contactId)
	{
		ModelAndView mv = new ModelAndView("/contacts");
		try
		{
			contactService.deleteContact(contactId);
		} 
		catch (NoResultException e)
		{
			mv.addObject("error", "Error: The contact to be deleted could not be found");
		}
		catch (DataIntegrityViolationException e)
		{
			mv.addObject("error", "Error: There are other items associated to this contact");
		}
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
		ModelAndView mv = new ModelAndView("/pages/security/userDetails.jsp");
		UserForm userForm;
		try
		{
			int userId = Integer.valueOf(id);
			userForm = new UserForm(authService.searchUserByUserId(userId));
		} 
		catch (NoResultException e)
		{
			userForm = new UserForm();
			mv.addObject("error", "Error: The user could not be found");
		}
		catch (NumberFormatException e)
		{
			userForm = new UserForm();
			mv.addObject("error", "Error: The user information provided is invalid");
		}
		mv.addObject("userForm", userForm);
		mv.addObject("user", authUser.getUser());
		return mv;
	}
	
	@RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
	public ModelAndView deleteUser(int userId)
	{
		ModelAndView mv = new ModelAndView("/users");
		try
		{
			authService.deleteUser(userId);
		} 
		catch (NoResultException e)
		{
			mv.addObject("error", "Error: The user to be deleted could not be found");
		}
		catch (DataIntegrityViolationException e)
		{
			mv.addObject("error", "Error: There are other items associated to this user");
		}
		return mv;
	}
	
	@RequestMapping("/roles")
	public ModelAndView showRoles()
	{
		List<Role> allRoles = authService.searchAllRoles();
		ModelAndView mv = new ModelAndView("/pages/security/roleView.jsp");
		mv.addObject("roles", allRoles);
		mv.addObject("user", authUser.getUser());
		return mv;
	}
	
	@RequestMapping("/roleDetail")
	public ModelAndView showRoleDetails(@RequestParam("r") String id)
	{
		ModelAndView mv = new ModelAndView("/pages/security/roleDetails.jsp");
		Role role;
		try
		{
			int roleId = Integer.valueOf(id);
			role = authService.searchRoleByIdWithMembers(roleId);
			mv.addObject("role", role);
			if (role.getMembers().size() > 0)
			{
				mv.addObject("userMembers", role.getMembers());
			}
		} 
		catch (NoResultException e)
		{
			role = new Role(null);
			mv.addObject("role", role);
			mv.addObject("error", "Error: The role provided could not be found");
		}
		catch (NumberFormatException e)
		{
			role = new Role(null);
			mv.addObject("role", role);
			mv.addObject("error", "Error: The role information provided is invalid");
		}
		
		mv.addObject("user", authUser.getUser());
		return mv;
	}
}
