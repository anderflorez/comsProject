package com.unlimitedcompanies.coms.testClient;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.unlimitedcompanies.coms.dao.search.ConditionGroup;
import com.unlimitedcompanies.coms.dao.search.Operator;
import com.unlimitedcompanies.coms.dao.search.Method;
import com.unlimitedcompanies.coms.dao.search.Search;
import com.unlimitedcompanies.coms.dao.search.SearchCondition;
import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
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
		
		System.out.println(" =========== Search Condition ===========");
		SearchCondition condition1 = new SearchCondition("Role", "roleId", Operator.EQUAL, 2);
		SearchCondition condition2 = new SearchCondition("User", "username", Operator.EQUAL, "administrator");
		SearchCondition condition3 = new SearchCondition("Contact", "firstName", Operator.EQUAL, "Administrator");
		System.out.println(condition1);
		System.out.println(condition2);
		System.out.println(condition3);
		
		System.out.println(" =========== Condition Group ===========");
		ConditionGroup conditionGroup = new ConditionGroup(Method.AND);
		conditionGroup.addCondition(condition1);
		ConditionGroup cg = new ConditionGroup(Method.OR);
		ConditionGroup cg2 = new ConditionGroup(Method.AND);
		cg.addCondition(condition2);
		cg.addCondition(condition3);
		conditionGroup.addConditionGroupToSet(cg);
		conditionGroup.addConditionGroupToSet(cg2);
		System.out.println(conditionGroup);
		
		System.out.println(" =========== Search ===========");
		Search search = new Search("Role");
		search.addField("Role", "roleId");
		search.addField("Role", "roleName");
	}

}
