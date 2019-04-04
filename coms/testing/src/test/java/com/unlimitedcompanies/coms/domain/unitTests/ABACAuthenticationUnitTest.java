package com.unlimitedcompanies.coms.domain.unitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.unlimitedcompanies.coms.data.abac.ABACPolicy;
import com.unlimitedcompanies.coms.data.abac.ComparisonOperator;
import com.unlimitedcompanies.coms.data.abac.ConditionGroup;
import com.unlimitedcompanies.coms.data.abac.LogicOperator;
import com.unlimitedcompanies.coms.data.abac.PolicyType;
import com.unlimitedcompanies.coms.data.abac.ResourceAttribute;
import com.unlimitedcompanies.coms.data.abac.UserAttribute;
import com.unlimitedcompanies.coms.data.exceptions.DuplicatedResourcePolicyException;
import com.unlimitedcompanies.coms.domain.security.Resource;

public class ABACAuthenticationUnitTest
{
	
	@Test
	public void addPolicyToResourceTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		assertEquals(1, testResource.getPolicies().size(), "Adding policy to resource test failed");
	}

	// This test will work only if the method getLogicOperator() from ABACPolicy.java is set to public
//	@Test
//	public void policyLogicOperatorToStringTest()
//	{
//		Resource testResource = new Resource("TestingResource");
//		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
//		assertEquals("AND", policy.getLogicOperator(), "conversion to read logic operator to string failed");
//	}
	
	// This test will work only if the method setLogicOperator(String logicOperator) from ABACPolicy.java is set to public
//	@Test
//	public void stringToPolicyLogicOperatorTest()
//	{
//		Resource testResource = new Resource("TestingResource");
//		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
//		policy.setLogicOperator("or");
//		assertEquals(LogicOperator.OR, policy.getOperator(), "conversion to update logic operator from string failed");
//	}
	
	@Test
	public void getPolicyLogicOperatorTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		assertEquals(LogicOperator.AND, policy.getOperator(), "conversion to read logic operator to string failed");
	}
	
	@Test
	public void setPolicyLogicOperatorTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		policy.setLogicOperator(LogicOperator.OR);
		assertEquals(LogicOperator.OR, policy.getOperator(), "conversion to update logic operator from string failed");
	}

	// This test will work only if the method getPolicyType() from ABACPolicy.java is set to public
//	@Test
//	public void policyTypeToStringTest()
//	{
//		Resource testResource = new Resource("TestingResource");
//		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
//		assertEquals("READ", policy.getPolicyType(), "Conversion to read policy type to string failed");
//	}
	
	// This test will work only if the method setPolicyType(String policyType) from ABACPolicy.java is set to public
//	@Test
//	public void stringToPolicyTypeTest()
//	{
//		Resource testResource = new Resource("TestingResource");
//		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
//		policy.setPolicyType("Create");
//		assertEquals(PolicyType.CREATE, policy.getType(), "Conversion to read policy type to string failed");
//	}
	
	@Test
	public void getPolicyTypeTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		assertEquals(PolicyType.READ, policy.getType(), "Conversion to read policy type to string failed");
	}
	
	@Test
	public void setPolicyTypeTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		policy.setPolicyType(PolicyType.CREATE);
		assertEquals(PolicyType.CREATE, policy.getType(), "Conversion to read policy type to string failed");
	}
	
	// This test will work only if the method getLogicOperator() from ConditionGrup.java is set to public
//	@Test
//	public void conditionGroupLogicOperatorToStringTest()
//	{
//		Resource testResource = new Resource("TestingResource");
//		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
//		ConditionGroup group1 = policy.addConditionGroup();
//		assertEquals("AND", group1.getLogicOperator(), "Getting condition group logic operator test failed");
//	}
	
	// This test will work only if the method setLogicOperator(String logicOperator) from ConditionGrup.java is set to public
//	@Test
//	public void stringToConditionGroupLogicOperatorTest()
//	{
//		Resource testResource = new Resource("TestingResource");
//		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
//		ConditionGroup group1 = policy.addConditionGroup();
//		group1.setLogicOperator("or");
//		assertEquals(LogicOperator.OR, group1.getOperator(), "Getting condition group logic operator test failed");
//	}
	
	@Test
	public void getConditionGroupLogicOperatorTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		ConditionGroup group1 = policy.addConditionGroup();
		assertEquals(LogicOperator.AND, group1.getOperator(), "Getting condition group logic operator test failed");
	}
	
	@Test
	public void setConditionGroupLogicOperatorTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		ConditionGroup group1 = policy.addConditionGroup();
		group1.setLogicOperator(LogicOperator.OR);
		assertEquals(LogicOperator.OR, group1.getOperator(), "Getting condition group logic operator test failed");
	}
	
	@Test
	public void addingPolicyConditionGroups() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		ConditionGroup group1 = policy.addConditionGroup();
		assertEquals(policy, group1.getAbacPolicy(), "Adding policy condition group failed");
	}
	
	@Test
	public void addingConditionGroupsToAConditionGroup() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		ConditionGroup group1 = policy.addConditionGroup();
		ConditionGroup groupA = group1.addConditionGroup();
		ConditionGroup groupB = group1.addConditionGroup(LogicOperator.OR);
		assertEquals(group1, groupA.getParentConditionGroup(), "Adding condition groups to a condition group failed");
		assertEquals(group1, groupB.getParentConditionGroup(), "Adding condition groups to a condition group failed");
		assertEquals(policy, groupA.getParentConditionGroup().getAbacPolicy(), "Adding condition groups to a condition group failed");
	}
	
	@Test
	public void addingEntityConditionToConditionGroupTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		policy.setLogicOperator(LogicOperator.OR);
		ConditionGroup groupA = policy.addConditionGroup(LogicOperator.OR);
		ConditionGroup groupB = policy.addConditionGroup();
		groupA.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrator");
		groupA.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Management");
		groupB.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Project Manager");
		groupB.addEntityCondition(UserAttribute.PROJECTS, ComparisonOperator.EQUALS, "Sample Project");
		
		assertEquals(2, testResource.getPolicies().get(0).getConditionGroups().get(0).getEntityConditions().size(),
					 "Adding entity condition to condition group test failed");
		assertEquals(2, testResource.getPolicies().get(0).getConditionGroups().get(1).getEntityConditions().size(),
				 "Adding entity condition to condition group test failed");
	}
	
	@Test
	public void addingRecordConditionToConditionGroupTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		policy.setLogicOperator(LogicOperator.OR);
		ConditionGroup groupA = policy.addConditionGroup();
		ConditionGroup groupB = policy.addConditionGroup(LogicOperator.OR);
		groupA.addRecordCondition(UserAttribute.PROJECTS, ComparisonOperator.EQUALS, ResourceAttribute.PROJECTS);
		groupB.addRecordCondition(UserAttribute.FULL_NAME, ComparisonOperator.EQUALS, ResourceAttribute.P_MANAGERS);
		groupB.addRecordCondition(UserAttribute.FULL_NAME, ComparisonOperator.EQUALS, ResourceAttribute.SUPERINTENDENTS);
		groupB.addRecordCondition(UserAttribute.FULL_NAME, ComparisonOperator.EQUALS, ResourceAttribute.FOREMEN);		
		
		assertEquals(1, testResource.getPolicies().get(0).getConditionGroups().get(0).getRecordConditions().size(), 
					 "Adding entity condition to condition group test failed");
		assertEquals(3, testResource.getPolicies().get(0).getConditionGroups().get(1).getRecordConditions().size(), 
					 "Adding entity condition to condition group test failed");
	}
}
