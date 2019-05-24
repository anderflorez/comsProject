package com.unlimitedcompanies.coms.service.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
import com.unlimitedcompanies.coms.domain.abac.LogicOperator;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.ResourceAttribute;
import com.unlimitedcompanies.coms.domain.abac.ResourceReadPolicy;
import com.unlimitedcompanies.coms.domain.abac.UserAttribute;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.abac.SystemAbacService;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.security.ABACService;
import com.unlimitedcompanies.coms.service.security.AuthService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
@Transactional
@ActiveProfiles("integrationTesting")
class ABACAuthenticationIntegrationTests
{	
	@Autowired
	private ABACService abacService;
	
	@Autowired
	private SystemAbacService setupService;
	
	@Autowired
	private AuthService authService;
	
	@Test
	public void numberOfPolicyTests() throws DuplicatedResourcePolicyException
	{
		setupService.initialSetup();
		assertEquals(1, abacService.getNumberOfPolicies(), "Number of policies found in the db failed");
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
		Resource abacResource = abacService.findResourceByNameWithFieldsAndPolicy("ABACPolicy");
		
		ABACPolicy policy = new ABACPolicy("PolicyRead", PolicyType.READ, abacResource);
		abacService.savePolicy(policy, "Administrator");
		
		assertEquals(2, abacService.getNumberOfPolicies(), "Saving resource policy test failed");
	}
	
	@Test
	public void saveCreateAndDeleteResourcePolicyTest() throws DuplicatedResourcePolicyException, NoResourceAccessException
	{		
		setupService.initialSetup();
		Resource abacPolicyResource = abacService.findResourceByName("ABACPolicy");
		Resource contactResource = abacService.findResourceByNameWithFieldsAndPolicy("Contact");
		
		ABACPolicy abacPolicy = new ABACPolicy("PolicyRead", PolicyType.READ, abacPolicyResource);
		abacPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacPolicy, "administrator");
		
		ABACPolicy policy = new ABACPolicy("ContactUpdate", PolicyType.UPDATE, contactResource);
		policy.setCdPolicy(true, true);
		abacService.savePolicy(policy, "Administrator");
		
