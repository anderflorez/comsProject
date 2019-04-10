package com.unlimitedcompanies.coms.service.integrationTests;

import static org.junit.jupiter.api.Assertions.*;

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
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
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
	SecuritySetupService setupService;
	
	@Autowired
	ContactService contactService;
	
	@Autowired
	AuthService authService;

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
		assertEquals(0, abacService.getNumberOfRecordConditions(), "Number of record conditions found in the db failed");
	}
	
	@Test
	public void saveResourcePolicyIntegrationTest() throws DuplicatedResourcePolicyException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFields("User");
		ABACPolicy policy = new ABACPolicy("UserRead", PolicyType.READ, userResource);
		
		System.out.println(policy.getAbacPolicyId() + " policy id length is " + policy.getAbacPolicyId().length());
		
		abacService.savePolicy(policy);
		
		assertEquals(1, abacService.getNumberOfPolicies(), "Saving resource policy test failed");
	}
	
	@Test
	public void saveMultipleResourcePolicyIntegrationTest() throws DuplicatedResourcePolicyException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFields("User");
		Resource contactResource = setupService.findResourceByName("Contact");
		
		ABACPolicy policyA1 = new ABACPolicy("UserRead", PolicyType.READ, userResource);
		ABACPolicy policyA2 = new ABACPolicy("UserCreate", PolicyType.CREATE, userResource);
		ABACPolicy policyB1 = new ABACPolicy("ContactUpdate", PolicyType.UPDATE, contactResource);
		ABACPolicy policyB2 = new ABACPolicy("ContactDelete", PolicyType.DELETE, contactResource);
		
		abacService.savePolicy(policyA1);
		abacService.savePolicy(policyA2);
		abacService.savePolicy(policyB1);
		abacService.savePolicy(policyB2);
		
		assertEquals(4, abacService.getNumberOfPolicies(), "Saving multiple resource policy test failed");
	}
	
	@Test
	public void findABACPolicyByNameTest() throws DuplicatedResourcePolicyException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFields("User");
		ABACPolicy policy = new ABACPolicy("UserRead", PolicyType.READ, userResource);
		abacService.savePolicy(policy);
		
		assertEquals(userResource, abacService.findPolicyByName("UserRead").getResource(), "Finding policy by name test failed");
	}
	
	@Test
	public void savePolicyWithMultipleConditionGroupTest() throws DuplicatedResourcePolicyException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFields("User");
		
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
		Resource userResource = setupService.findResourceByNameWithFields("User");
		
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
		Resource userResource = setupService.findResourceByNameWithFields("User");
		
		ABACPolicy policy = new ABACPolicy("UserRead", PolicyType.READ, userResource);
		ConditionGroup group = policy.addConditionGroup();
		group.addRecordCondition(UserAttribute.PROJECTS, ComparisonOperator.EQUALS, ResourceAttribute.PROJECTS);
		group.addRecordCondition(UserAttribute.FULL_NAME, ComparisonOperator.EQUALS, ResourceAttribute.P_MANAGERS);
		
		abacService.savePolicy(policy);
		
		assertTrue(abacService.getNumberOfRecordConditions() == 2, 
				"Saving policy with multiple entity condition integration test failed");
	}
	
	@Test
	public void initialPermissionTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException, DuplicatedResourcePolicyException
	{
		fail();
	}
}
