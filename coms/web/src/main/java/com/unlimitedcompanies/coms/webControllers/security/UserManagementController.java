package com.unlimitedcompanies.coms.webControllers.security;

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
import com.unlimitedcompanies.coms.securityService.AuthenticationService;
import com.unlimitedcompanies.coms.securityService.ContactService;
import com.unlimitedcompanies.coms.securityServiceExceptions.NonExistingContactException;
import com.unlimitedcompanies.coms.webFormObjects.UserForm;

@Controller
@RequestMapping("/manageUser")
public class UserManagementController
{
	@Autowired
	AuthenticationService authenticationService;
	
	@Autowired
	ContactService contactService;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showUserDetails(@RequestParam("u") String uId)
	{
		Integer id = Integer.valueOf(uId);
		UserForm userForm;
		
		if (id > 0)
		{
			User user = authenticationService.findUserByUserId(id);
			
		} else
		{
			user = new User(null, null, null);
		}
		
		return new ModelAndView("/pages/security/userManagement.jsp", "user", userForm);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processUsers(UserForm user)
	{
		System.out.println("================ In processUsers() ================");
		System.out.println("initial user last access: " + user.getLastAccess());
		if (user.getUserId() != null)
		{
			authenticationService.updateUser(user.getUserId(), user);
			return new ModelAndView("/pages/security/userManagement.jsp", "user", user);
		} else
		{
			try {
				Contact contact = contactService.findContactById(3);
				String password = null;
				if (pass1.equals(pass2))
				{
					PasswordEncoder pe = new BCryptPasswordEncoder();
					password = pe.encode(pass1);
				} 
				else
				{
					// TODO: Throw an error
				}
				
				User actualUser = new User(user.getUsername(), password, contact);
				user = authenticationService.saveUser(actualUser);
			} catch (NonExistingContactException e) {
				// TODO: send an error that can be shown to the user
				e.printStackTrace();
			}
			System.out.println("new user last access: " + user.getLastAccess());
			return new ModelAndView("/pages/security/userManagement.jsp", "user", user);
		}
	}
}
