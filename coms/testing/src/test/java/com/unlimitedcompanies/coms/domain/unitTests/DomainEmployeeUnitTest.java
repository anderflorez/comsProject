package com.unlimitedcompanies.coms.domain.unitTests;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import com.unlimitedcompanies.coms.domain.employee.Employee;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.User;

class DomainEmployeeUnitTest
{

	@Test
	public void createEmployeeTest()
	{
		Contact contact = new Contact("John", null, "Doe", "jdoe@example.com");
		new User("jdoe", "123", contact);
		Employee employee = new Employee(contact);

		assertEquals("John", employee.getContact().getFirstName());
	}

}
