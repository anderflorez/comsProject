package com.unlimitedcompanies.coms.domain.unitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.unlimitedcompanies.coms.data.exceptions.ExistingConditionGroupException;
import com.unlimitedcompanies.coms.data.exceptions.NonExistingFieldException;
import com.unlimitedcompanies.coms.data.exceptions.IncorrectFieldFormatException;
import com.unlimitedcompanies.coms.data.exceptions.NoLogicalOperatorException;
import com.unlimitedcompanies.coms.data.query.COperator;
import com.unlimitedcompanies.coms.data.query.SearchQuery;
import com.unlimitedcompanies.coms.domain.security.Address;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Phone;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.ResourceField;
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
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		Address address1 = new Address("0000 myway ave", "my city", "FL", "99999", contact);
		Address address2 = new Address("0000 myway ave", "my town", "FL", "99999", contact);
		
		assertEquals(address1, address2, "Domain unit test for address equals failed");
	}
	
	@Test
	public void phoneEqualTest() throws InvalidPhoneNumberException
	{
		Contact contact1 = new Contact("John", null, "Doe", "john@example.com");
		Contact contact2 = new Contact("Jane", null, "Doe", "jane@example.com");
		Phone firstPhone = new Phone("9998887766", null, null, contact1);
		Phone secondPhone = new Phone("9998887766", "", null, contact2);
		
		assertEquals(firstPhone, secondPhone, "Domain test phone not equal test failed");
	}
	
	@Test
	public void phoneNotEqualTest() throws InvalidPhoneNumberException
	{
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		Phone firstPhone = new Phone("9998887766", null, null, contact);
		Phone secondPhone = new Phone("9998887766", "111", null, contact);
		
		assertNotEquals(firstPhone, secondPhone, "Domain test phone not equal test failed");
	}
	
	@Test
	public void phoneWithInvalidNumberTest()
	{
		//TODO: Create a good validation test for phone numbers
		
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		
		assertThrows(InvalidPhoneNumberException.class, 
					 () -> new Phone("(999)9999999", null, null, contact));
	}
	
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
	

	// Testing new search query functionality
	// TODO: Extract to a new class
	
	@Test
	public void searchChaininigTest() throws NonExistingFieldException
	{
		Resource userResource = new Resource("User");
		userResource.addField(new ResourceField("userId", false, userResource));
		userResource.addField(new ResourceField("username", false, userResource));
		userResource.addField(new ResourceField("enabled", false, userResource));
		userResource.addField(new ResourceField("contact", true, userResource));
		userResource.addField(new ResourceField("roles", true, userResource));
		
		Resource contactResource = new Resource("Contact");
		contactResource.addField(new ResourceField("contactId", false, contactResource));
		contactResource.addField(new ResourceField("firstName", false, contactResource));
		contactResource.addField(new ResourceField("lastName", false, contactResource));
		contactResource.addField(new ResourceField("email", false, contactResource));
		
		Resource roleResource = new Resource("Role");
		roleResource.addField(new ResourceField("roleId", false, roleResource));
		roleResource.addField(new ResourceField("roleName", false, roleResource));
		roleResource.addField(new ResourceField("permission", true, roleResource));
		
		Resource permissionResource = new Resource("Permission");
		permissionResource.addField(new ResourceField("permId", false, permissionResource));
		permissionResource.addField(new ResourceField("permName", false, permissionResource));
		
		SearchQuery userSearch = new SearchQuery(userResource);
		userSearch.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact", contactResource);
		userSearch.leftJoinFetch(userResource.getResourceFieldByName("roles"), "role", roleResource)
				  .leftJoinFetch(roleResource.getResourceFieldByName("permission"), "perm", permissionResource);
		
		String expectedResult = "select root from User as root "
									+ "left join fetch root.contact as contact "
									+ "left join fetch root.roles as role "
									+ "left join fetch role.permission as perm";
		
		assertTrue(userSearch.generateFullQuery().equals(expectedResult));
	}
	
