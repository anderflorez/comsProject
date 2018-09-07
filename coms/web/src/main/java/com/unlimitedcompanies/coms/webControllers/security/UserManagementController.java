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
	public ModelAndView showUserDetails(@RequestParam("u") String uId, @RequestParam("c") String cId)
	{
		Integer uid = Integer.valueOf(uId);
		Integer cid = Integer.valueOf(cId);
		UserForm userForm = null;
		
		if (uid > 0)
		{
			User user = authenticationService.findUserByUserId(uid);
			userForm = new UserForm(user);
		} 
		else if (uid == 0 && cid > 0)
		{
			userForm = new UserForm();
			userForm.setContactId(cid);
		}
		
		return new ModelAndView("/pages/security/userManagement.jsp", "userForm", userForm);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processUsers(UserForm userForm)
	{
		System.out.println("================ In processUsers() ================");
		
		if (userForm.getUserId() != null)
		{
			User user = new User(userForm.getUsername(), null, null);
			user.setEnabled(userForm.getEnabled());
			user = authenticationService.updateUser(userForm.getUserId(), user);
			userForm = new UserForm(user);
			System.out.println("userForm contact id: " + userForm.getContactId());
			System.out.println(user);
		} 
		else
		{
			//Find the contact and the password
			// TODO: send some error if contact cannot be found
			Contact contact = contactService.findContactById(userForm.getContactId());
			String password = null;
			
			if (userForm.getPassword1().equals(userForm.getPassword2())) {
				password = userForm.getPassword1();
			}
			else 
			{
				// TODO: Send some error if passwords don't match				
			}
						
			//Create new user
			try
			{
				User newUser = authenticationService.saveUser(new User(userForm.getUsername(), password, contact));
				userForm = new UserForm(newUser);
			} catch (NonExistingContactException e)
			{
				// TODO: Send an internal error to the user
				e.printStackTrace();
			}
			
		}
		
		return new ModelAndView("/pages/security/userManagement.jsp", "userForm", userForm);
	}
}
