package com.unlimitedcompanies.coms.client;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.AbstractEnvironment;

import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.securityService.AuthenticationService;

public class Client
{
	public static void main(String[] args)
	{
//		ClassPathXmlApplicationContext container = new ClassPathXmlApplicationContext("testApplication.xml");
		
		System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "integrationTesting");
		AnnotationConfigApplicationContext container = new AnnotationConfigApplicationContext(ApplicationConfig.class);
		AuthenticationService authenticationService = container.getBean(AuthenticationService.class);
		
		Role role = authenticationService.saveRole(new Role("Administrator"));
		System.out.println("============================Saved role: " + role.getRoleName());
		
		container.close();
	}

}
