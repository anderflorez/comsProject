package com.unlimitedcompanies.coms.ws.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
public class OAuth2ServerConfiguration
{
	private static final String SERVER_RESOURCE_ID = "oauth2-server";
	private static InMemoryTokenStore tokenStore = new InMemoryTokenStore();
	
	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter
	{		
		@Autowired
		@Qualifier("authenticationManagerBean")
		private AuthenticationManager authenticationManager;
		
		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints)
		{
			endpoints.authenticationManager(authenticationManager)
					 .tokenStore(tokenStore)
					 .approvalStoreDisabled();
		}
		
		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception
		{
			clients.inMemory()
						.withClient("comsClient")
						.authorizedGrantTypes("authorization_code")
						.authorities("TRUSTED_CLIENT")
						.scopes("read", "write", "trusted")
						// TODO: use a proper secure password
						.secret("{noop}somesecret")
						.resourceIds(SERVER_RESOURCE_ID)
						.autoApprove(true)
						.redirectUris("http://localhost:8080/coms/tokenmanager");
		}

	}
	
	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfig extends ResourceServerConfigurerAdapter
	{
		
		@Override
		public void configure(ResourceServerSecurityConfigurer resources)
		{
			resources.tokenStore(tokenStore).resourceId(SERVER_RESOURCE_ID);
		}
		
		@Override
		public void configure(HttpSecurity http) throws Exception
		{
			http.antMatcher("/rest/**")
					.authorizeRequests()
						.antMatchers("/rest/**")
//						.hasAuthority("TRUSTED_CLIENT")
						.access("#oauth2.hasScope('trusted') or #oauth2.hasScope('read') or #oauth2.hasScope('write')")
//						.access("#oauth2.hasScope('read')")
					.and().csrf().disable()
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
			
		}

	}
	
}
