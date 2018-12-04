package com.unlimitedcompanies.coms.ws.security.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.securityService.AuthService;
import com.unlimitedcompanies.coms.ws.security.reps.UserRep;

@RestController
public class LoggedUserInformationController
{
	@Autowired
	AuthService authService;
	
	@RequestMapping("/rest/loggedUserInfo")
	public UserRep getUserInfo()
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		User user = authService.searchUserByUsernameWithContact(userDetails.getUsername());
		UserRep loggedUser = new UserRep(user.getUsername(), user.getContact().getFirstName(), user.getContact().getLastName());
		
		return loggedUser;
	}
}
