package com.unlimitedcompanies.coms.service.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.unlimitedcompanies.coms.data.exceptions.InvalidPolicyException;
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
import com.unlimitedcompanies.coms.service.security.AbacService;
import com.unlimitedcompanies.coms.service.security.AuthService;
import com.unlimitedcompanies.coms.service.security.ContactService;
import com.unlimitedcompanies.coms.service.system.SystemService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
@Transactional
@ActiveProfiles("integrationTesting")
class ABACAuthenticationIntegrationTests
{	
	@Autowired
	private AbacService abacService;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private AuthService authService;

	@Autowired
	private ContactService contactService;
	
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
		assertEquals(8, abacService.getNumberOfPolicies());
	}
	
	@Test
	public void numberOfEntityConditionTest() throws Exception
	{
		systemService.initialSetup();
		assertEquals(8, abacService.getNumberOfEntityConditions());
	}
	
	@Test
	public void numberOfAttributeConditionTest() throws Exception
	{
		systemService.initialSetup();
		assertEquals(0, abacService.getNumberOfAttributeConditions());
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
		Resource projectResource = abacService.searchResourceByNameWithFields("Project");
		
		AbacPolicy policy = new AbacPolicy("ProjectRead", PolicyType.READ, projectResource);
		abacService.savePolicy(policy, "Administrator");
		
		assertEquals(9, abacService.getNumberOfPolicies());
	}
	
	@Test
	public void duplicateResourcePolicyNotAllowedTest() throws Exception
	{
		systemService.initialSetup();
		Resource projectResource = abacService.searchResourceByNameWithFields("Project");
		
		AbacPolicy policy1 = new AbacPolicy("ProjectRead", PolicyType.READ, projectResource);
		abacService.savePolicy(policy1, "Administrator");
		
		AbacPolicy policy2 = new AbacPolicy("Project", PolicyType.READ, projectResource);
		assertThrows(InvalidPolicyException.class, ()-> abacService.savePolicy(policy2, "administrator"));
	}
	
	@Test
	public void readTheCreateAndDeletePolicyTest() throws Exception
	{		
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");
		
		assertTrue(abacService.searchPolicy(contactResource, PolicyType.UPDATE, "administrator").getCdPolicy().isCreatePolicy());
		assertTrue(abacService.searchPolicy(contactResource, PolicyType.UPDATE, "administrator").getCdPolicy().isDeletePolicy());
	}
	
	@Test
	public void savePolicyWithMultipleSubPoliciesTest() throws Exception
	{		
		systemService.initialSetup();
		Resource employeeResource = abacService.searchResourceByNameWithFields("Employee");
		
		AbacPolicy employeeReadPolicy = new AbacPolicy("EmployeeRead", PolicyType.READ, employeeResource);
		employeeReadPolicy.addSubPolicy(LogicOperator.AND);
		employeeReadPolicy.addSubPolicy(LogicOperator.OR);
		AbacPolicy secondLevel = employeeReadPolicy.addSubPolicy(LogicOperator.OR);
		secondLevel.addSubPolicy(LogicOperator.AND);
		secondLevel.addSubPolicy(LogicOperator.OR);
		secondLevel.addSubPolicy(LogicOperator.AND);
		AbacPolicy thirdLevel = secondLevel.addSubPolicy(LogicOperator.OR);
		thirdLevel.addSubPolicy();
		thirdLevel.addSubPolicy();
		thirdLevel.addSubPolicy();
		thirdLevel.addSubPolicy();
		thirdLevel.addSubPolicy();
		abacService.savePolicy(employeeReadPolicy, "administrator");
		
		assertEquals(21, abacService.getNumberOfPolicies());
		assertEquals(3, abacService.searchPolicy(employeeResource, PolicyType.READ, "administrator").getSubPolicies().size());
		
		AbacPolicy foundPolicy = abacService.searchPolicyById(employeeReadPolicy.getAbacPolicyId(), "administrator");
		assertEquals(3, foundPolicy.getSubPolicies().size());
		
		AbacPolicy second = null;
		for (AbacPolicy policy : foundPolicy.getSubPolicies())
		{
			if (policy.getSubPolicies() != null && policy.getSubPolicies().size() > 0)
			{
				second = policy;
			}
		}
		assertNotNull(second);
		assertEquals(4, second.getSubPolicies().size());
	}
	
	@Test
	public void savePolicyWithEntityConditionTest() throws Exception
	{
		systemService.initialSetup();
		Resource employeeResource = abacService.searchResourceByNameWithFields("Employee");
		
		AbacPolicy policy = new AbacPolicy("Employee", PolicyType.READ, employeeResource);
		policy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		policy.addEntityCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, "Admin");
		
		abacService.savePolicy(policy, "administrator");
		
		assertEquals(2, abacService.searchPolicy(employeeResource, PolicyType.READ, "administrator").getEntityConditions().size()); 
	}
	
	@Test
	public void savePolicyWithAttributeConditionTest() throws Exception
	{
		systemService.initialSetup();
		Resource employeeResource = abacService.searchResourceByNameWithFields("Employee");
		
		AbacPolicy policy = new AbacPolicy("EmployeeRead", PolicyType.READ, employeeResource);
		policy.addAttributeCondition(ResourceAttribute.PROJECT_NAME, ComparisonOperator.EQUALS, UserAttribute.PROJECTS);
		policy.addAttributeCondition(ResourceAttribute.P_MANAGERS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		
		abacService.savePolicy(policy, "administrator");
		
		assertEquals(2, abacService.getNumberOfAttributeConditions());
	}
	
	@Test
	public void policyEntityConditionTest() throws Exception
	{
		systemService.initialSetup();
		Resource projectResource = abacService.searchResourceByName("Project");
		
		AbacPolicy policy = new AbacPolicy("ProjectRead", PolicyType.READ, projectResource);
		policy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		policy.addEntityCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, "administrator");
		abacService.savePolicy(policy, "administrator");
		
		AbacPolicy foundPolicy = abacService.searchPolicy(projectResource, PolicyType.READ, "administrator");
		
		assertEquals(2, foundPolicy.getEntityConditions().size());
		assertEquals("ProjectRead", foundPolicy.getPolicyName());
	}
	
	@Test
	public void policyAttributeConditionTest() throws Exception
	{
		systemService.initialSetup();
		Resource employeeResource = abacService.searchResourceByNameWithFields("Employee");
		
		AbacPolicy policy = new AbacPolicy("EmployeeRead", PolicyType.READ, employeeResource);
		policy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		policy.addAttributeCondition(ResourceAttribute.P_MANAGERS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		policy.addAttributeCondition(ResourceAttribute.P_SUPERINTENDENTS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		abacService.savePolicy(policy, "administrator");
		
		AbacPolicy foundPolicy = abacService.searchPolicy(employeeResource, PolicyType.READ, "administrator");
		
		assertEquals(2, foundPolicy.getAttributeConditions().size());
	}	
	
	@Test
	public void policyFieldConditionTest() throws Exception 
	{
		systemService.initialSetup();
		User administratorUser = systemService.searchFullUserByUsername("administrator");
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");
		
		AbacPolicy policy = abacService.searchPolicyWithRestrictedFields(contactResource, PolicyType.READ, "administrator");		
		policy.setLogicOperator(LogicOperator.OR);
		policy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		policy.addFieldConditions("lastName", ComparisonOperator.EQUALS, "Doe");
		policy.addFieldConditions("firstName", ComparisonOperator.EQUALS, "Richard");
		abacService.updatePolicy(policy.getAbacPolicyId(), policy, "administrator");
		
		AbacPolicy foundPolicy = abacService.searchPolicy(contactResource, PolicyType.READ, "administrator");
		ResourceReadPolicy resourceReadPolicy = foundPolicy.getReadPolicy(Contact.class, administratorUser);
		assertEquals(2, foundPolicy.getFieldConditions().size(), 
				"Policy attribute condition integration test failed");
		assertTrue(resourceReadPolicy.isReadGranted(), "Policy attribute condition integration test failed");
		assertTrue(resourceReadPolicy.getReadConditions().equals("(contact.lastName = 'Doe' OR contact.firstName = 'Richard')") ||
				resourceReadPolicy.getReadConditions().equals("(contact.firstName = 'Richard' OR contact.lastName = 'Doe')"));
	}
	
	@Test
	public void simplePolicyReadTest() throws Exception
	{
		systemService.initialSetup();
		Resource abacPolicyResource = abacService.searchResourceByName("AbacPolicy");
		
		assertEquals("PolicyUpdate", abacService.searchPolicy(abacPolicyResource, PolicyType.UPDATE, "administrator").getPolicyName());
	}
	
	@Test
	public void simplePolicyNoReadPermissionTest() throws Exception
	{
		systemService.initialSetup();
		
		Contact contact = new Contact("Manager", null, null);
		contactService.saveContact(contact, "administrator");
		authService.saveUser(new User("manager", "123", contact), "administrator");
		Resource abacPolicyResource = abacService.searchResourceByNameWithFields("AbacPolicy");		
				
		assertThrows(NoResourceAccessException.class, () -> abacService.searchPolicy(abacPolicyResource, PolicyType.UPDATE, "manager"));
	}
	
	@Test
	public void simplePolicyNotFoundTest() throws Exception
	{
		systemService.initialSetup();
		Resource projectResource = abacService.searchResourceByName("Project");
		
		assertThrows(NoResourceAccessException.class, () -> abacService.searchPolicy(projectResource, PolicyType.READ, "administrator"));
	}
	
	@Test
	public void searchPolicyByIdTest() throws Exception
	{
		systemService.initialSetup();
		Resource abacPolicyResource = abacService.searchResourceByName("AbacPolicy");
		AbacPolicy abacPolicy = abacService.searchPolicy(abacPolicyResource, PolicyType.READ, "administrator");
		
		assertEquals(abacPolicy.getPolicyName(), abacService.searchPolicyById(abacPolicy.getAbacPolicyId(), "administrator").getPolicyName());
	}
	
	@Test
	public void readPoliciesByRangeTest() throws Exception
	{
		systemService.initialSetup();
		
		assertEquals(5, abacService.searchPoliciesByRange(5, 1, "administrator").size());
		assertEquals(3, abacService.searchPoliciesByRange(5, 2, "administrator").size());
	}
	
	@Test
	public void updateExistingPolicyTest() throws Exception
	{
		systemService.initialSetup();
		
		Resource projectResource = abacService.searchResourceByName("Project");
		AbacPolicy projectReadPolicy = new AbacPolicy("ProjectRead", PolicyType.READ, projectResource);
		projectReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		projectReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Operation Managers");
		projectReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Project Managers");
		projectReadPolicy.addAttributeCondition(ResourceAttribute.P_MANAGERS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		projectReadPolicy.addAttributeCondition(ResourceAttribute.P_SUPERINTENDENTS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		AbacPolicy projectReadPolicyB = projectReadPolicy.addSubPolicy();
		projectReadPolicyB.addEntityCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, "jdoe");
		abacService.savePolicy(projectReadPolicy, "administrator");
		
		projectReadPolicy = abacService.searchPolicy(projectResource, PolicyType.READ, "administrator");
		assertEquals(1, projectReadPolicy.getSubPolicies().size());
		assertEquals(3, projectReadPolicy.getEntityConditions().size());
		assertEquals(2, projectReadPolicy.getAttributeConditions().size());
		assertEquals(10, abacService.getNumberOfPolicies());
		assertEquals(12, abacService.getNumberOfEntityConditions());
		
		projectReadPolicy.getSubPolicies().clear();
		projectReadPolicy.getEntityConditions().clear();
		projectReadPolicy.addAttributeCondition(ResourceAttribute.P_FOREMEN, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		abacService.updatePolicy(projectReadPolicy.getAbacPolicyId(), projectReadPolicy, "administrator");
		
		projectReadPolicy = abacService.searchPolicy(projectResource, PolicyType.READ, "administrator");
		assertEquals(0, projectReadPolicy.getSubPolicies().size());
		assertEquals(0, projectReadPolicy.getEntityConditions().size());
		assertEquals(3, projectReadPolicy.getAttributeConditions().size());
		assertEquals(9, abacService.getNumberOfPolicies());
		assertEquals(8, abacService.getNumberOfEntityConditions());
	}
	
	@Test
	public void deleteExistingPolicyTest() throws Exception
	{
		systemService.initialSetup();
		
		Resource projectResource = abacService.searchResourceByName("Project");
		AbacPolicy projectReadPolicy = new AbacPolicy("ProjectRead", PolicyType.READ, projectResource);
		projectReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		projectReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Operation Managers");
		projectReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Project Managers");
		projectReadPolicy.addAttributeCondition(ResourceAttribute.P_MANAGERS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		projectReadPolicy.addAttributeCondition(ResourceAttribute.P_SUPERINTENDENTS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
		AbacPolicy projectReadPolicyB = projectReadPolicy.addSubPolicy();
		projectReadPolicyB.addEntityCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, "jdoe");
		abacService.savePolicy(projectReadPolicy, "administrator");
		
		projectReadPolicy = abacService.searchPolicy(projectResource, PolicyType.READ, "administrator");
		assertEquals(1, projectReadPolicy.getSubPolicies().size());
		assertEquals(3, projectReadPolicy.getEntityConditions().size());
		assertEquals(2, projectReadPolicy.getAttributeConditions().size());
		assertEquals(10, abacService.getNumberOfPolicies());
		assertEquals(12, abacService.getNumberOfEntityConditions());
		
		abacService.deletePolicy(projectReadPolicy.getAbacPolicyId(), "administrator");
		
		assertThrows(NoResourceAccessException.class, ()-> abacService.searchPolicy(projectResource, PolicyType.READ, "administrator"));
		assertEquals(8, abacService.getNumberOfPolicies());
		assertEquals(8, abacService.getNumberOfEntityConditions());
	}
	
	/*
	 * Resource Field Restriction Tests
	 */
	
	@Test
	public void numberOfRestrictedFieldsTest() throws Exception
	{
		assertEquals(0, abacService.getNumberOfRestrictedFields());
	}
	
	@Test
	public void addRestrictedFieldToRoleTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");
		
		AbacPolicy restrictionFieldUpdate = new AbacPolicy("RestrictedUpdate", PolicyType.UPDATE, restrictedFieldResource);
		restrictionFieldUpdate.setCdPolicy(true, false);
		restrictionFieldUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(restrictionFieldUpdate, "administrator");
		
		Role adminRole = authService.searchRoleByName("Administrators", "administrator");
		contactResource = abacService.searchResourceByNameWithFields("Contact");
		ResourceField field = contactResource.getResourceFieldByName("email");
		abacService.addFieldRestriction(adminRole.getRoleId(), field.getResourceFieldId(), "administrator");
		
		assertEquals(1, abacService.getNumberOfRestrictedFields());
	}
	
	@Test
	public void restrictRoleForResourceFieldTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");
		
		AbacPolicy restrictionFieldUpdate = new AbacPolicy("RestrictedUpdate", PolicyType.UPDATE, restrictedFieldResource);
		restrictionFieldUpdate.setCdPolicy(true, false);
		restrictionFieldUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(restrictionFieldUpdate, "administrator");

		Role adminRole = authService.searchRoleByName("Administrators", "administrator");
		contactResource = abacService.searchResourceByNameWithFields("Contact");
		ResourceField contactEmailField = contactResource.getResourceFieldByName("email");
		
		abacService.addFieldRestriction(adminRole.getRoleId(), contactEmailField.getResourceFieldId(), "administrator");
		
		assertEquals(1, abacService.getNumberOfRestrictedFields());
	}
	
	@Test
	public void restrictedFieldForRoleResourceNotAllowed() throws Exception
	{
		systemService.initialSetup();
		Resource roleResource = abacService.searchResourceByNameWithFields("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");
		
		// Add ABAC policies to allow storing field restrictions
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
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");
		
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
		assertEquals(1, restrictedFields.size());
	}

	@Test
	public void findAResourceTest() throws Exception
	{
		systemService.checkAllResources();
		Resource resource = abacService.searchResourceByName("Contact");
		assertEquals("Contact", resource.getResourceName());
	}
}
