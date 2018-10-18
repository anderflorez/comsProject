//package com.unlimitedcompanies.coms.wsConfig.configImpl;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Scope;
//import org.springframework.context.annotation.ScopedProxyMode;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//import com.unlimitedcompanies.coms.domain.security.User;
//import com.unlimitedcompanies.coms.securityService.AuthService;
//import com.unlimitedcompanies.coms.webappSecurity.AuthenticatedUserDetail;
//
//@Component
//@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
//public class AuthenticatedUserDetailImpl implements AuthenticatedUserDetail
//{
//	@Autowired
//	private AuthService authService;
//	
//	private User user;
//
//	@Override
//	public User getUser()
//	{
//		if (user == null)
//		{
//			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//			if (!(authentication instanceof AnonymousAuthenticationToken))
//			{
//				String currentUserName = authentication.getName();
//				user = authService.searchFullUserByUsername(currentUserName);
//			}
//		}
//		return user;
//	}
//}
