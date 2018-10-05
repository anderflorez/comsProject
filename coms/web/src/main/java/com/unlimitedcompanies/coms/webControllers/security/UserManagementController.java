package com.unlimitedcompanies.coms.webControllers.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manageUser")
public class UserManagementController
{
//	@Autowired
//	AuthService authService;
//	
//	@Autowired
//	ContactService contactService;
	
//	@RequestMapping(method = RequestMethod.GET)
//	public ModelAndView showUserDetails(@RequestParam("u") String uId, @RequestParam("c") String cId)
//	{
//		// TODO: check for errors if the id is null or invalid
//		Integer uid = Integer.valueOf(uId);
//		// TODO: check for errors if the id is null or invalid
//		Integer cid = Integer.valueOf(cId);
//		UserForm userForm = null;
//		
//		if (uid > 0)
//		{
//			// TODO: show error if object is not found
//			User user = authService.findUserByUserId(uid);
//			userForm = new UserForm(user);
//		} 
//		else if (uid == 0 && cid > 0)
//		{
//			userForm = new UserForm();
//			userForm.setContactId(cid);
//		}
//		
//		return new ModelAndView("/pages/security/userManagement.jsp", "userForm", userForm);
//	}
//	
//	@RequestMapping(method = RequestMethod.POST)
//	public ModelAndView processUsers(UserForm userForm)
//	{		
//		if (userForm.getUserId() != null)
//		{
//			User user = new User(userForm.getUsername(), null, null);
//			user.setEnabled(userForm.getEnabled());
//			user = authService.updateUser(userForm.getUserId(), user);
//			userForm = new UserForm(user);
//		} 
//		else
//		{
//			//Find the contact and the password
//			// TODO: send some error if contact cannot be found
//			Contact contact = contactService.findContactById(userForm.getContactId());
//			String password = null;
//			
//			if (userForm.getPassword1().equals(userForm.getPassword2())) {
//				PasswordEncoder pe = new BCryptPasswordEncoder();
//				password = pe.encode(userForm.getPassword1());
//			}
//			else 
//			{
//				// TODO: Send some error if passwords don't match				
//			}
//						
//			//Create new user
//			try
//			{
//				User newUser = authService.saveUser(new User(userForm.getUsername(), password, contact));
//				userForm = new UserForm(newUser);
//			} catch (NonExistingContactException e)
//			{
//				// TODO: Send an internal error to the user
//				e.printStackTrace();
//			}
//			
//		}
//		
//		return new ModelAndView("redirect:/manageUser?u=" + userForm.getUserId() + "&c=0");
//	}
}
