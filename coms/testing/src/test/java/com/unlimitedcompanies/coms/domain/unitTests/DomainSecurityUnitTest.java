package com.unlimitedcompanies.coms.domain.unitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.domain.abac.ResourceField;
import com.unlimitedcompanies.coms.domain.security.ContactAddress;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.ContactPhone;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.domain.security.exen.InvalidPhoneNumberException;

class DomainSecurityUnitTest
{
	@Test
	public void contactNotEqualTest()
	{
		Contact contact1 = new Contact("John", null, "Doe", "john@example.com");
		Contact contact2 = new Contact("John", null, "Doe", "john@sample.com");
		assertNotEquals(contact1, contact2, "The not equal contact test failed");
		
		Contact contact3 = new Contact("John", null, "Doe", null);
		Contact contact4 = new Contact("Jane", null, "Roe", null);
		assertNotEquals(contact3, contact4, "The not equal contact test failed");
		
		Contact contact5 = new Contact("John", null, "Doe", null);
		Contact contact6 = new Contact("John", null, null, null);
		assertNotEquals(contact5, contact6, "The not equal contact test failed");
	}
	
	@Test
	public void contactEqualsTest()
	{
		// TODO: This test must be improved
		Contact contact1 = new Contact("John", null, "Doe", "john@example.com");
		Contact contact2 = new Contact("John", null, "", "john@example.com");
		assertEquals(contact1, contact2, "The equal contact test failed");
	}
	
	@Test
	public void bidirectionalContactAndUserRelationshipTest()
	{
		Contact contact = new Contact("John", null, "Doe");
//		User user = new User();
	}
	
	@Test
	public void addressEqualsTest()
	{
		Contact contact1 = new Contact("John", null, "Doe", "john@example.com");
		Contact contact2 = new Contact("Jane", null, "Doe", "jane@example.com");
		contact1.setAddress("0000 myway ave", "my city", "FL", "99999");
		contact2.setAddress("0000 myway ave", "my city", "FL", "99999");
		
		assertEquals(contact1.getAddress(), contact2.getAddress());
	}
	
	@Test
	public void phoneEqualTest() throws InvalidPhoneNumberException
	{
		Contact contact1 = new Contact("John", null, "Doe", "john@example.com");
		Contact contact2 = new Contact("Jane", null, "Doe", "jane@example.com");
		contact1.addPhone("9998887766", null, null);
		contact2.addPhone("9998887766", "", null);
		
		assertEquals(contact1.getPhones().get(0), contact2.getPhones().get(0));
	}
	
	@Test
	public void phoneNotEqualTest() throws InvalidPhoneNumberException
	{
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		contact.addPhone("9998887766", null, null);
		contact.addPhone("9998887766", "111", null);
		
		assertNotEquals(contact.getPhones().get(0), contact.getPhones().get(1));
	}
	
	@Test
	public void phoneWithInvalidNumberTest()
	{
		//TODO: Create a good validation test for phone numbers
		
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		
		assertThrows(InvalidPhoneNumberException.class, 
					 () -> contact.addPhone("(999)9999999", null, null));
	}
	
	// TODO: Create more testing for users and other classes to check for constraints such as userWithNullUsernameNotAllowed
	
	@Test
	public void datesForUsersFormatingTesting()
	{
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		User user = new User("admin", "mypass".toCharArray(), contact);
		
		System.out.println("=====> Date Added: " + user.getDateAdded());
		System.out.println("=====> Last Access: " + user.getLastAccess());
		
		System.out.println("=====> Date Added: " + user.getClientLocalDateAdded());
		System.out.println("=====> Last Access: " + user.getClientLocalLastAccess());
		
		// TODO: Create some good checking for date and date time
		// TODO: Create some good translating between MySQL dates and times and Java dates and times		
	}
	
	@Test
	public void equalUserTest()
	{
		Contact contact1 = new Contact("John", null, "Doe", "john@example.com");
		User user1 = new User("John", "mypass".toCharArray(), contact1);
		
		Contact contact2 = new Contact("Jane", null, "Doe", "jane@example.com");
		User user2 = new User("John", "mypass".toCharArray(), contact2);
		
		assertEquals(user1, user2, "Domain unit test for equal users failed");
	}
	
	@Test
	public void roleEqualsTest()
	{
		Role role1 = new Role("TESTING_ROLE");
		Role role2 = new Role("TESTING_ROLE");
		
		assertEquals(role1, role2, "Domain Unit test for role equality failed");
	}
	
	@Test
	public void resourceFieldNotEqualsTest()
	{
		Resource resource1 = new Resource("resource1");
		Resource resource2 = new Resource("resource2");
		ResourceField resourceField1 = new ResourceField("field", false, resource1);
		ResourceField resourceField2 = new ResourceField("field", false, resource2);
		
		assertNotEquals(resourceField1, resourceField2, "Unit test for resourceFieldNotEqualsTest failed");
	}

}