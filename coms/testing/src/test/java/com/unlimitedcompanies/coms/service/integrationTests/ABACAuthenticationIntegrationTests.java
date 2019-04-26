package com.unlimitedcompanies.coms.service.integrationTests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.data.abac.ABACPolicy;
import com.unlimitedcompanies.coms.data.abac.ComparisonOperator;
import com.unlimitedcompanies.coms.data.abac.ConditionGroup;
import com.unlimitedcompanies.coms.data.abac.LogicOperator;
import com.unlimitedcompanies.coms.data.abac.PolicyType;
import com.unlimitedcompanies.coms.data.abac.ResourceAttribute;
import com.unlimitedcompanies.coms.data.abac.UserAttribute;
import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.data.exceptions.DuplicatedResourcePolicyException;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotCreatedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.ABACService;
import com.unlimitedcompanies.coms.service.security.AuthService;
import com.unlimitedcompanies.coms.service.security.ContactService;
import com.unlimitedcompanies.coms.service.security.SecuritySetupService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
@Transactional
@ActiveProfiles("integrationTesting")
class ABACAuthenticationIntegrationTests
{
	@Autowired
	private ABACService abacService;
	
	@Autowired
	private SecuritySetupService setupService;
	
	@Autowired
	private ContactService contactService;
	
	@Autowired
	private AuthService authService;

	@Test
	public void numberOfPolicieTest()
	{
		assertEquals(0, abacService.getNumberOfPolicies(), "Number of policies found in the db failed");
	}
	
	@Test
	public void numberOfConditionGroupTest()
	{
		assertEquals(0, abacService.getNumberOfConditionGroups(), "Number of condition groups found in the db failed");
	}
	
	@Test
	public void numberOfEntityConditionTest()
	{
		assertEquals(0, abacService.getNumberOfEntityConditions(), "Number of entity conditions found in the db failed");
	}
	
	@Test
	public void numberOfRecordConditionTest()
	{
		assertEquals(0, abacService.getNumberOfAttributeConditions(), "Number of record conditions found in the db failed");
	}
	
	@Test
	public void saveResourcePolicyIntegrationTest() throws DuplicatedResourcePolicyException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFieldsAndPolicy("User");
		ABACPolicy policy = new ABACPolicy("UserRead", PolicyType.READ, userResource);
		
		System.out.println(policy.getAbacPolicyId() + " policy id length is " + policy.getAbacPolicyId().length());
		
		abacService.savePolicy(policy);
		