		assertTrue(abacService.findPolicy(contactResource, PolicyType.UPDATE, "administrator").getCdPolicy().isCreatePolicy(), 
				"Resource create policy test failed");
		assertTrue(abacService.findPolicy(contactResource, PolicyType.UPDATE, "administrator").getCdPolicy().isDeletePolicy(), 
				"Resource delete policy test failed");
	}
	
	@Test
	public void savePolicyWithMultipleSubPoliciesTest() throws DuplicatedResourcePolicyException, NoResourceAccessException
	{		
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByNameWithFieldsAndPolicy("Contact");
		
		ABACPolicy policy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		ABACPolicy subPolicy1 = policy.addSubPolicy(LogicOperator.OR);
		subPolicy1.addSubPolicy(LogicOperator.AND);
		subPolicy1.addSubPolicy(LogicOperator.OR);
		subPolicy1.addSubPolicy(LogicOperator.AND);
		policy.addSubPolicy(LogicOperator.AND);
		policy.addSubPolicy(LogicOperator.OR);
		
		abacService.savePolicy(policy, "administrator");
		
		assertEquals(8, abacService.getNumberOfPolicies(), 
				"Saving policy with multiple condition group integration test failed");
		
		// TODO: Add assertEquals for subpolicies
	}
	
	@Test
	public void savePolicyWithEntityConditionTest() throws DuplicatedResourcePolicyException, NoResourceAccessException
	{
		setupService.initialSetup();
		Resource abacPolicyResource = abacService.findResourceByNameWithFieldsAndPolicy("ABACPolicy");
		
		ABACPolicy policy = new ABACPolicy("AbacPolicyRead", PolicyType.READ, abacPolicyResource);
		policy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		policy.addEntityCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, "Admin");
		
		abacService.savePolicy(policy, "administrator");
		
		assertEquals(3, abacService.getNumberOfEntityConditions(), 
				"Saving policy with multiple entity condition integration test failed"); 
	}
	
	@Test
	public void savePolicyWithAttributeConditionTest() throws DuplicatedResourcePolicyException, NoResourceAccessException
	{
		setupService.initialSetup();
		Resource abacPolicyResource = abacService.findResourceByNameWithFieldsAndPolicy("ABACPolicy");
		
		ABACPolicy policy = new ABACPolicy("UserRead", PolicyType.READ, abacPolicyResource);
		policy.addAttributeCondition(ResourceAttribute.PROJECT_NAME, ComparisonOperator.EQUALS, UserAttribute.PROJECTS);
		policy.addAttributeCondition(ResourceAttribute.P_MANAGERS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		
		abacService.savePolicy(policy, "administrator");
		
		assertEquals(2, abacService.getNumberOfAttributeConditions(), 
				"Saving policy with multiple entity condition integration test failed");
	}
	
	@Test
	public void simplePolicyReadTest() throws Exception
	{
		setupService.initialSetup();
		Resource abacPolicyResource = abacService.findResourceByNameWithFieldsAndPolicy("ABACPolicy");
		
		assertThrows(NoResourceAccessException.class, () -> abacService.findPolicy(abacPolicyResource, PolicyType.UPDATE, "administrator"));
		
		ABACPolicy policy = new ABACPolicy("PolicyRead", PolicyType.READ, abacPolicyResource);
		policy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(policy, "administrator");
		
		assertEquals("PolicyUpdate", abacService.findPolicy(abacPolicyResource, PolicyType.UPDATE, "administrator").getPolicyName(),
				"Simple read permission integration test failed");
	}
	
	@Test
	public void simplePolicyNoReadPermissionTest() throws Exception
	{
		setupService.initialSetup();
		Resource abacPolicyResource = abacService.findResourceByNameWithFieldsAndPolicy("ABACPolicy");
		
		assertThrows(NoResourceAccessException.class, () -> abacService.findPolicy(abacPolicyResource, PolicyType.UPDATE, "administrator"));
	}
	
	@Test
	public void policyEntityConditionTest() throws Exception
	{
		setupService.initialSetup();
		Resource abacPolicyResource = abacService.findResourceByNameWithFieldsAndPolicy("ABACPolicy");
		
		ABACPolicy policy = new ABACPolicy("PolicyRead", PolicyType.READ, abacPolicyResource);
		policy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		policy.addEntityCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, "administrator");
		abacService.savePolicy(policy, "administrator");
		
		ABACPolicy foundPolicy = abacService.findPolicy(abacPolicyResource, PolicyType.READ, "administrator");
		
		assertEquals(2, foundPolicy.getEntityConditions().size(),
				"Simple read permission integration test failed");
		assertEquals("PolicyRead", foundPolicy.getPolicyName(), "Simple read permission integration test failed");
	}
	
	@Test
	public void policyAttributeConditionTest() throws Exception
	{
		setupService.initialSetup();
		Resource abacPolicyResource = abacService.findResourceByNameWithFieldsAndPolicy("ABACPolicy");
		Resource contactResource = abacService.findResourceByNameWithFieldsAndPolicy("Contact");
		
		ABACPolicy abacPolicy = new ABACPolicy("PolicyRead", PolicyType.READ, abacPolicyResource);
		abacPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacPolicy, "administrator");
		
		ABACPolicy policy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		policy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		policy.addAttributeCondition(ResourceAttribute.P_MANAGERS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		policy.addAttributeCondition(ResourceAttribute.P_SUPERINTENDENTS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		abacService.savePolicy(policy, "administrator");		
		
		ABACPolicy foundPolicy = abacService.findPolicy(contactResource, PolicyType.READ, "administrator");
		
		assertEquals(2, foundPolicy.getAttributeConditions().size(), 
				"Policy attribute condition integration test failed");
	}	
	
	@Test
	public void policyFieldConditionTest() throws Exception 
	{
		setupService.initialSetup();
		User administratorUser = authService.searchUserByUsername("administrator");
		Resource abacPolicyResource = abacService.findResourceByNameWithFieldsAndPolicy("ABACPolicy");
		Resource contactResource = abacService.findResourceByNameWithFieldsAndPolicy("Contact");
		
		ABACPolicy abacPolicy = new ABACPolicy("PolicyRead", PolicyType.READ, abacPolicyResource);
		abacPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacPolicy, "administrator");
		
		ABACPolicy policy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		policy.setLogicOperator(LogicOperator.OR);
		policy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		policy.addFieldConditions("lastName", ComparisonOperator.EQUALS, "Doe");
		policy.addFieldConditions("firstName", ComparisonOperator.EQUALS, "Richard");
		abacService.savePolicy(policy, "administrator");
		
		ABACPolicy foundPolicy = abacService.findPolicy(contactResource, PolicyType.READ, "administrator");
		ResourceReadPolicy resourceReadPolicy = foundPolicy.getReadPolicy("contact", "project", administratorUser);
		assertEquals(2, foundPolicy.getFieldConditions().size(), 
				"Policy attribute condition integration test failed");
		assertTrue(resourceReadPolicy.isReadGranted(), "Policy attribute condition integration test failed");
		assertEquals("(contact.lastName = 'Doe' OR contact.firstName = 'Richard')", 
					 resourceReadPolicy.getReadConditions(), 
					 "Policy attribute condition integration test failed");
	}
	
	@Test
	public void numberOfRestrictedFieldsTest()
	{
		assertEquals(0, abacService.getNumberOfRestrictedFields(), "Number of restricted fields found in the db failed");
	}

	@Test
	public void addRestrictedFieldTest() throws Exception
	{
		fail();
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByName("contactResource");
		
		ABACPolicy contactModify = new ABACPolicy("contactUpdate", PolicyType.UPDATE, contactResource);
		contactModify.setCdPolicy(true, false);
		contactModify.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactModify, "administrator");
		
		// TODO: Continue writing this test method when all the services for users, roles and resourceFields are available
		
		assertEquals(1, abacService.getNumberOfRestrictedFields(), "Add restricted fields in the db failed");
	}
}
