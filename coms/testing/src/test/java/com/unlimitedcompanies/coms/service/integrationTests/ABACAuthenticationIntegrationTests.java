package com.unlimitedcompanies.coms.service.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.ComparisonOperator;
import com.unlimitedcompanies.coms.domain.abac.LogicOperator;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.domain.abac.ResourceAttribute;
import com.unlimitedcompanies.coms.domain.abac.ResourceField;
import com.unlimitedcompanies.coms.domain.abac.ResourceReadPolicy;
import com.unlimitedcompanies.coms.domain.abac.UserAttribute;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.ABACService;
import com.unlimitedcompanies.coms.service.security.AuthService;
import com.unlimitedcompanies.coms.service.system.SystemService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
@Transactional
@ActiveProfiles("integrationTesting")
class ABACAuthenticationIntegrationTests
{	
	@Autowired
	private ABACService abacService;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private AuthService authService;
	
	/* 
	 * TODO: Create some testing using the project names (when the code to save projects is ready) and 
	 * fully test the policies with entityConditions, attributeConditions and fieldCoditions 
	 * eg. a user can only access projects that are related to the user itself 
	 * or a superintendent can see employees related to a project he supervises
	*/
	
	@Test
	public void numberOfPolicyTests() throws Exception
	{
		systemService.initialSetup();
		assertEquals(1, abacService.getNumberOfPolicies(), "Number of policies found in the db failed");
	}
	
	@Test
	public void numberOfEntityConditionTest() throws Exception
	{
		systemService.initialSetup();
		assertEquals(1, abacService.getNumberOfEntityConditions(), "Number of entity conditions found in the db failed");
	}
	
	@Test
	public void numberOfAttributeConditionTest() throws Exception
	{
		systemService.initialSetup();
		assertEquals(0, abacService.getNumberOfAttributeConditions(), "Number of record conditions found in the db failed");
	}
	
	@Test
	public void checkExcludedResources() throws Exception
	{
		systemService.initialSetup();
		
		assertThrows(RecordNotFoundException.class, ()-> abacService.searchResourceByName("ContactAddress"));
		assertThrows(RecordNotFoundException.class, ()-> abacService.searchResourceByName("ContactPhone"));
	}
	
	@Test
	public void saveSingleResourcePolicyTest() throws Exception
	{
		systemService.initialSetup();
		Resource abacResource = abacService.searchResourceByNameWithFields("AbacPolicy");
		
		AbacPolicy policy = new AbacPolicy("PolicyRead", PolicyType.READ, abacResource);
		abacService.savePolicy(policy, "Administrator");
		
		assertEquals(2, abacService.getNumberOfPolicies(), "Saving resource policy test failed");
	}
	
	@Test
	public void saveCreateAndDeleteResourcePolicyTest() throws Exception
	{		
		systemService.initialSetup();
		Resource abacPolicyResource = abacService.searchResourceByName("AbacPolicy");
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");
		
		AbacPolicy abacPolicy = new AbacPolicy("PolicyRead", PolicyType.READ, abacPolicyResource);
		abacPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacPolicy, "administrator");
		
		AbacPolicy policy = new AbacPolicy("ContactUpdate", PolicyType.UPDATE, contactResource);
		policy.setCdPolicy(true, true);
		abacService.savePolicy(policy, "Administrator");
		