		assertEquals(1, abacService.getNumberOfPolicies(), "Saving resource policy test failed");
	}
	
	@Test
	public void saveMultipleResourcePolicyIntegrationTest() throws DuplicatedResourcePolicyException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFieldsAndPolicy("User");
		Resource contactResource = setupService.findResourceByNameWithFieldsAndPolicy("Contact");
		
		ABACPolicy policyA = new ABACPolicy("UserRead", PolicyType.READ, userResource);
		ABACPolicy policyB = new ABACPolicy("ContactUpdate", PolicyType.UPDATE, contactResource);
		
		abacService.savePolicy(policyA);
		abacService.savePolicy(policyB);
		
		assertEquals(2, abacService.getNumberOfPolicies(), "Saving multiple resource policy test failed");
	}
	
	@Test
	public void findABACPolicyByNameTest() throws DuplicatedResourcePolicyException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFieldsAndPolicy("User");
		ABACPolicy policy = new ABACPolicy("UserRead", PolicyType.READ, userResource);
		abacService.savePolicy(policy);
		
		assertEquals(userResource, abacService.findPolicyByName("UserRead").getResource(), "Finding policy by name test failed");
	}
	
	@Test
	public void savePolicyWithMultipleConditionGroupTest() throws DuplicatedResourcePolicyException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFieldsAndPolicy("User");
		
		ABACPolicy policy = new ABACPolicy("UserRead", PolicyType.READ, userResource);
		policy.setLogicOperator(LogicOperator.OR);
		
		policy.addConditionGroup();
		policy.addConditionGroup();
		ConditionGroup groupA = policy.addConditionGroup();
		ConditionGroup groupB = groupA.addConditionGroup(LogicOperator.OR);
		groupA.addConditionGroup();
		groupB.addConditionGroup();
		groupB.addConditionGroup();
		
		abacService.savePolicy(policy);
		
		assertTrue(abacService.getNumberOfConditionGroups() == 7, 
				"Saving policy with multiple condition group integration test failed");
	}
	
	@Test
	public void savePolicyWithEntityConditionTest() throws DuplicatedResourcePolicyException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFieldsAndPolicy("User");
		
		ABACPolicy policy = new ABACPolicy("UserRead", PolicyType.READ, userResource);
		ConditionGroup group = policy.addConditionGroup();
		group.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrator");
		group.addEntityCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, "Admin");
		
		abacService.savePolicy(policy);
		
		assertTrue(abacService.getNumberOfEntityConditions() == 2, 
				"Saving policy with multiple entity condition integration test failed");
	}
	
	@Test
	public void savePolicyWithRecordConditionTest() throws DuplicatedResourcePolicyException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFieldsAndPolicy("User");
		
		ABACPolicy policy = new ABACPolicy("UserRead", PolicyType.READ, userResource);
		ConditionGroup group = policy.addConditionGroup();
		group.addAttributeCondition(UserAttribute.PROJECTS, ComparisonOperator.EQUALS, ResourceAttribute.PROJECTS);
		group.addAttributeCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, ResourceAttribute.P_MANAGERS);
		
		abacService.savePolicy(policy);
		
		assertTrue(abacService.getNumberOfAttributeConditions() == 2, 
				"Saving policy with multiple entity condition integration test failed");
	}
	
	@Test
	public void simpleReadForEntityPermissionTest() throws Exception
	{
		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"));
		contactService.saveContact(new Contact("Jane", null, "Doe", "jane@example.com"));
		contactService.saveContact(new Contact("Richard", null, "Roe", "rich@example.com"));
		User user = authService.saveUser(new User("usertest", "mypass".toCharArray(), contact));
		Role role = authService.saveRole(new Role("Administrator"));
		authService.assignUserToRole(user.getUserId(), role.getRoleId());
		
		setupService.checkAllResources();
		Resource contactResource = setupService.findResourceByNameWithFieldsAndPolicy("Contact");
		
		ABACPolicy policy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		ConditionGroup group = policy.addConditionGroup();
		group.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrator");
		abacService.savePolicy(policy);
		
		Contact foundContact = contactService.searchContactByEmail("jane@example.com", user.getUsername());
		assertTrue(foundContact.getFirstName().equals("Jane"), "Simple read permission integration test failed");
	}
	
	@Test
	public void simpleReadForEntityNoPermissionTest() throws Exception
	{
		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"));
		contactService.saveContact(new Contact("Jane", null, "Doe", "jane@example.com"));
		contactService.saveContact(new Contact("Richard", null, "Roe", "rich@example.com"));
		User user = authService.saveUser(new User("usertest", "mypass".toCharArray(), contact));
		Role role = authService.saveRole(new Role("Manager"));
		authService.assignUserToRole(user.getUserId(), role.getRoleId());
		
		setupService.checkAllResources();
		Resource contactResource = setupService.findResourceByNameWithFieldsAndPolicy("Contact");
		
		ABACPolicy policy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		ConditionGroup group = policy.addConditionGroup();
		group.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrator");
		abacService.savePolicy(policy);
		
		assertThrows(NoResourceAccessException.class, () -> contactService.searchContactByEmail("jane@example.com", user.getUsername()));
	}
	
	@Test
	public void multipleRecordReadForEntityPermissionTest() 
			throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException, 
				   DuplicatedResourcePolicyException, NoResourceAccessException
	{
		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"));
		contactService.saveContact(new Contact("Jane", null, "Doe", "jane@example.com"));
		contactService.saveContact(new Contact("Richard", null, "Roe", "rich@example.com"));
		User user = authService.saveUser(new User("usertest", "mypass".toCharArray(), contact));
		Role role = authService.saveRole(new Role("Administrator"));		
		authService.assignUserToRole(user.getUserId(), role.getRoleId());
		
		setupService.checkAllResources();
		Resource contactResource = setupService.findResourceByNameWithFieldsAndPolicy("Contact");
		
		ABACPolicy policy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		ConditionGroup group = policy.addConditionGroup();
		group.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrator");
		abacService.savePolicy(policy);
		
		List<Contact> foundContacts = contactService.searchAllContacts(user.getUsername());
		assertEquals(3, foundContacts.size(), "Multiple record read permission integration test failed");
	}
	
