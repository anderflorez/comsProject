package com.unlimitedcompanies.coms.service.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.data.exceptions.DuplicatedResourcePolicyException;
import com.unlimitedcompanies.coms.domain.abac.ABACPolicy;
import com.unlimitedcompanies.coms.domain.abac.ComparisonOperator;
import com.unlimitedcompanies.coms.domain.abac.ConditionGroup;
import com.unlimitedcompanies.coms.domain.abac.LogicOperator;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.ResourceAttribute;
import com.unlimitedcompanies.coms.domain.abac.UserAttribute;
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
	public void numberOfPolicyTests() throws DuplicatedResourcePolicyException
	{
		setupService.initialSetup();
		assertEquals(1, abacService.getNumberOfPolicies(), "Number of policies found in the db failed");
	}
	
	@Test
	public void numberOfConditionGroupTest() throws DuplicatedResourcePolicyException
	{
		setupService.initialSetup();
		assertEquals(1, abacService.getNumberOfConditionGroups(), "Number of condition groups found in the db failed");
	}
	
	@Test
	public void numberOfEntityConditionTest() throws DuplicatedResourcePolicyException
	{
		setupService.initialSetup();
		assertEquals(1, abacService.getNumberOfEntityConditions(), "Number of entity conditions found in the db failed");
	}
	
	@Test
	public void numberOfAttributeConditionTest() throws DuplicatedResourcePolicyException
	{
		setupService.initialSetup();
		assertEquals(0, abacService.getNumberOfAttributeConditions(), "Number of record conditions found in the db failed");
	}
	
	@Test
	public void saveSingleResourcePolicyTest() throws DuplicatedResourcePolicyException, NoResourceAccessException
	{
		setupService.initialSetup();
		Resource abacPolicy = setupService.findResourceByNameWithFieldsAndPolicy("ABACPolicy");
		
		ABACPolicy policy = new ABACPolicy("PolicyRead", PolicyType.READ, abacPolicy);
		abacService.savePolicy(policy, "Administrator");
		
		assertEquals(2, abacService.getNumberOfPolicies(), "Saving resource policy test failed");
	}
	
	@Test
	public void saveCreateAndDeleteResourcePolicyTest() throws DuplicatedResourcePolicyException, NoResourceAccessException
	{
		setupService.initialSetup();
		Resource userResource = setupService.findResourceByNameWithFieldsAndPolicy("User");
		ABACPolicy policy = new ABACPolicy("UserUpdate", PolicyType.UPDATE, userResource);
		policy.setCdPolicy(true, true);
		
		abacService.savePolicy(policy, "Administrator");
		
		// TODO: Change the user "system" to use the actual username for the next two lines
		assertTrue(abacService.findPolicy(userResource, PolicyType.UPDATE, "Administrator").getCdPolicy().isCreatePolicy(), 
				"Resource create policy test failed");
		assertTrue(abacService.findPolicy(userResource, PolicyType.UPDATE, "Administrator").getCdPolicy().isDeletePolicy(), 
				"Resource delete policy test failed");
	}
	
	@Test
	public void savePolicyWithMultipleConditionGroupTest() throws DuplicatedResourcePolicyException, NoResourceAccessException
	{
		setupService.initialSetup();
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
		
		abacService.savePolicy(policy, "Administrator");
		
		assertEquals(8, abacService.getNumberOfConditionGroups(), 
				"Saving policy with multiple condition group integration test failed");
	}
	
	@Test
	public void savePolicyWithEntityConditionTest() throws DuplicatedResourcePolicyException, NoResourceAccessException
	{
		setupService.initialSetup();
		Resource userResource = setupService.findResourceByNameWithFieldsAndPolicy("User");
		
		ABACPolicy policy = new ABACPolicy("UserRead", PolicyType.READ, userResource);
		ConditionGroup group = policy.addConditionGroup();
		group.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrator");
		group.addEntityCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, "Admin");
		
		abacService.savePolicy(policy, "Administrator");
		
		assertEquals(3, abacService.getNumberOfEntityConditions(), 
				"Saving policy with multiple entity condition integration test failed"); 
	}
	
	@Test
	public void savePolicyWithRecordConditionTest() throws DuplicatedResourcePolicyException, NoResourceAccessException
	{
		setupService.initialSetup();
		Resource userResource = setupService.findResourceByNameWithFieldsAndPolicy("User");
		
		ABACPolicy policy = new ABACPolicy("UserRead", PolicyType.READ, userResource);
		ConditionGroup group = policy.addConditionGroup();
		group.addAttributeCondition(UserAttribute.PROJECTS, ComparisonOperator.EQUALS, ResourceAttribute.PROJECT_NAME);
		group.addAttributeCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, ResourceAttribute.P_MANAGERS);
		
		abacService.savePolicy(policy, "Administrator");
		
		assertEquals(2, abacService.getNumberOfAttributeConditions(), 
				"Saving policy with multiple entity condition integration test failed");
	}
	
	@Test
	public void findABACPolicyTest() throws DuplicatedResourcePolicyException, NoResourceAccessException
	{
		setupService.initialSetup();
		Resource abacPolicyResource = setupService.findResourceByNameWithFieldsAndPolicy("ABACPolicy");
		
		ABACPolicy policy = new ABACPolicy("PolicyRead", PolicyType.READ, abacPolicyResource);
		ConditionGroup cg = policy.addConditionGroup();
		cg.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");		
		abacService.savePolicy(policy, "Administrator");
		
		ABACPolicy foundPolicy = abacService.findPolicy(abacPolicyResource, PolicyType.UPDATE, "Administrator");
		
		assertTrue(foundPolicy.getCdPolicy().isCreatePolicy(), "Finding policy by name test failed");
		assertFalse(foundPolicy.getCdPolicy().isDeletePolicy(), "Finding policy by name test failed");
	}
	
	@Test
	public void findABACPolicyByNameTest() throws DuplicatedResourcePolicyException
	{
		setupService.initialSetup();
		
		assertEquals("ABACPolicy", abacService.findPolicyByName("PolicyCreate").getResource().getResourceName(), "Finding policy by name test failed");
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
	public void simpleCreateRecordPolicyIntegrationTest() throws Exception
	{
		// TODO: Need to have an initial employees and projects tables in db to test
		
		setupService.initialSetup();
		Resource contactResource = setupService.findResourceByNameWithFieldsAndPolicy("Contact");
		

//		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"));
//		User user = authService.saveUser(new User("usertest", "mypass".toCharArray(), contact));
//		Role role = authService.saveRole(new Role("Manager"));
//		authService.assignUserToRole(user.getUserId(), role.getRoleId());		
//		
//		ABACPolicy policy = new ABACPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
//		ConditionGroup group = policy.addConditionGroup();
//		group.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Manager");
//		abacService.savePolicy(policy);
//
//		fail("Simple create record policy integration test failed");
//		
//		contactService.saveContact(new Contact("Jane", null, "Doe", "jane@example.com"), user.getUsername());
//		contactService.saveContact(new Contact("Richard", null, "Roe", "rich@example.com"), user.getUsername());
//		
//		assertEquals(3, contactService.findNumberOfContacts(), "Simple create record policy integration test failed");
		fail();
	}
	
	@Test
	public void multipleRecordReadForEntityPermissionTest() throws Exception
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
//		group.addAttributeCondition(UserAttribute.PROJECTS, ComparisonOperator.EQUALS, ResourceAttribute.PROJECT_NAME);
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
