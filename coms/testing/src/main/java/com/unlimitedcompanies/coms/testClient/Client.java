package com.unlimitedcompanies.coms.testClient;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.service.security.SecuritySetupService;

public class Client
{
	public static void main(String[] args)
	{
		// TODO: Create an integration test for general searches
//		System.out.println(" =========== Search ===========");
//		Search search = new Search("Role");
//		search.join("Role", "users", "User");
//		search.join("User", "contact", "Contact");
//		search.addField("Role", "roleId");
//		search.addField("Role", "roleName");
//		search.addField("User", "username");
//		search.addField("Contact", "firstName");
//		search.where("User", "username", "administrator", Operator.EQUALS);
//		ConditionGroup conds = new ConditionGroup(Method.OR);
//		conds.addCondition("Role", "roleId", 2, Operator.EQUALS);
//		conds.addCondition("Contact", "firstName", "Administrator", Operator.EQUALS);
//		search.where(conds);
//		System.out.println(search);
		
		AnnotationConfigApplicationContext container = new AnnotationConfigApplicationContext();
		container.getEnvironment().setActiveProfiles("production");
		container.register(ApplicationConfig.class);
		container.refresh();
		
		SecuritySetupService setupService = container.getBean(SecuritySetupService.class);
//		AuthService authService = container.getBean(AuthService.class);
		
		setupService.initialSetup();
		setupService.checkAllResources();
		
//		int roleId = authService.findRoleByRoleName("Administrators").getRoleId();
//		Role role = authService.findRoleByIdWithMembers(roleId);
		
//		Role testRole = (Role) authService.superSearch(search);
		
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


// MAIN SYSTEM AND SECURITY

// Week 1
// TODO: Create unit tests for queries with single string results (select), joins and (where) conditions
// TODO: Create integration tests for queries with single string results (select), joins and (where) conditions
// TODO: Delete previous condition system and its classes
// TODO: Create a relationship between the saved search system and the permissions to stored condition permissions

// TODO: Create service to store single string result saved searches with their view
// TODO: Create service to store object result saved searches with their view
// TODO: Create service to read single string result saved searches with their view
// TODO: Create service to read object result saved searches with their view

// TODO: Create service to edit single string result saved searches with their view
// TODO: Create service to edit object result saved searches with their view
// TODO: Create service to delete single string result saved searches with their view
// TODO: Create service to delete object result saved searches with their view

// TODO: Add exceptions for all errors that must be handled

// TODO: Develop missing functionality referenced in the todo's

// Week 2
// TODO: Create service to store full and denied permissions
// TODO: Create service to read full and denied permissions
// TODO: Add service to create conditioned permissions

// TODO: Add service to read condition permissions
// TODO: Create service to manage full, conditioned and denied permissions (edit and delete)

// TODO: Create a web service to receive and create permissions
// TODO: Create a web service to read and return permissions

// TODO: Create web service for permission management

// TODO: Design and implement client logic to send permission create requests
// TODO: Design and implement client logic to send permission read requests
// TODO: Design and implement client logic to send permission management requests

// Week 3
// TODO: Design and implement web UI to create permissions

// TODO: Design and implement web UI to display permissions

// TODO: Design and implement web UI to manage permissions

// TODO: Clean up client project

// TODO: Design a task system
// TODO: Design and implement a database to store tasks
// TODO: Create domain classes, dao and services necessary for tasks

// Week 4
// TODO: Use the software to assign administrative permissions to administrators
// TODO: Complete all the pending todo's
// TODO: Check all the project for extra testing needed and implement it
// TODO: if possible design and implement a logging system
// TODO: Wrapp up the project



// EMPLOYEE INFORMATION MANAGEMENT SYSTEM

// TODO: Design the employee information system
// TODO: Tentative design: Create the system with a list of available positions

// TODO: Design and implement the database to store employee information
// TODO: Design and implement the database to store new applicant information
// TODO: Design and implement a relation between applications and employees

// TODO: Create the necessary domain classes for employees and new applications
// TODO: Create dao and services to store employees and new applications
// TODO: Create web service that will allow the creation of new applications and employees
// TODO: Programmatically create and assign corresponding tasks 

// TODO: Create services that will allow to read and manage applications and employees
// TODO: Create web services that will allow the management of applications and employees
// TODO: Programmatically manage corresponding tasks

// TODO: Clean and wrap up the module
