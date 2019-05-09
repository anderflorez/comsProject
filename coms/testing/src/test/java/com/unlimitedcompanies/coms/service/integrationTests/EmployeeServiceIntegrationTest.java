package com.unlimitedcompanies.coms.service.integrationTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.domain.employee.Employee;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.service.employee.EmployeeService;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
import com.unlimitedcompanies.coms.service.security.ContactService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
@Transactional
@ActiveProfiles("integrationTesting")
class EmployeeServiceIntegrationTest
{
	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	ContactService contactService;
	
	@Test
	public void numberOfEmployeesTest()
	{
		assertEquals(0, employeeService.getNumberOfEmployees(), "Number of employees count integration test failed");
	}

	@Test
	public void storeNewEmployeeIntegrationTest() throws DuplicateRecordException
	{		
		Contact contact = contactService.saveContact(new Contact("Diane", null, null, "Diane@example.com"));
		Employee employee = new Employee(contact);
		employeeService.saveEmployee(employee);
		
		assertEquals(1, employeeService.getNumberOfEmployees(), "Storing a new employee integration test failed");
	}

}
