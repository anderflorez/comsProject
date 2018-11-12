//package com.unlimitedcompanies.coms.ws.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
//
//@Configuration
//@EnableAuthorizationServer
//public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter
//{	
//
//	@Override
//	public void configure(ClientDetailsServiceConfigurer clients) throws Exception
//	{
//		clients.inMemory()
//					.withClient("comsClient")
//					.authorizedGrantTypes("authorization_code")
//					.authorities("TRUSTED_CLIENT")
//					.scopes("read", "write", "trusted")
//					.secret("{noop}somesecret")
//					.redirectUris("http://localhost:8080/coms/contacts");
//	}
//
//}
