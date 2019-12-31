package com.unlimitedcompanies.coms.ws.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.unlimitedcompanies.coms.data.config.ServerURLs;

@Configuration
@EnableWebSecurity
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler
{

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException
	{
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		
		// TODO: Change the hard coded address to the ServerURLs.CLIENT when no port is necessary
		if (request.getRemoteAddr().equals("192.168.1.31")) 
		{
			response.sendRedirect(ServerURLs.CLIENT.toString());
		}
		else 
		{
			response.sendRedirect(ServerURLs.PROVIDER.toString());
		}
	}

}
