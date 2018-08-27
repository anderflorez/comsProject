package com.unlimitedcompanies.coms.webControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.securityService.AuthenticationService;
import com.unlimitedcompanies.coms.securityService.ContactService;
import com.unlimitedcompanies.coms.securityServiceExceptions.NonExistingContactException;

@Controller
@RequestMapping("/initialSetup")
public class CreateInitialAdminAccountController
{
	@Autowired
	ContactService contactService;
	
	@Autowired
	AuthenticationService authenticationService;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView show()
	{
		// Assure the app has no users and no roles, otherwise redirect to dashboard
		int numberOfUsers = authenticationService.findNumberOfUsers();
		int numberOfRoles = authenticationService.findNumberOfRoles();
		if (numberOfUsers == 0 && numberOfRoles == 0)
		{
			return new ModelAndView("/app/initial-setup.jsp");
		}
		else
		{
			return new ModelAndView("/app/login.jsp");
		}
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processForm(@RequestParam("password") String pass)
	{
//		//Validation check
//		if (results.hasErrors())
//		{
//			return new ModelAndView("/app/initial-setup.jsp", "adminSetup", new User(null, null, null));
//		}
		
		Role role = authenticationService.saveRole(new Role("Administrators"));
		contactService.saveContact(new Contact("Administrator", null, null, "uec_ops_support@unlimitedcompanies.com"));
		Contact contact = contactService.findContactByEmail("uec_ops_support@unlimitedcompanies.com");
		
		PasswordEncoder pe = new BCryptPasswordEncoder();
		User adminUser = new User("administrator", pe.encode(pass), contact);
		
		try
		{
			adminUser = authenticationService.saveUser(adminUser);
		} catch (NonExistingContactException e)
		{
			// TODO: Create and display a validation error message for the user
			// As the contact is created automatically by the app then this is considered an internal error
			e.printStackTrace();
		}
		
		authenticationService.assignUserToRole(role, adminUser);

		return new ModelAndView("/");
	}
}