//	@Test
//	public void multipleRecordReadForAttributeConditionPermissionTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException, DuplicatedResourcePolicyException
//	{
//		// TODO: Activate this test when projects is available
//		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"));
//		contactService.saveContact(new Contact("Jane", null, "Doe", "jane@example.com"));
//		contactService.saveContact(new Contact("Richard", null, "Roe", "rich@example.com"));
//		User user = authService.saveUser(new User("usertest", "mypass".toCharArray(), contact));
//		Role role = authService.saveRole(new Role("Project Manager"));		
//		authService.assignUserToRole(user.getUserId(), role.getRoleId());
//		
//		setupService.checkAllResources();
//		Resource contactResource = setupService.findResourceByNameWithFieldsAndPolicy("Contact");
//		
//		ABACPolicy policy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
//		ConditionGroup group = policy.addConditionGroup();
//		group.addAttributeCondition(UserAttribute.PROJECTS, ComparisonOperator.EQUALS, ResourceAttribute.PROJECTS);
//
//		abacService.savePolicy(policy);
//		
//		// TODO: Activate this test when projects is available 
//		fail();
//		List<Contact> foundContacts = contactService.searchAllContacts(user.getUsername());
//		assertEquals(3, foundContacts.size(), "Multiple record read permission integration test failed");
//		
//	}
	
	@Test
	public void multipleRecordReadForFieldConditionPermissionTest() 
			throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException, 
				   DuplicatedResourcePolicyException, NoResourceAccessException
	{
		Contact contact1 = contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"));
		contactService.saveContact(new Contact("Jane", null, "Doe", "jane@example.com"));
		contactService.saveContact(new Contact("Richard", null, "Roe", "rich@example.com"));
		User user = authService.saveUser(new User("usertest", "mypass".toCharArray(), contact1));
		Role role = authService.saveRole(new Role("Project Manager"));		
		authService.assignUserToRole(user.getUserId(), role.getRoleId());
		
		setupService.checkAllResources();
		Resource contactResource = setupService.findResourceByNameWithFieldsAndPolicy("Contact");
		
		ABACPolicy policy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		ConditionGroup group = policy.addConditionGroup();
		group.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Project Manager");
		group.addFieldConditions("lastName", ComparisonOperator.EQUALS, "Doe");
		abacService.savePolicy(policy);
		
		List<Contact> foundContacts = contactService.searchAllContacts(user.getUsername());
		foundContacts.forEach(n -> System.out.println(n.getFirstName() + " " + n.getLastName()));
		assertEquals(2, foundContacts.size(), "Multiple record read permission integration test failed");
	}
	
	@Test
	public void multipleRecordReadForFieldConditionNotEnoughPermissionTest() 
			throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException, 
				   DuplicatedResourcePolicyException, NoResourceAccessException
	{
		Contact contact1 = contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"));
		contactService.saveContact(new Contact("Jane", null, "Doe", "jane@example.com"));
		contactService.saveContact(new Contact("Richard", null, "Roe", "rich@example.com"));
		User user = authService.saveUser(new User("usertest", "mypass".toCharArray(), contact1));
		Role role = authService.saveRole(new Role("Project Manager"));		
		authService.assignUserToRole(user.getUserId(), role.getRoleId());
		
		setupService.checkAllResources();
		Resource contactResource = setupService.findResourceByNameWithFieldsAndPolicy("Contact");
		
		ABACPolicy policy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		ConditionGroup group = policy.addConditionGroup();
		group.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Project Manager");
		group.addFieldConditions("lastName", ComparisonOperator.EQUALS, "Tales");
		abacService.savePolicy(policy);

		List<Contact> foundContacts = contactService.searchAllContacts(user.getUsername());
		foundContacts.forEach(n -> System.out.println(n.getFirstName() + " " + n.getLastName()));
		assertFalse(foundContacts.size() > 0, "Multiple record read permission integration test failed");
	}
	
	@Test
	public void multipleRecordReadForFieldConditionNoPermissionTest() 
			throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException, 
				   DuplicatedResourcePolicyException, NoResourceAccessException
	{
		Contact contact1 = contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"));
		contactService.saveContact(new Contact("Jane", null, "Doe", "jane@example.com"));
		contactService.saveContact(new Contact("Richard", null, "Roe", "rich@example.com"));
		User user = authService.saveUser(new User("usertest", "mypass".toCharArray(), contact1));
		Role role = authService.saveRole(new Role("Project Manager"));		
		authService.assignUserToRole(user.getUserId(), role.getRoleId());
		
		setupService.checkAllResources();
		Resource contactResource = setupService.findResourceByNameWithFieldsAndPolicy("Contact");
		
		ABACPolicy policy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		ConditionGroup group = policy.addConditionGroup();
		group.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrator");
		group.addFieldConditions("lastName", ComparisonOperator.EQUALS, "Doe");
		abacService.savePolicy(policy);
		
		assertThrows(NoResourceAccessException.class, ()-> contactService.searchAllContacts(user.getUsername()), 
				"Multiple record read permission integration test failed");
	}
}
