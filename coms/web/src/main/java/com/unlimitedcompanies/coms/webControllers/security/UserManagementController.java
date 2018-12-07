package com.unlimitedcompanies.coms.webControllers.security;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotCreatedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.AuthService;
import com.unlimitedcompanies.coms.service.security.ContactService;
import com.unlimitedcompanies.coms.webFormObjects.UserForm;
import com.unlimitedcompanies.coms.webappSecurity.AuthenticatedUserDetail;

@Controller
@RequestMapping("/manageUser")
public class UserManagementController
{
	@Autowired
	AuthenticatedUserDetail authUser;
	
	@Autowired
	AuthService authService;
	
	@Autowired
	ContactService contactService;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showUserDetails(@RequestParam(value = "u", required = false) String uId, 
										@RequestParam(value = "c", required = false) Integer cId) throws RecordNotFoundException
	{
		ModelAndView mv = new ModelAndView("/pages/security/userManagement.jsp");
		UserForm userForm = null;
		
		if (uId == null)
		{
			// Request the form to create a new user
			if (cId == null)
			{
				// The request is missing the contact information
				mv.setViewName("/contacts");
				mv.addObject("error", "Error: There is not enough information about the contact to create a new user");
			}
			else
			{
				// Display the form to create a new user
				try
				{
					Contact contact = contactService.searchContactById(cId);
					userForm = new UserForm();
					userForm.setContactId(contact.getContactId());
					mv.addObject("userForm", userForm);
				} catch (RecordNotFoundException e)
				{
					mv.setViewName("/contacts");
					mv.addObject("error", "Error: The contact being used to create the new user could not be found");
				}
			}
		}
		else 
		{
			// Request the form to edit an existing user
			int userId;
			try
			{
				userId = Integer.valueOf(uId);
				User user = authService.searchUserByUserId(userId);
				userForm = new UserForm(user);
				mv.addObject("userForm", userForm);
			} 
			catch (NumberFormatException e)
			{
				mv.setViewName("/users");
				mv.addObject("error", "Error: The user information provided is invalid");
			}
			catch (NoResultException e)
			{
				mv.setViewName("/users");
				mv.addObject("error", "Error: The user to be updated could not be found");
			}
		}
		
		mv.addObject("user", authUser.getUser());
		return mv;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processUsers(UserForm userForm) throws RecordNotFoundException, RecordNotCreatedException
	{
		ModelAndView mv = new ModelAndView();
		
		if (userForm.getUserId() == null)
		{
			// This is a request to create a new user
			try
			{
				//Get the needed info
				Contact contact = contactService.searchContactById(userForm.getContactId());
				
				char[] password;
				if (userForm.getPassword1().equals(userForm.getPassword2())) {
					PasswordEncoder pe = new BCryptPasswordEncoder();
					password = pe.encode(userForm.getPassword1()).toCharArray();
				}
				else 
				{
					// TODO: Display a validation error message
					return new ModelAndView("redirect:/manageUser", "c", userForm.getContactId());
				}
				
				//Create and store the new user
				User newUser = authService.saveUser(new User(userForm.getUsername(), "mypass".toCharArray(), contact));
				newUser.setPassword(password);
				// TODO: Clear the password from the char password
				mv.setViewName("/userDetail?u=" + newUser.getUserId());
				
			} 
			catch (RecordNotFoundException e)
			{
				mv.setViewName("/contacts");
				mv.addObject("error", "Error: The contact provided could not be found");
			} 
			
		} 
		else
		{
			// This is a request to update an existing user
			if (userForm.getUsername() != null)
			{
				User user = new User(userForm.getUsername(), "mypass".toCharArray(), null);
				user.setEnabled(userForm.getEnabled());
				user = authService.updateUser(user);
				mv.setViewName("/userDetail");
				mv.addObject("u", user.getUserId());
			}
			else {
				// TODO: Display a validation error message
				mv.setViewName("/manageUser");
				mv.addObject("u", userForm.getUserId());
				mv.addObject("error", "Error: There is not enough information to process the update request");
			}
		}
		
		mv.addObject("user", authUser.getUser());
		return mv;

	}
	
}
