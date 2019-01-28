package com.unlimitedcompanies.coms.domain.unitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.unlimitedcompanies.coms.data.query.COperator;
import com.unlimitedcompanies.coms.data.query.LOperator;
import com.unlimitedcompanies.coms.data.query.SearchQuery;
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
	
	@Test
	public void andGroupWithConditionsSetInGroupTest()
	{
		AndGroup andGroup = new AndGroup();
		AndCondition andCondition1 = new AndCondition("firstName", "John", Operator.EQUALS);
		AndCondition andCondition2 = new AndCondition("email", "johnd@example.com", Operator.EQUALS);
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
		AndCondition andCondition1 = new AndCondition("firstName", "John", Operator.EQUALS);
		AndCondition andCondition2 = new AndCondition("email", "johnd@example.com", Operator.EQUALS);
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
		OrCondition orCondition1 = new OrCondition("firstName", "John", Operator.EQUALS);
		OrCondition orCondition2 = new OrCondition("email", "johnd@example.com", Operator.EQUALS);
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
		OrCondition orCondition1 = new OrCondition("firstName", "John", Operator.EQUALS);
		OrCondition orCondition2 = new OrCondition("email", "johnd@example.com", Operator.EQUALS);
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
		AndCondition condition1 = new AndCondition("firstName", "John", Operator.EQUALS);
		AndCondition condition2 = new AndCondition("email", "johnd@example.com", Operator.EQUALS);
		conditionGroup.addAndConditionBidirectional(condition1);
		conditionGroup.addAndConditionBidirectional(condition2);
		ResourcePermissions permission = new ResourcePermissions(role, resource, true, true, true, false);
		permission.setViewCondtitions(conditionGroup);
		
		List<AndCondition> conditions = permission.getViewCondtitions().getConditions();
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
		AndCondition andCondition1 = new AndCondition("firstName", "John", Operator.EQUALS);
		AndCondition andCondition2 = new AndCondition("email", "johnd@example.com", Operator.EQUALS);
		andCondition1.assignToGroupBidirectional(andGroup1);
		andCondition2.assignToGroupBidirectional(andGroup1);
		
		OrGroup orGroup1 = new OrGroup();
		OrCondition orCondition1 = new OrCondition("firstName", "John", Operator.EQUALS);
		OrCondition orCondition2 = new OrCondition("email", "johnd@example.com", Operator.EQUALS);
		orCondition1.assignToGroupBidirectional(orGroup1);
		orCondition2.assignToGroupBidirectional(orGroup1);
		
		andGroup1.setContainerOrGroup(orGroup1);
		
		assertTrue(andGroup1.getContainerOrGroup().getConditions().contains(orCondition2));
	}
	
	@Test
	public void deepPermissionWithConditionsTest()
	{
		Role role = new Role("Administrator");
		Resource resource = new Resource("Contact");
		
		AndGroup andGroup1 = new AndGroup();
		AndCondition andCondition1 = new AndCondition("firstName", "John", Operator.EQUALS);
		AndCondition andCondition2 = new AndCondition("email", "johnd@example.com", Operator.EQUALS);
		andGroup1.addAndConditionBidirectional(andCondition1);
		andGroup1.addAndConditionBidirectional(andCondition2);
		
		OrGroup orGroup2 = new OrGroup();
		OrCondition orCondition1 = new OrCondition("roleName", "Administrator", Operator.NOT_EQUAL);
		orGroup2.addOrConditionBidirectional(orCondition1);
		
		OrGroup orGroup3 = new OrGroup();
		OrCondition orCondition2 = new OrCondition("userId", "5", Operator.LESS_THAN);
		orGroup3.addOrConditionBidirectional(orCondition2);
		
		AndGroup andGroup4 = new AndGroup();
		AndCondition andCondition3 = new AndCondition("contactId", "2", Operator.GRATER_THAN);
		andGroup4.addAndConditionBidirectional(andCondition3);
		
		andGroup1.addOrGroup(orGroup2);
		andGroup1.addOrGroup(orGroup3);
		orGroup2.addAndGroup(andGroup4);
		
		ResourcePermissions permission = new ResourcePermissions(role, resource, true, true, true, false);
		permission.setViewCondtitions(andGroup1);
		
		List<AndGroup> foundAndGroup = null;
		for (OrGroup o : permission.getViewCondtitions().getOrGroups())
		{
			assertTrue(o.equals(orGroup2) || o.equals(orGroup3));
			if (o.getAndGroups().size() > 0)
			{
				foundAndGroup = o.getAndGroups();
			}
		}
		assertTrue(foundAndGroup.contains(andGroup4));
	}
	
	
	// Testing new search query functionality
	
	@Test
	public void searchChaininigTest()
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
		userSearch.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact");
		userSearch.leftJoinFetch(userResource.getResourceFieldByName("roles"), "role")
				  .leftJoinFetch(roleResource.getResourceFieldByName("permission"), "perm");
		
		String expectedResult = "select root from User as root "
									+ "left join fetch root.contact as contact "
									+ "left join fetch root.roles as role "
									+ "left join fetch role.permission as perm";
		
		assertTrue(userSearch.generateFullQuery().equals(expectedResult));
	}
	
	@Test
	public void searchQueryWithConditionsTest()
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
		userSearch.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact");
		userSearch.where("username", COperator.EQUALS, "administrator", 't');
		
		String expectedResult = "select root from User as root left join fetch root.contact as contact where root.username = administrator;";
		String obtainedResult = userSearch.generateFullQuery();
		System.out.println(obtainedResult);
		
		assertEquals(expectedResult, obtainedResult);
		
	}
}