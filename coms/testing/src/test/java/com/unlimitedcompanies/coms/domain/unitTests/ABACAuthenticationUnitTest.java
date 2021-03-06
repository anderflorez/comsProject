package com.unlimitedcompanies.coms.domain.unitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.unlimitedcompanies.coms.data.exceptions.InvalidPolicyException;
import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.ComparisonOperator;
import com.unlimitedcompanies.coms.domain.abac.LogicOperator;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.domain.abac.ResourceAttribute;
import com.unlimitedcompanies.coms.domain.abac.ResourceField;
import com.unlimitedcompanies.coms.domain.abac.UserAttribute;

public class ABACAuthenticationUnitTest
{
	
	@Test
	public void addPolicyToResourceTest() throws Exception
	{
		Resource testResource = new Resource("TestingResource");
		new AbacPolicy("TestPolicy", PolicyType.READ, testResource);
		assertEquals(1, testResource.getPolicies().size(), "Adding policy to resource test failed");
	}

	@Test
	public void policyLogicOperatorToStringTest() throws Exception
	{
		Resource testResource = new Resource("TestingResource");
		AbacPolicy policy = new AbacPolicy("TestPolicy", PolicyType.READ, testResource);
		assertEquals("AND", policy.getLogicOperator().toString(), "conversion to read logic operator to string failed");
	}
	
	@Test
	public void getPolicyLogicOperatorTest() throws Exception
	{
		Resource testResource = new Resource("TestingResource");
		AbacPolicy policy = new AbacPolicy("TestPolicy", PolicyType.READ, testResource);
		assertEquals(LogicOperator.AND, policy.getLogicOperator(), "conversion to read logic operator to string failed");
	}
	
	@Test
	public void setPolicyLogicOperatorTest() throws Exception
	{
		Resource testResource = new Resource("TestingResource");
		AbacPolicy policy = new AbacPolicy("TestPolicy", PolicyType.READ, testResource);
		policy.setLogicOperator(LogicOperator.OR);
		assertEquals(LogicOperator.OR, policy.getLogicOperator(), "conversion to update logic operator from string failed");
	}

	@Test
	public void policyTypeToStringTest() throws Exception
	{
		Resource testResource = new Resource("TestingResource");
		AbacPolicy policy = new AbacPolicy("TestPolicy", PolicyType.READ, testResource);
		assertEquals("READ", policy.getPolicyType().toString(), "Conversion to read policy type to string failed");
	}
	
	@Test
	public void getPolicyTypeTest() throws Exception
	{
		Resource testResource = new Resource("TestingResource");
		AbacPolicy policy = new AbacPolicy("TestPolicy", PolicyType.READ, testResource);
		assertEquals(PolicyType.READ, policy.getPolicyType(), "Conversion to read policy type to string failed");
	}
	
	@Test
	public void setPolicyTypeTest() throws Exception
	{
		Resource testResource = new Resource("TestingResource");
		AbacPolicy policy = new AbacPolicy("TestPolicy", PolicyType.READ, testResource);
		policy.setPolicyType(PolicyType.UPDATE);
		assertEquals(PolicyType.UPDATE, policy.getPolicyType(), "Conversion to read policy type to string failed");
	}
	
	@Test
	public void multipleResourcePolicyUnitTest() throws Exception
	{
		Resource userResource = new Resource("UserResource");
		
		userResource.addPolicy("UserRead", PolicyType.READ);
		userResource.addPolicy("UserUpdate", PolicyType.UPDATE);
		
		assertEquals(2, userResource.getPolicies().size(), "Creating a multiple resource policy test failed");
	}
	
	@Test
	public void ResourceCreateAndDeletePolicyTest() throws Exception
	{
		Resource userResource = new Resource("UserResource");
		
		AbacPolicy policy = new AbacPolicy("userCreate", PolicyType.UPDATE, userResource);
		policy.getCdPolicy().setCreatePolicy(true);
		policy.getCdPolicy().setDeletePolicy(true);
		
		assertTrue(userResource.getPolicies().get(0).getCdPolicy().isCreatePolicy(), "Resource create policy test failed");
		assertTrue(userResource.getPolicies().get(0).getCdPolicy().isDeletePolicy(), "Resource delete policy test failed");
	}
	
	@Test
	public void addingSubPolicyTest() throws Exception
	{
		Resource testResource = new Resource("TestingResource");
		AbacPolicy policy = new AbacPolicy("TestPolicy", PolicyType.READ, testResource);
		AbacPolicy subPolicy = policy.addSubPolicy(LogicOperator.OR);
		
		assertNull(policy.getSubPolicies().iterator().next().getPolicyName());
		assertEquals(subPolicy.getAbacPolicyId(), policy.getSubPolicies().iterator().next().getAbacPolicyId());
	}
	
	@Test
	public void addMultipleLevelPoliciesTest() throws Exception
	{
		Resource testResource = new Resource("TestingResource");
		AbacPolicy policy = new AbacPolicy("TestPolicy", PolicyType.READ, testResource);
		AbacPolicy subPolicy = policy.addSubPolicy(LogicOperator.OR);
		AbacPolicy thirdPolicy = subPolicy.addSubPolicy(LogicOperator.AND);
		AbacPolicy fourthPolicy = thirdPolicy.addSubPolicy(LogicOperator.OR);
		String id = fourthPolicy.getAbacPolicyId();
				
		AbacPolicy foundPolicy = policy.getSubPolicies().iterator().next().getSubPolicies().iterator().next().getSubPolicies().iterator().next();
		assertEquals(id, foundPolicy.getAbacPolicyId());
	}
	
