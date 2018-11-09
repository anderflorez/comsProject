package com.unlimitedcompanies.coms.ws.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter
{	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception
	{
		System.out.println("\n\n========> Checking if tokekn is authenticated: \n" + security.checkTokenAccess("isAuthenticated()") + "\n\n");		
		security.checkTokenAccess("isAuthenticated()");
	}
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception
	{
		clients.inMemory()
					.withClient("comsClient")
					.authorizedGrantTypes("authorization_code")
					.authorities("TRUSTED_CLIENT")
					.scopes("read", "write", "trusted")
					.secret("{noop}somesecret")
					.redirectUris("http://localhost:8080/coms/contacts");
	}

}
