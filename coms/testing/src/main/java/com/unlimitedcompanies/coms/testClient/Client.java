package com.unlimitedcompanies.coms.testClient;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.securityService.AuthenticationService;

public class Client
{
	public static void main(String[] args)
	{		
		AnnotationConfigApplicationContext container = new AnnotationConfigApplicationContext();
		container.getEnvironment().setActiveProfiles("integrationTesting");
		container.register(ApplicationConfig.class);
		container.refresh();
		
		AuthenticationService authenticationService = container.getBean(AuthenticationService.class);
		
		Role role = authenticationService.saveRole(new Role("Administrator"));
		System.out.println("============================Saved role: " + role.getRoleName());
		
		container.close();
	}

}
