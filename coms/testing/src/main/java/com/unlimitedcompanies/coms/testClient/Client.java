package com.unlimitedcompanies.coms.testClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.securityService.AuthenticationService;

public class Client
{
	public static void main(String[] args)
	{		
		AnnotationConfigApplicationContext container = new AnnotationConfigApplicationContext();
		container.getEnvironment().setActiveProfiles("production");
		container.register(ApplicationConfig.class);
		container.refresh();
		
		AuthenticationService authenticationService = container.getBean(AuthenticationService.class);
		
		int roleId = authenticationService.findRoleByRoleName("AdministratorGroup").getRoleId();
		Role role = authenticationService.findRoleByIdWithMembers(roleId);
		
		container.close();
		
		System.out.println("Found role name: " + role.getRoleName());
		System.out.println("Found role members:");
		for (User user : role.getMembers())
		{
			System.out.println("Found user member:" + user.getUsername());
		}
		
		
		
		
		Map items = new HashMap();
		items.put("item1", "value1");
		items.put("item2", Integer.valueOf(1));
		items.put("item3", new Contact("name", null, "last", null));
		
		ArrayList<String> itemKeys = new ArrayList<String>(items.keySet());
		for (String key : itemKeys)
		{
			System.out.println("Checking the " + key + " value: " + items.get(key));
		}
	}

}
