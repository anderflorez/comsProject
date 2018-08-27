package com.unlimitedcompanies.coms.webSecurity;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class AuthFailureHandler implements AuthenticationFailureHandler
{	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, 
										HttpServletResponse response,
										AuthenticationException exception) 
			throws IOException, ServletException
	{
		// Find what the username was
		String username = request.getParameter("username");
		
		// Redirect to the login page providing the username used
		response.sendRedirect(request.getContextPath() + "/app/login.jsp?error&username=" + username);
	}
	
}
