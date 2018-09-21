//package com.unlimitedcompanies.coms.webControllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//import com.unlimitedcompanies.coms.domain.security.User;
//import com.unlimitedcompanies.coms.securityService.AuthenticationService;
//import com.unlimitedcompanies.coms.webappSecurity.AuthenticatedUserFacade;
//
//@Component
//public class AuthenticatedUserFacadeImpl implements AuthenticatedUserFacade
//{
//	@Autowired
//	private AuthenticationService authenticationService;
//
//	@Override
//	public User authenticatedUser()
//	{
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		User loggedUser = null;
//		if (!(authentication instanceof AnonymousAuthenticationToken))
//		{
//			String currentUserName = authentication.getName();
//			loggedUser = authenticationService.findUserByUsernameWithContact(currentUserName);
//		}
//		return loggedUser;
//	}
//
//}
