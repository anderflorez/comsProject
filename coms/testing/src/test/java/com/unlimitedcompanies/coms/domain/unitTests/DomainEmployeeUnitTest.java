package com.unlimitedcompanies.coms.domain.unitTests;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;

import com.unlimitedcompanies.coms.domain.employees.Employee;
import com.unlimitedcompanies.coms.domain.projects.Project;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.User;

class DomainEmployeeUnitTest
{

	@Test
	public void createEmployeeTest()
	{
		Contact contact = new Contact("John", null, "Doe", "jdoe@example.com");
		User user = new User("jdoe", "123".toCharArray(), contact);
		Employee employee = new Employee(user);

		assertEquals("John", employee.getUser().getContact().getFirstName());
	}
	
	@Test
	public void assignEmployeeAsProjectManagerTest()
	{
		Contact contact = new Contact("John", null, "Doe", "jdoe@example.com");
		User user = new User("jdoe", "123".toCharArray(), contact);
		Employee employee = new Employee(user);
		Project project = new Project(19502, "Testing One");
		
		employee.assignAsProjectManager(project);

		assertEquals("John", project.getProjectManagers().get(0).getUser().getContact().getFirstName());
		
	}

}
