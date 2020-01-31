package com.unlimitedcompanies.coms.domain.unitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalField;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.domain.abac.ResourceField;
import com.unlimitedcompanies.coms.domain.security.Contact;
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
		Contact contact1 = new Contact("John", null, "Doe", "john@example.com");
		Contact contact2 = new Contact("John", null, "", "john@example.com");
		assertEquals(contact1, contact2, "The equal contact test failed");
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
	
	@Test
	public void createNewUserPasswordEncryptionTest()
	{
		User user = new User("testUser", "testPassword", new Contact("Test", null, "User"));
		assertTrue(user.isPassword("testPassword"));
	}
	
	@Test
	public void updateUserPasswordEncryptionTest()
	{
		User user = new User("testUser", "testPassword", new Contact("Test", null, "User"));
		user.setPassword("TestPassword");
		assertTrue(user.isPassword("TestPassword"));
	}
	
	@Test
	public void datesForUsersFormatingTesting()
	{
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		User user = new User("admin", "mypass", contact);
		
		ZonedDateTime createdDate = user.getDateAdded().withZoneSameInstant(ZoneId.systemDefault());
		
		String monthDay = "";
		if (createdDate.getDayOfMonth() < 10) monthDay += "0";
		monthDay += createdDate.getDayOfMonth();
		String initialDate = createdDate.getMonth() + " " + monthDay + ", " + createdDate.getYear();
		
		ZonedDateTime tmp = user.getLastAccess().withZoneSameInstant(ZoneId.systemDefault());
		monthDay = "";
		if (tmp.getDayOfMonth() < 10) monthDay += "0";
		monthDay += tmp.getDayOfMonth();
		int hour = tmp.getHour();
		int minute = tmp.getMinute();
		String timeOfDay = "AM";
		if (hour > 11) timeOfDay = "PM";
		if (hour > 12) hour -= 12;
		String stringHour = hour < 10 ? "0" : "";
		stringHour += Integer.valueOf(hour);
		String stringMinute = minute < 10 ? "0" : "";
		stringMinute += Integer.valueOf(minute);
		String initialAccess = tmp.getMonth() + " " + monthDay + ", " + tmp.getYear() 
								+ " " + stringHour + ":" + stringMinute + " " + timeOfDay;
		
		assertTrue(initialDate.equalsIgnoreCase(user.getClientLocalDateAdded()));
		assertTrue(initialAccess.equalsIgnoreCase(user.getClientLocalLastAccess()));
	}
	
	@Test
	public void updateDateTest()
	{
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		User user = new User("admin", "mypass", contact);
		
		ZonedDateTime createdTemp = user.getDateAdded();
		ZonedDateTime accessTemp = user.getLastAccess();
		
		user.setLastAccess(ZonedDateTime.now());

		assertTrue(createdTemp.isEqual(user.getDateAdded()));
		assertTrue(accessTemp.isBefore(user.getLastAccess()));
	}
	
	@Test
	public void equalUserTest()
	{
		Contact contact1 = new Contact("John", null, "Doe", "john@example.com");
		User user1 = new User("John", "mypass", contact1);
		
		Contact contact2 = new Contact("Jane", null, "Doe", "jane@example.com");
		User user2 = new User("John", "mypass", contact2);
		
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