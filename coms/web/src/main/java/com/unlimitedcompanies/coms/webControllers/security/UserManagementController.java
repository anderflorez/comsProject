package com.unlimitedcompanies.coms.webControllers.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.securityService.AuthenticationService;

@Controller
@RequestMapping("/users")
public class UserManagementController
{
	@Autowired
	AuthenticationService authenticationService;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showUsers()
	{
		List<User> users = authenticationService.findAllUsers();
		ModelAndView mv = new ModelAndView("/app/userManagement.jsp");
		mv.addObject("userObject", new User(null, null, null));
		mv.addObject("users", users);
		return mv;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processUsers(@RequestParam("objectPassword1") String pass1,
									 @RequestParam("objectPassword2") String pass2,
									 User userObject)
	{
		return new ModelAndView();
	}
}