//	@Test
//	public void savedSearchWrongFiledsNotAllowed()
//	{
//		Resource userResource = new Resource("User");
//		userResource.addField(new ResourceField("userId", false, userResource));
//		userResource.addField(new ResourceField("username", false, userResource));
//		userResource.addField(new ResourceField("enabled", false, userResource));
//		userResource.addField(new ResourceField("contact", true, userResource));
//		userResource.addField(new ResourceField("roles", true, userResource));
//		
//		Resource contactResource = new Resource("Contact");
//		contactResource.addField(new ResourceField("contactId", false, contactResource));
//		contactResource.addField(new ResourceField("firstName", false, contactResource));
//		contactResource.addField(new ResourceField("lastName", false, contactResource));
//		contactResource.addField(new ResourceField("email", false, contactResource));
//		
//		Resource roleResource = new Resource("Role");
//		roleResource.addField(new ResourceField("roleId", false, roleResource));
//		roleResource.addField(new ResourceField("roleName", false, roleResource));
//		roleResource.addField(new ResourceField("permission", true, roleResource));
//		
//		SearchQuery userSearch = new SearchQuery(userResource);
//		Path contactPath = userSearch.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact", contactResource);
//		
//		//This should get an exception as the resource "Contact" does not have a field "roles"
//		contactPath.leftJoinFetch(userResource.getResourceFieldByName("roles"), "role", roleResource);
//
//		assertThrows(expectedType, executable);
//	}
	
	@Test
	public void searchQueryWithConditionsTest() 
			throws NonExistingFieldException, IncorrectFieldFormatException, ExistingConditionGroupException, NoLogicalOperatorException
	{
		Resource userResource = new Resource("User");
		userResource.addField(new ResourceField("userId", false, userResource));
		userResource.addField(new ResourceField("username", false, userResource));
		userResource.addField(new ResourceField("enabled", false, userResource));
		userResource.addField(new ResourceField("contact", true, userResource));
		userResource.addField(new ResourceField("roles", true, userResource));
		
		Resource contactResource = new Resource("Contact");
		contactResource.addField(new ResourceField("contactId", false, contactResource));
		contactResource.addField(new ResourceField("firstName", false, contactResource));
		contactResource.addField(new ResourceField("lastName", false, contactResource));
		contactResource.addField(new ResourceField("email", false, contactResource));
		
		SearchQuery userSearch = new SearchQuery(userResource);
		userSearch.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact", contactResource);
		userSearch.where("root.username", COperator.EQUALS, "administrator");
		
		String expectedResult = "select root from User as root "
				+ "left join fetch root.contact as contact "
				+ "where root.username = administrator";
		String obtainedResult = userSearch.generateFullQuery();
		
		assertEquals(expectedResult, obtainedResult);
		
	}
	
	@Test
	public void searchSingleResultQueryWithConditionsTest() 
			throws NonExistingFieldException, IncorrectFieldFormatException, ExistingConditionGroupException, NoLogicalOperatorException
	{
		Resource userResource = new Resource("User");
		userResource.addField(new ResourceField("userId", false, userResource));
		userResource.addField(new ResourceField("username", false, userResource));
		userResource.addField(new ResourceField("enabled", false, userResource));
		userResource.addField(new ResourceField("contact", true, userResource));
		userResource.addField(new ResourceField("roles", true, userResource));
		
		Resource contactResource = new Resource("Contact");
		contactResource.addField(new ResourceField("contactId", false, contactResource));
		contactResource.addField(new ResourceField("firstName", false, contactResource));
		contactResource.addField(new ResourceField("lastName", false, contactResource));
		contactResource.addField(new ResourceField("email", false, contactResource));
		
		SearchQuery userSearch = new SearchQuery(userResource);
		userSearch.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact", contactResource);
		userSearch.where("root.username", COperator.EQUALS, "administrator");
		userSearch.assignSingleResultField("root", "userId");
		
		String expectedResult = "select root.userId from User as root "
				+ "left join fetch root.contact as contact "
				+ "where root.username = administrator";
		String obtainedResult = userSearch.generateFullQuery();
		
		assertEquals(expectedResult, obtainedResult);
	}
	
	@Test
	public void ConditionStructureTest() throws NonExistingFieldException, IncorrectFieldFormatException, ExistingConditionGroupException, NoLogicalOperatorException
	{
		Resource userResource = new Resource("User");
		userResource.addField(new ResourceField("userId", false, userResource));
		userResource.addField(new ResourceField("username", false, userResource));
		userResource.addField(new ResourceField("enabled", false, userResource));
		userResource.addField(new ResourceField("contact", true, userResource));
		userResource.addField(new ResourceField("roles", true, userResource));
		
		Resource contactResource = new Resource("Contact");
		contactResource.addField(new ResourceField("contactId", false, contactResource));
		contactResource.addField(new ResourceField("firstName", false, contactResource));
		contactResource.addField(new ResourceField("lastName", false, contactResource));
		contactResource.addField(new ResourceField("email", false, contactResource));
		
		SearchQuery userSearch = new SearchQuery(userResource);
		userSearch.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact", contactResource);
		userSearch.where("root.username", COperator.EQUALS, "administrator");
		userSearch.assignSingleResultField("root", "userId");
	}
}