package com.unlimitedcompanies.coms.domain.unitTests;

import static org.junit.jupiter.api.Assertions.*;

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
	public void getConditionGroupLogicOperatorTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		ConditionGroup group1 = policy.addConditionGroup();
		assertEquals(LogicOperator.AND, group1.getLogicOperator(), "Getting condition group logic operator test failed");
	}
	
	@Test
	public void setConditionGroupLogicOperatorTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		ConditionGroup group1 = policy.addConditionGroup();
		group1.setLogicOperator(LogicOperator.OR);
		assertTrue(policy.getConditionGroups().size() > 0, "Getting condition group logic operator test failed");
		assertEquals(LogicOperator.OR, group1.getLogicOperator(), "Getting condition group logic operator test failed");
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
	public void addMultipleLevelConditionGroupsUnitTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		policy.setLogicOperator(LogicOperator.OR);
		policy.addConditionGroup();
		policy.addConditionGroup();
		ConditionGroup groupA = policy.addConditionGroup();
		ConditionGroup groupB = groupA.addConditionGroup(LogicOperator.OR);
		groupA.addConditionGroup();
		groupB.addConditionGroup();
		groupB.addConditionGroup();
		
		assertEquals(3, policy.getConditionGroups().size(), "Adding multiple level condition groups unit test");
		assertEquals(2, policy.getConditionGroups().get(2).getConditionGroups().size(), 
				"Adding multiple level condition groups unit test");
		assertEquals(2, policy.getConditionGroups().get(2).getConditionGroups().get(0).getConditionGroups().size(), 
				"Adding multiple level condition groups unit test");
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
	public void addingAttributeConditionToConditionGroupTest() throws DuplicatedResourcePolicyException
	{
		Resource testResource = new Resource("TestingResource");
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		policy.setLogicOperator(LogicOperator.OR);
		ConditionGroup groupA = policy.addConditionGroup();
		ConditionGroup groupB = policy.addConditionGroup(LogicOperator.OR);
		groupA.addAttributeCondition(UserAttribute.PROJECTS, ComparisonOperator.EQUALS, ResourceAttribute.PROJECTS);
		groupB.addAttributeCondition(UserAttribute.FULL_NAME, ComparisonOperator.EQUALS, ResourceAttribute.P_MANAGERS);
		groupB.addAttributeCondition(UserAttribute.FULL_NAME, ComparisonOperator.EQUALS, ResourceAttribute.SUPERINTENDENTS);
		groupB.addAttributeCondition(UserAttribute.FULL_NAME, ComparisonOperator.EQUALS, ResourceAttribute.FOREMEN);		
		
		assertEquals(1, testResource.getPolicies().get(0).getConditionGroups().get(0).getAttributeConditions().size(), 
					 "Adding attribute condition to condition group test failed");
		assertEquals(3, testResource.getPolicies().get(0).getConditionGroups().get(1).getAttributeConditions().size(), 
					 "Adding attribute condition to condition group test failed");
	}
	
	@Test
	public void addingFieldConditionToConditionGroupTest() throws DuplicatedResourcePolicyException
	{
		
		
		// TODO: This test is producing a double checking of the fields in the class ConditionGroup.java
		// TODO: This issue must be solved
		fail();
		
		
		Resource testResource = new Resource("TestingResource");
		new ResourceField("TestField1", false, testResource);
		new ResourceField("TestField2", false, testResource);
		new ResourceField("TestField3", false, testResource);
		new ResourceField("TestField4", false, testResource);
		
		ABACPolicy policy = new ABACPolicy("TestPolicy", PolicyType.READ, testResource);
		policy.setLogicOperator(LogicOperator.OR);
		ConditionGroup groupA = policy.addConditionGroup();
		ConditionGroup groupB = policy.addConditionGroup(LogicOperator.OR);
		groupA.addFieldConditions("TestField1", ComparisonOperator.EQUALS, "Field1Value");
		groupB.addFieldConditions("TestField2", ComparisonOperator.NOT_EQUALS, "Field2Value");
		groupB.addFieldConditions("TestField3", ComparisonOperator.EQUALS, "Field3Value");
		groupB.addFieldConditions("TestField4", ComparisonOperator.EQUALS, "Field4Value");
		
		assertEquals(1, testResource.getPolicies().get(0).getConditionGroups().get(0).getFieldConditions().size(), 
					 "Adding field condition to condition group test failed");
		assertEquals(3, testResource.getPolicies().get(0).getConditionGroups().get(1).getFieldConditions().size(), 
					 "Adding field condition to condition group test failed");
	}

}