		assertTrue(abacService.searchPolicy(contactResource, PolicyType.UPDATE, "administrator").getCdPolicy().isCreatePolicy(), 
				"Resource create policy test failed");
		assertTrue(abacService.searchPolicy(contactResource, PolicyType.UPDATE, "administrator").getCdPolicy().isDeletePolicy(), 
				"Resource delete policy test failed");
	}
	
	@Test
	public void savePolicyWithMultipleSubPoliciesTest() throws Exception
	{		
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");
		Resource abacResource = abacService.searchResourceByName("AbacPolicy");
		
		AbacPolicy readPolicies = new AbacPolicy("PolicyRead", PolicyType.READ, abacResource);
		readPolicies.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(readPolicies, "Administrator");
		
		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		AbacPolicy subPolicy1 = contactReadPolicy.addSubPolicy(LogicOperator.OR);
		subPolicy1.addSubPolicy(LogicOperator.AND);
		subPolicy1.addSubPolicy(LogicOperator.OR);
		subPolicy1.addSubPolicy(LogicOperator.AND);
		contactReadPolicy.addSubPolicy(LogicOperator.AND);
		contactReadPolicy.addSubPolicy(LogicOperator.OR);
		
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		assertEquals(9, abacService.getNumberOfPolicies());
		
		assertEquals(3, abacService.searchPolicy(contactResource, PolicyType.READ, "administrator").getSubPolicies().size());
	}
	
	@Test
	public void savePolicyWithEntityConditionTest() throws Exception
	{
		systemService.initialSetup();
		Resource abacPolicyResource = abacService.searchResourceByNameWithFields("AbacPolicy");
		
		AbacPolicy policy = new AbacPolicy("AbacPolicyRead", PolicyType.READ, abacPolicyResource);
		policy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		policy.addEntityCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, "Admin");
		
		abacService.savePolicy(policy, "administrator");
		
		assertEquals(3, abacService.getNumberOfEntityConditions(), 
				"Saving policy with multiple entity condition integration test failed"); 
	}
	
	@Test
	public void savePolicyWithAttributeConditionTest() throws Exception
	{
		systemService.initialSetup();
		Resource abacPolicyResource = abacService.searchResourceByNameWithFields("AbacPolicy");
		
		AbacPolicy policy = new AbacPolicy("UserRead", PolicyType.READ, abacPolicyResource);
		policy.addAttributeCondition(ResourceAttribute.PROJECT_NAME, ComparisonOperator.EQUALS, UserAttribute.PROJECTS);
		policy.addAttributeCondition(ResourceAttribute.P_MANAGERS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		
		abacService.savePolicy(policy, "administrator");
		
		assertEquals(2, abacService.getNumberOfAttributeConditions(), 
				"Saving policy with multiple entity condition integration test failed");
	}
	
	@Test
	public void simplePolicyReadTest() throws Exception
	{
		systemService.initialSetup();
		Resource abacPolicyResource = abacService.searchResourceByNameWithFields("AbacPolicy");
		
		assertThrows(NoResourceAccessException.class, () -> abacService.searchPolicy(abacPolicyResource, PolicyType.UPDATE, "administrator"));
		
		AbacPolicy policy = new AbacPolicy("PolicyRead", PolicyType.READ, abacPolicyResource);
		policy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(policy, "administrator");
		
		assertEquals("PolicyUpdate", abacService.searchPolicy(abacPolicyResource, PolicyType.UPDATE, "administrator").getPolicyName(),
				"Simple read permission integration test failed");
	}
	
	@Test
	public void simplePolicyNoReadPermissionTest() throws Exception
	{
		systemService.initialSetup();
		Resource abacPolicyResource = abacService.searchResourceByNameWithFields("AbacPolicy");
		
		assertThrows(NoResourceAccessException.class, () -> abacService.searchPolicy(abacPolicyResource, PolicyType.UPDATE, "administrator"));
	}
	
	@Test
	public void simplePolicyNotFoundTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		
		assertThrows(NoResourceAccessException.class, () -> abacService.searchPolicy(contactResource, PolicyType.READ, "administrator"));
	}
	
	@Test
	public void policyEntityConditionTest() throws Exception
	{
		systemService.initialSetup();
		Resource abacPolicyResource = abacService.searchResourceByNameWithFields("AbacPolicy");
		
		AbacPolicy policy = new AbacPolicy("PolicyRead", PolicyType.READ, abacPolicyResource);
		policy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		policy.addEntityCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, "administrator");
		abacService.savePolicy(policy, "administrator");
		
		AbacPolicy foundPolicy = abacService.searchPolicy(abacPolicyResource, PolicyType.READ, "administrator");
		
		assertEquals(2, foundPolicy.getEntityConditions().size());
		assertEquals("PolicyRead", foundPolicy.getPolicyName());
	}
	
	@Test
	public void policyAttributeConditionTest() throws Exception
	{
		systemService.initialSetup();
		Resource abacPolicyResource = abacService.searchResourceByNameWithFields("AbacPolicy");
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");
		
		AbacPolicy abacPolicy = new AbacPolicy("PolicyRead", PolicyType.READ, abacPolicyResource);
		abacPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacPolicy, "administrator");
		
		AbacPolicy policy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		policy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		policy.addAttributeCondition(ResourceAttribute.P_MANAGERS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		policy.addAttributeCondition(ResourceAttribute.P_SUPERINTENDENTS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		abacService.savePolicy(policy, "administrator");		
		
		AbacPolicy foundPolicy = abacService.searchPolicy(contactResource, PolicyType.READ, "administrator");
		
		assertEquals(2, foundPolicy.getAttributeConditions().size(), 
				"Policy attribute condition integration test failed");
	}	
	
	@Test
	public void policyFieldConditionTest() throws Exception 
	{
		systemService.initialSetup();
		User administratorUser = systemService.searchFullUserByUsername("administrator");
		Resource abacPolicyResource = abacService.searchResourceByNameWithFields("AbacPolicy");
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");
		
		AbacPolicy abacPolicy = new AbacPolicy("PolicyRead", PolicyType.READ, abacPolicyResource);
		abacPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacPolicy, "administrator");
		
		AbacPolicy policy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		policy.setLogicOperator(LogicOperator.OR);
		policy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		policy.addFieldConditions("lastName", ComparisonOperator.EQUALS, "Doe");
		policy.addFieldConditions("firstName", ComparisonOperator.EQUALS, "Richard");
		abacService.savePolicy(policy, "administrator");
		
		AbacPolicy foundPolicy = abacService.searchPolicy(contactResource, PolicyType.READ, "administrator");
		ResourceReadPolicy resourceReadPolicy = foundPolicy.getReadPolicy(Contact.class, administratorUser);
		assertEquals(2, foundPolicy.getFieldConditions().size(), 
				"Policy attribute condition integration test failed");
		assertTrue(resourceReadPolicy.isReadGranted(), "Policy attribute condition integration test failed");
		assertTrue(resourceReadPolicy.getReadConditions().equals("(contact.lastName = 'Doe' OR contact.firstName = 'Richard')") ||
				resourceReadPolicy.getReadConditions().equals("(contact.firstName = 'Richard' OR contact.lastName = 'Doe')"));
	}
	
	/*
	 * Resource Field Restriction Tests
	 */
	
	@Test
	public void numberOfRestrictedFieldsTest() throws Exception
	{
		assertEquals(0, abacService.getNumberOfRestrictedFields(), "Number of restricted fields found in the db failed");
	}
	
	@Test
	public void addRestrictedFieldToRoleTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");
		
		AbacPolicy contactUpdate = new AbacPolicy("contactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdate.setCdPolicy(true, false);
		contactUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdate, "administrator");
		
		AbacPolicy contactRead = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactRead, "administrator");
		
		AbacPolicy roleUpdate = new AbacPolicy("RoleUpdate", PolicyType.UPDATE, roleResource);
		roleUpdate.setCdPolicy(true, false);
		roleUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleUpdate, "administrator");
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");
		
		AbacPolicy restrictionFieldUpdate = new AbacPolicy("RestrictedUpdate", PolicyType.UPDATE, restrictedFieldResource);
		restrictionFieldUpdate.setCdPolicy(true, false);
		restrictionFieldUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(restrictionFieldUpdate, "administrator");
		
		Role adminRole = authService.searchRoleByName("Administrators", "administrator");
		contactResource = abacService.searchResourceByNameWithFields("Contact");
		ResourceField field = contactResource.getResourceFieldByName("email");
		abacService.addFieldRestriction(adminRole.getRoleId(), field.getResourceFieldId(), "administrator");
		
		assertEquals(1, abacService.getNumberOfRestrictedFields(), "Add restricted fields in the db failed");
	}
	
	@Test
	public void restrictRoleForResourceFieldTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");
		
		AbacPolicy contactUpdate = new AbacPolicy("contactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdate.setCdPolicy(true, false);
		contactUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdate, "administrator");
		
		AbacPolicy contactRead = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactRead, "administrator");
		
		AbacPolicy roleUpdate = new AbacPolicy("RoleUpdate", PolicyType.UPDATE, roleResource);
		roleUpdate.setCdPolicy(true, false);
		roleUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleUpdate, "administrator");
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");
		
		AbacPolicy restrictionFieldUpdate = new AbacPolicy("RestrictedUpdate", PolicyType.UPDATE, restrictedFieldResource);
		restrictionFieldUpdate.setCdPolicy(true, false);
		restrictionFieldUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(restrictionFieldUpdate, "administrator");

		Role adminRole = authService.searchRoleByName("Administrators", "administrator");
		contactResource = abacService.searchResourceByNameWithFields("Contact");
		ResourceField contactEmailField = contactResource.getResourceFieldByName("email");
		
		abacService.addFieldRestriction(adminRole.getRoleId(), contactEmailField.getResourceFieldId(), "administrator");
		
		assertEquals(1, abacService.getNumberOfRestrictedFields(), "Add restricted fields in the db failed");
	}
	
	@Test
	public void restrictedFieldForRoleResourceNotAllowed() throws Exception
	{
		systemService.initialSetup();
		Resource roleResource = abacService.searchResourceByNameWithFields("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");
		
		// Add ABAC policies to allow storing field restrictions
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");
		
		AbacPolicy fieldRestrictPolicy = new AbacPolicy("RestrictedFieldUpdate", PolicyType.UPDATE, restrictedFieldResource);
		fieldRestrictPolicy.setCdPolicy(true, false);
		fieldRestrictPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(fieldRestrictPolicy, "administrator");
		
		// Add field restrictions
		Role role = authService.searchRoleByName("Administrators", "administrator");
		ResourceField restrictedField = roleResource.getResourceFieldByName("roleName");
		
		assertThrows(NoResourceAccessException.class, 
				()-> abacService.addFieldRestriction(role.getRoleId(), restrictedField.getResourceFieldId(), "administrator"));
	}
	
	@Test
	public void retrieveRoleWithRestrictedFields() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");
		
		AbacPolicy contactUpdate = new AbacPolicy("contactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdate.setCdPolicy(true, false);
		contactUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdate, "administrator");
		
		AbacPolicy contactRead = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactRead, "administrator");
		
		AbacPolicy roleUpdate = new AbacPolicy("RoleUpdate", PolicyType.UPDATE, roleResource);
		roleUpdate.setCdPolicy(true, false);
		roleUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleUpdate, "administrator");
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");
		
		AbacPolicy restrictionFieldUpdate = new AbacPolicy("RestrictedUpdate", PolicyType.UPDATE, restrictedFieldResource);
		restrictionFieldUpdate.setCdPolicy(true, false);
		restrictionFieldUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(restrictionFieldUpdate, "administrator");

		Role adminRole = authService.searchRoleByName("Administrators", "administrator");
		contactResource = abacService.searchResourceByNameWithFields("Contact");
		ResourceField field = contactResource.getResourceFieldByName("email");
		
		abacService.addFieldRestriction(adminRole.getRoleId(), field.getResourceFieldId(), "administrator");
		
		adminRole = systemService.roleWithAllRestrictedFields(adminRole.getRoleId());
		List<ResourceField> restrictedFields = adminRole.getRestrictedFields();
		assertEquals(1, restrictedFields.size(), "Retrieve restricted fields for a role test failed");
	}

	@Test
	public void findAResourceTest() throws Exception
	{
		systemService.checkAllResources();
		Resource resource = abacService.searchResourceByName("Contact");
		assertEquals("Contact", resource.getResourceName(), "Find resource integration test failed");
	}
}
