package com.unlimitedcompanies.coms.domain.unitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.unlimitedcompanies.coms.domain.search.Operator;
import com.unlimitedcompanies.coms.domain.security.Address;
import com.unlimitedcompanies.coms.domain.security.AndCondition;
import com.unlimitedcompanies.coms.domain.security.AndGroup;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.OrCondition;
import com.unlimitedcompanies.coms.domain.security.OrGroup;
import com.unlimitedcompanies.coms.domain.security.Phone;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.ResourceField;
import com.unlimitedcompanies.coms.domain.security.ResourcePermissions;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.domain.security.exceptions.InvalidPhoneNumberException;

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
		// TODO: Create some good checking for date and date time
		// TODO: Create some good translating between MySQL dates and times and Java dates and times		
	}
	
	@Test
	public void equalUserTest()
	{
		Contact contact1 = new Contact("John", null, "Doe", "john@example.com");
		User user1 = new User("John", "pass", contact1);
		
		Contact contact2 = new Contact("Jane", null, "Doe", "jane@example.com");
		User user2 = new User("John", "pass", contact2);
		
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
	
	@Test
	public void andGroupWithConditionsSetInGroupTest()
	{
		AndGroup andGroup = new AndGroup();
		AndCondition andCondition1 = new AndCondition("firstName", "John", Operator.EQUAL);
		AndCondition andCondition2 = new AndCondition("email", "johnd@example.com", Operator.EQUAL);
		andGroup.addAndConditionBidirectional(andCondition1);
		andGroup.addAndConditionBidirectional(andCondition2);
		
		List<AndCondition> conditions = andGroup.getConditions();
		for (AndCondition condition : conditions)
		{
			assertTrue(condition.getAndGroup().equals(andGroup));
		}
	}
	
	@Test
	public void andGroupWithConditionsSetInConditionTest()
	{
		AndGroup andGroup = new AndGroup();
		AndCondition andCondition1 = new AndCondition("firstName", "John", Operator.EQUAL);
		AndCondition andCondition2 = new AndCondition("email", "johnd@example.com", Operator.EQUAL);
		andCondition1.assignToGroupBidirectional(andGroup);
		andCondition2.assignToGroupBidirectional(andGroup);
		
		List<AndCondition> conditions = andGroup.getConditions();
		for (AndCondition condition : conditions)
		{
			assertTrue(condition.getAndGroup().equals(andGroup));
		}
	}
	
	@Test
	public void orGroupWithConditionsSetInGroupTest()
	{
		OrGroup orGroup = new OrGroup();
		OrCondition orCondition1 = new OrCondition("firstName", "John", Operator.EQUAL);
		OrCondition orCondition2 = new OrCondition("email", "johnd@example.com", Operator.EQUAL);
		orGroup.addOrConditionBidirectional(orCondition1);
		orGroup.addOrConditionBidirectional(orCondition2);
		
		List<OrCondition> conditions = orGroup.getConditions();
		for (OrCondition condition : conditions)
		{
			assertTrue(condition.getOrGroup().equals(orGroup));
		}
	}
	
	@Test
	public void orGroupWithConditionsSetInConditionTest()
	{
		OrGroup orGroup = new OrGroup();
		OrCondition orCondition1 = new OrCondition("firstName", "John", Operator.EQUAL);
		OrCondition orCondition2 = new OrCondition("email", "johnd@example.com", Operator.EQUAL);
		orCondition1.assignToGroupBidirectional(orGroup);
		orCondition2.assignToGroupBidirectional(orGroup);
		
		List<OrCondition> conditions = orGroup.getConditions();
		for (OrCondition condition : conditions)
		{
			assertTrue(condition.getOrGroup().equals(orGroup));
		}
	}
	
	@Test
	public void resourcePermissionsWithAndConditionsTest()
	{
		Role role = new Role("Administrator");
		Resource resource = new Resource("Contact");
		AndGroup conditionGroup = new AndGroup();
		AndCondition condition1 = new AndCondition("firstName", "John", Operator.EQUAL);
		AndCondition condition2 = new AndCondition("email", "johnd@example.com", Operator.EQUAL);
		conditionGroup.addAndConditionBidirectional(condition1);
		conditionGroup.addAndConditionBidirectional(condition2);
		ResourcePermissions permission = new ResourcePermissions(role, resource, true, true, true, false, conditionGroup);
		
		List<AndCondition> conditions = permission.getAndGroup().getConditions();
		for (AndCondition condition : conditions)
		{
			assertTrue(condition.getAndGroup().equals(conditionGroup), "Resource permissions with conditions unit test failed");
		}
		assertEquals(2, conditions.size(), "Resource permissions with conditions unit test failed");
	}
	
	@Test
	public void chainedConditionGroupsTest()
	{
		AndGroup andGroup1 = new AndGroup();		
		AndCondition andCondition1 = new AndCondition("firstName", "John", Operator.EQUAL);
		AndCondition andCondition2 = new AndCondition("email", "johnd@example.com", Operator.EQUAL);
		andCondition1.assignToGroupBidirectional(andGroup1);
		andCondition2.assignToGroupBidirectional(andGroup1);
		
		OrGroup orGroup1 = new OrGroup();
		OrCondition orCondition1 = new OrCondition("firstName", "John", Operator.EQUAL);
		OrCondition orCondition2 = new OrCondition("email", "johnd@example.com", Operator.EQUAL);
		orCondition1.assignToGroupBidirectional(orGroup1);
		orCondition2.assignToGroupBidirectional(orGroup1);
		
		andGroup1.addOrGroup(orGroup1);
		
		assertTrue(andGroup1.getContainerOrGroup().getConditions().contains(orCondition2));
	}
}