	@Test
	public void addingEntityConditionTest() throws Exception
	{
		Resource testResource = new Resource("TestingResource");
		AbacPolicy policy = new AbacPolicy("TestPolicy", PolicyType.READ, testResource);
		policy.setLogicOperator(LogicOperator.OR);
		AbacPolicy subPolicy1 = policy.addSubPolicy(LogicOperator.OR);
		AbacPolicy subPolicy2 = policy.addSubPolicy(LogicOperator.AND);
		subPolicy1.addEntityCondition(UserAttribute.ROLE, ComparisonOperator.EQUALS, "Administrator");
		subPolicy1.addEntityCondition(UserAttribute.PROJECT, ComparisonOperator.EQUALS, "Testin Building");
		subPolicy2.addEntityCondition(UserAttribute.ROLE, ComparisonOperator.EQUALS, "Project Manager");
		subPolicy2.addEntityCondition(UserAttribute.PROJECT, ComparisonOperator.NOT_EQUALS, "Sample Project");
		
		Iterator<AbacPolicy> iterator = testResource.getPolicies().get(0).getSubPolicies().iterator();
		assertEquals(2, iterator.next().getEntityConditions().size());
		assertEquals(2, iterator.next().getEntityConditions().size());
	}
	
	@Test
	public void addingAttributeConditionTest() throws Exception
	{
		Resource testResource = new Resource("TestingResource");
		AbacPolicy policy = new AbacPolicy("TestPolicy", PolicyType.READ, testResource);
		policy.setLogicOperator(LogicOperator.OR);
		AbacPolicy groupA = policy.addSubPolicy(LogicOperator.AND);
		AbacPolicy groupB = policy.addSubPolicy(LogicOperator.OR);
		groupA.addAttributeCondition(ResourceAttribute.PROJECT_NAME, ComparisonOperator.EQUALS, UserAttribute.PROJECT);
		groupB.addAttributeCondition(ResourceAttribute.P_MANAGERS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		groupB.addAttributeCondition(ResourceAttribute.P_SUPERINTENDENTS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		groupB.addAttributeCondition(ResourceAttribute.P_FOREMEN, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		
		Iterator<AbacPolicy> iterator = testResource.getPolicies().get(0).getSubPolicies().iterator();
		int a = iterator.next().getAttributeConditions().size();
		int b = iterator.next().getAttributeConditions().size();
		assertTrue((a < b && a == 1) || (a > b && a == 3));
		assertTrue((b < a && b == 1) || (b > a && b == 3));
	}
	
	@Test
	public void addingFieldConditionToConditionGroupTest() throws Exception
	{
		Resource testResource = new Resource("TestingResource");
		new ResourceField("TestField1", false, testResource);
		new ResourceField("TestField2", false, testResource);
		new ResourceField("TestField3", false, testResource);
		new ResourceField("TestField4", false, testResource);
		
		AbacPolicy policy = new AbacPolicy("TestPolicy", PolicyType.READ, testResource);
		policy.setLogicOperator(LogicOperator.OR);
		AbacPolicy groupA = policy.addSubPolicy();
		AbacPolicy groupB = policy.addSubPolicy(LogicOperator.OR);
		groupA.addFieldConditions("TestField1", ComparisonOperator.EQUALS, "Field1Value");
		groupB.addFieldConditions("TestField2", ComparisonOperator.NOT_EQUALS, "Field2Value");
		groupB.addFieldConditions("TestField3", ComparisonOperator.EQUALS, "Field3Value");
		groupB.addFieldConditions("TestField4", ComparisonOperator.EQUALS, "Field4Value");
		
		Iterator<AbacPolicy> iterator = testResource.getPolicies().get(0).getSubPolicies().iterator();
		int a = iterator.next().getFieldConditions().size();
		int b = iterator.next().getFieldConditions().size();
		assertTrue((a < b && a == 1) || (a > b && a == 3));
		assertTrue((b < a && b == 1) || (b > a && b == 3));
	}
	
	@Test
	public void addingFieldConditionToEditingPolicyTest() throws Exception
	{
		Resource testResource = new Resource("TestingResource");
		
		new ResourceField("TestField1", false, testResource);
		new ResourceField("TestField2", false, testResource);
		
		AbacPolicy policy = new AbacPolicy("TestPolicy", PolicyType.UPDATE, testResource);
		
		assertThrows(InvalidPolicyException.class, ()-> policy.addFieldConditions("TestField1", ComparisonOperator.EQUALS, "Field1Value"));
		
	}
	
	@Test
	public void addingFieldConditionToPolicyWithNonExistingFieldTest() throws Exception
	{
		Resource testResource = new Resource("TestingResource");
		Resource anotherResource = new Resource("AnotherResource");
		
		new ResourceField("TestField1", false, testResource);
		new ResourceField("TestField2", false, anotherResource);
		
		AbacPolicy policy = new AbacPolicy("TestPolicy", PolicyType.READ, testResource);
		
		assertThrows(InvalidPolicyException.class, ()-> policy.addFieldConditions("TestField2", ComparisonOperator.EQUALS, "Field1Value"));
		
	}
	
}
