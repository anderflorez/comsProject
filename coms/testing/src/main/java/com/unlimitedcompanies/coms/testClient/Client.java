package com.unlimitedcompanies.coms.testClient;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.unlimitedcompanies.coms.dao.securitySettings.SecuritySetup;
import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.securityService.AuthenticationService;

public class Client
{
	public static void main(String[] args)
	{
//		System.out.println(" =========== Search ===========");
//		Search search = new Search("Role");
//		search.join("Role", "users", "User");
//		search.join("User", "contact", "Contact");
//		search.addField("Role", "roleId");
//		search.addField("Role", "roleName");
//		search.addField("User", "username");
//		search.addField("Contact", "firstName");
//		search.where("User", "username", "administrator", Operator.EQUAL);
//		ConditionGroup conds = new ConditionGroup(Method.OR);
//		conds.addCondition("Role", "roleId", 2, Operator.EQUAL);
//		conds.addCondition("Contact", "firstName", "Administrator", Operator.EQUAL);
//		search.where(conds);
//		System.out.println(search);
		
		AnnotationConfigApplicationContext container = new AnnotationConfigApplicationContext();
		container.getEnvironment().setActiveProfiles("production");
		container.register(ApplicationConfig.class);
		container.refresh();
		
		SecuritySetup securitySetup = container.getBean(SecuritySetup.class);
		AuthenticationService authenticationService = container.getBean(AuthenticationService.class);
		
		securitySetup.checkAllResources();
		
//		int roleId = authenticationService.findRoleByRoleName("Administrators").getRoleId();
//		Role role = authenticationService.findRoleByIdWithMembers(roleId);
		
//		Role testRole = (Role) authenticationService.superSearch(search);
		
		container.close();
		
//		System.out.println("Found role name: " + role.getRoleName());
//		System.out.println("Found role members:");
//		for (User user : role.getMembers())
//		{
//			System.out.println("Found user member:" + user.getUsername());
//		}
		
//		System.out.println(" =========== Super Search Test ===========");
//		System.out.println(testRole.getRoleName());
	}

}
