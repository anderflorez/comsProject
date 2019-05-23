package com.unlimitedcompanies.coms.domain.unitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.unlimitedcompanies.coms.data.exceptions.DuplicatedResourcePolicyException;
import com.unlimitedcompanies.coms.domain.abac.ABACPolicy;
import com.unlimitedcompanies.coms.domain.abac.ComparisonOperator;
import com.unlimitedcompanies.coms.domain.abac.LogicOperator;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.ResourceAttribute;
import com.unlimitedcompanies.coms.domain.abac.UserAttribute;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.ResourceField;

public class ABACAuthenticationUnitTest
{
	
	@Test
	public void addPolicyToResourceTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		assertEquals(1, testResource.getPolicies().size(), "Adding policy to resource test failed");
	}

	@Test
	public void policyLogicOperatorToStringTest() throws DuplicatedResourcePolicyException 
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		assertEquals("AND", policy.getLogicOperator().toString(), "conversion to read logic operator to string failed");
	}
	
	@Test
	public void getPolicyLogicOperatorTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		assertEquals(LogicOperator.AND, policy.getLogicOperator(), "conversion to read logic operator to string failed");
	}
	
	@Test
	public void setPolicyLogicOperatorTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		policy.setLogicOperator(LogicOperator.OR);
		assertEquals(LogicOperator.OR, policy.getLogicOperator(), "conversion to update logic operator from string failed");
	}

	@Test
	public void policyTypeToStringTest() throws DuplicatedResourcePolicyException 
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		assertEquals("READ", policy.getPolicyType().toString(), "Conversion to read policy type to string failed");
	}
	
	@Test
	public void getPolicyTypeTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		assertEquals(PolicyType.READ, policy.getPolicyType(), "Conversion to read policy type to string failed");
	}
	
	@Test
	public void setPolicyTypeTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		policy.setPolicyType(PolicyType.UPDATE);
		assertEquals(PolicyType.UPDATE, policy.getPolicyType(), "Conversion to read policy type to string failed");
	}
	
	@Test
	public void multipleResourcePolicyUnitTest() throws DuplicatedResourcePolicyException
	{
		Resource userResource = new Resource("UserResource");
		
		userResource.addPolicy("UserRead", PolicyType.READ);
		userResource.addPolicy("UserUpdate", PolicyType.UPDATE);
		
		assertEquals(2, userResource.getPolicies().size(), "Creating a multiple resource policy test failed");
	}
	
	@Test
	public void duplicateResourcePolicyNotAllowedUnitTest() throws DuplicatedResourcePolicyException
	{
		Resource userResource = new Resource("UserResource");
		
		userResource.addPolicy("UserRead", PolicyType.READ);
		userResource.addPolicy("UserUpdate", PolicyType.UPDATE);
		
		assertThrows(DuplicatedResourcePolicyException.class, () -> userResource.addPolicy("TestCreate", PolicyType.UPDATE));
	}
	
	@Test
	public void ResourceCreateAndDeletePolicyTest() throws Exception
	{
		Resource userResource = new Resource("UserResource");
		
		ABACPolicy policy = new ABACPolicy("userCreate", PolicyType.UPDATE, userResource);
		policy.getCdPolicy().setCreatePolicy(true);
		policy.getCdPolicy().setDeletePolicy(true);
		
		assertTrue(userResource.getPolicies().get(0).getCdPolicy().isCreatePolicy(), "Resource create policy test failed");
		assertTrue(userResource.getPolicies().get(0).getCdPolicy().isDeletePolicy(), "Resource delete policy test failed");
	}
	
	@Test
	public void addingSubPolicyTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		ABACPolicy subPolicy = policy.addSubPolicy(LogicOperator.OR);
		
		assertNull(policy.getSubPolicies().get(0).getPolicyName());
		assertEquals(subPolicy.getAbacPolicyId(), policy.getSubPolicies().get(0).getAbacPolicyId(), "Adding policy condition group failed");
	}
	
	@Test
	public void addMultipleLevelPoliciesTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		ABACPolicy subPolicy = policy.addSubPolicy(LogicOperator.OR);
		ABACPolicy thirdPolicy = subPolicy.addSubPolicy(LogicOperator.AND);
		ABACPolicy fourthPolicy = thirdPolicy.addSubPolicy(LogicOperator.OR);
		String id = fourthPolicy.getAbacPolicyId();
				
		ABACPolicy foundPolicy = policy.getSubPolicies().get(0).getSubPolicies().get(0).getSubPolicies().get(0);
		assertEquals(id, foundPolicy.getAbacPolicyId(), "Adding multiple level condition groups unit test");
	}
	
	@Test
	public void addingEntityConditionTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		policy.setLogicOperator(LogicOperator.OR);
		ABACPolicy subPolicy1 = policy.addSubPolicy(LogicOperator.OR);
		ABACPolicy subPolicy2 = policy.addSubPolicy(LogicOperator.AND);
		subPolicy1.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrator");
		subPolicy1.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Management");
		subPolicy2.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Project Manager");
		subPolicy2.addEntityCondition(UserAttribute.PROJECTS, ComparisonOperator.EQUALS, "Sample Project");
		
		assertEquals(2, testResource.getPolicies().get(0).getSubPolicies().get(0).getEntityConditions().size(),
					 "Adding entity condition to condition group test failed");
		assertEquals(2, testResource.getPolicies().get(0).getSubPolicies().get(1).getEntityConditions().size(),
				 "Adding entity condition to condition group test failed");
	}

	@Test
	public void addingAttributeConditionTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		policy.setLogicOperator(LogicOperator.OR);
		ABACPolicy groupA = policy.addSubPolicy(LogicOperator.AND);
		ABACPolicy groupB = policy.addSubPolicy(LogicOperator.OR);
		groupA.addAttributeCondition(ResourceAttribute.PROJECT_NAME, ComparisonOperator.EQUALS, UserAttribute.PROJECTS);
		groupB.addAttributeCondition(ResourceAttribute.P_MANAGERS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		groupB.addAttributeCondition(ResourceAttribute.P_SUPERINTENDENTS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		groupB.addAttributeCondition(ResourceAttribute.P_FOREMEN, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		
		assertEquals(1, testResource.getPolicies().get(0).getSubPolicies().get(0).getAttributeConditions().size(), 
				"Adding attribute condition to condition group test failed");
		assertEquals(3, testResource.getPolicies().get(0).getSubPolicies().get(1).getAttributeConditions().size(), 
				"Adding attribute condition to condition group test failed");
	}
	
	@Test
	public void addingFieldConditionToConditionGroupTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		new ResourceField("TestField1", false, testResource);
		new ResourceField("TestField2", false, testResource);
		new ResourceField("TestField3", false, testResource);
		new ResourceField("TestField4", false, testResource);
		
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		policy.setLogicOperator(LogicOperator.OR);
		ABACPolicy groupA = policy.addSubPolicy();
		ABACPolicy groupB = policy.addSubPolicy(LogicOperator.OR);
		groupA.addFieldConditions("TestField1", ComparisonOperator.EQUALS, "Field1Value");
		groupB.addFieldConditions("TestField2", ComparisonOperator.NOT_EQUALS, "Field2Value");
		groupB.addFieldConditions("TestField3", ComparisonOperator.EQUALS, "Field3Value");
		groupB.addFieldConditions("TestField4", ComparisonOperator.EQUALS, "Field4Value");
		
		assertEquals(1, testResource.getPolicies().get(0).getSubPolicies().get(0).getFieldConditions().size(), 
				"Adding field condition to condition group test failed");
		assertEquals(3, testResource.getPolicies().get(0).getSubPolicies().get(1).getFieldConditions().size(), 
				"Adding field condition to condition group test failed");
	}
	
}
