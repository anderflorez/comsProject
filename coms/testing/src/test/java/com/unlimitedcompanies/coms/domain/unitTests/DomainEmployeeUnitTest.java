package com.unlimitedcompanies.coms.domain.unitTests;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;

import com.unlimitedcompanies.coms.domain.employee.Employee;
import com.unlimitedcompanies.coms.domain.projects.Project;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.User;

class DomainEmployeeUnitTest
{

	@Test
	public void createEmployeeTest()
	{
		Contact contact = new Contact("John", null, "Doe", "jdoe@example.com");
		new User("jdoe", "123".toCharArray(), contact);
		Employee employee = new Employee(contact);

		assertEquals("John", employee.getContact().getFirstName());
	}
	
	@Test
	public void assignEmployeeAsProjectManagerTest()
	{
		Contact contact = new Contact("John", null, "Doe", "jdoe@example.com");
		new User("jdoe", "123".toCharArray(), contact);
		Employee employee = new Employee(contact);
		Project project = new Project(19502, "Testing One");
		
		employee.assignAsProjectManager(project);

		assertEquals("John", project.getProjectManagers().get(0).getContact().getFirstName());
		
	}

}
