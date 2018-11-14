package com.unlimitedcompanies.coms.ws.security.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unlimitedcompanies.coms.ws.security.reps.UserDetailsRep;

@RestController
public class UserInformationController
{
	@RequestMapping("/rest/userInfo")
	public UserDetailsRep getUserInfo()
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		UserDetailsRep user = new UserDetailsRep(userDetails.getUsername());
		return user;
	}
}
