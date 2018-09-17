package com.unlimitedcompanies.coms.testClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.unlimitedcompanies.coms.dao.search.ConditionGroup;
import com.unlimitedcompanies.coms.dao.search.ConditionalOperator;
import com.unlimitedcompanies.coms.dao.search.Method;
import com.unlimitedcompanies.coms.dao.search.SearchCondition;
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
		
		SearchCondition condition1 = new SearchCondition("Role", "roleId", "2", false, ConditionalOperator.EQUAL);
		SearchCondition condition2 = new SearchCondition("User", "username", "admin", true, ConditionalOperator.EQUAL);
		SearchCondition condition3 = new SearchCondition("Contact", "firstName", "Administrator", true, ConditionalOperator.EQUAL);
		System.out.println(condition1);
		System.out.println(condition2);
		System.out.println(condition3);
		
		ConditionGroup conditionGroup = new ConditionGroup(Method.AND);
		
	}

}
