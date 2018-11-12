//package com.unlimitedcompanies.coms.ws.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
//
//@Configuration
//@EnableResourceServer
//public class ResourceServerConfig extends ResourceServerConfigurerAdapter
//{
//	
//	@Override
//	public void configure(HttpSecurity http) throws Exception
//	{
//		http.antMatcher("/rest/**")
//				.authorizeRequests()
//					.antMatchers("/rest/**")
////					.hasAuthority("TRUSTED_CLIENT")
////					.access("#oauth2.hasScope('trusted') or #oauth2.hasScope('read') or #oauth2.hasScope('write')")
//					.access("#oauth2.hasScope('read')")
////					.access("#oauth2.clientHasRole('TRUSTED_CLIENT')")
//				.and().csrf().disable()
//				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//		
//	}
//
//}
