package com.unlimitedcompanies.coms.ws.controllers.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.ws.config.OAuth2ServerConfiguration;
import com.unlimitedcompanies.coms.ws.config.RestLinks;

@RestController
public class LogoutController
{	
	@RequestMapping(value = RestLinks.URI_REST_BASE + "logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request) throws RecordNotFoundException, NoResourceAccessException
	{	
		String authHeader = request.getHeader("Authorization");
		
		if(authHeader != null)
		{
			String tokenValue = authHeader.replace("Bearer", "").trim();
			OAuth2AccessToken accessToken = OAuth2ServerConfiguration.tokenStore.readAccessToken(tokenValue);
			OAuth2ServerConfiguration.tokenStore.removeAccessToken(accessToken);
		}
		
		return "";
	}
}
