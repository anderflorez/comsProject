package com.unlimitedcompanies.coms.service.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.data.exceptions.DuplicatedResourcePolicyException;
import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.ComparisonOperator;
import com.unlimitedcompanies.coms.domain.abac.LogicOperator;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.domain.abac.ResourceField;
import com.unlimitedcompanies.coms.domain.abac.UserAttribute;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.ContactPhone;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.domain.security.exen.InvalidPhoneNumberException;
import com.unlimitedcompanies.coms.service.abac.SystemService;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.security.ABACService;
import com.unlimitedcompanies.coms.service.security.AuthService;
import com.unlimitedcompanies.coms.service.security.ContactService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
@Transactional
@ActiveProfiles("integrationTesting")
public class SecurityServiceIntegrationTest
{
	@Autowired
	ContactService contactService;

	@Autowired
	AuthService authService;

	@Autowired
	SystemService systemService;
	
	@Autowired
	ABACService abacService;
	
	@Test
	public void saveSimpleContactTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByNameWithFieldsAndPolicy("Contact");
		
		AbacPolicy abacPolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		abacPolicy.setCdPolicy(true, false);
		abacPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacPolicy, "administrator");
		
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		
		contactService.saveContact(contact, "administrator");
		
		assertEquals(2, contactService.findNumberOfContacts(),
				"Test for Saving a new contact from contact service failed");
	}
	
	@Test
	public void saveSimpleContactWithFieldRestrictedTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");
		Resource userResource = abacService.searchResourceByName("User");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");
		Resource abacPolicy = abacService.searchResourceByName("AbacPolicy");
		
		AbacPolicy contactUpdate = new AbacPolicy("ContactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdate.setCdPolicy(true, false);
		contactUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdate, "administrator");
		
		AbacPolicy contactRead = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactRead, "administrator");
		
		AbacPolicy userUpdatePolicy = new AbacPolicy("UserUpdate", PolicyType.UPDATE, userResource);
		userUpdatePolicy.setCdPolicy(true, false);
		userUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userUpdatePolicy, "administrator");
		
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
		
		AbacPolicy restrictionFieldRead = new AbacPolicy("RestrictedRead", PolicyType.READ, restrictedFieldResource);
		restrictionFieldRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(restrictionFieldRead, "administrator");
		
		Contact managerContact = new Contact("Manager", null, null);
		contactService.saveContact(managerContact, "administrator");
		managerContact = contactService.searchContactByCharId(managerContact.getContactCharId(), "administrator");
		User managerUser = new User("manager", "mypass".toCharArray(), managerContact);
		authService.saveUser(managerUser, "administrator");
		
		AbacPolicy abacRead = new AbacPolicy("PolicyRead", PolicyType.READ, abacPolicy);
		abacRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacRead, "administrator");
		
		contactRead = abacService.searchPolicy(contactResource, PolicyType.READ, "administrator");
		contactRead.setLogicOperator(LogicOperator.OR);
		contactRead.addEntityCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, "manager");
		abacService.savePolicy(contactRead, "administrator");
		
		ResourceField resourceField = contactResource.getResourceFieldByName("email");
		Role role = authService.searchRoleByNameWithRestrictedFields("Administrators", "administrator");
		abacService.addFieldRestriction(role.getRoleId(), resourceField.getResourceFieldId(), "administrator");
		
		Contact adminContact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		assertEquals("uec_ops_support@unlimitedcompanies.com", 
				contactService.searchContactByCharId(adminContact.getContactCharId(), "manager").getEmail());
		
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		contactService.saveContact(contact, "administrator");
		
		assertNull(contactService.searchContactByCharId(contact.getContactCharId(), "manager").getEmail());
		assertNull(contactService.searchContactByCharId(contact.getContactCharId(), "administrator").getEmail());
	}
	
	@Test
	public void noPermissionToSaveContactTest() throws Exception
	{
		systemService.initialSetup();
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		
		assertThrows(NoResourceAccessException.class, () -> contactService.saveContact(contact, "administrator"), 
				"Test for Saving a new contact without permission failed");
	}
	
	@Test
	public void saveContactWithRepeatedEmailNotAllowedTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		
		AbacPolicy abacPolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		abacPolicy.setCdPolicy(true, false);
		abacPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacPolicy, "administrator");
		
		Contact contact1 = new Contact("John", null, "Doe", "john@example.com");
		Contact contact2 = new Contact("Johnny", "J", "Roe", "john@example.com");
		
		contactService.saveContact(contact1, "administrator");
		assertThrows(DuplicateRecordException.class, () -> contactService.saveContact(contact2, "administrator"));
	}
	
	@Test
	public void numberOfContactsIntegrationTest() throws Exception
	{
		assertEquals(0, contactService.findNumberOfContacts(), "Number of contacts service test has failed");
	}
	
	@Test
	public void findAllContacts() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		
		AbacPolicy abacUpdatePolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		abacUpdatePolicy.setCdPolicy(true, false);
		abacUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacUpdatePolicy, "administrator");
		
		AbacPolicy abacReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		abacReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacReadPolicy, "administrator");
		
		contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"), "administrator");
		contactService.saveContact(new Contact("Diane", null, null, "Diane@example.com"), "administrator");
		contactService.saveContact(new Contact("Ella", null, null, "ella@example.com"), "administrator");
		contactService.saveContact(new Contact("Catherine", null, null, "catherine@example.com"), "administrator");
		contactService.saveContact(new Contact("Marcela", null, null, "marcela@example.com"), "administrator");
		
		assertEquals(6, contactService.searchAllContacts("administrator").size());
	}

	@Test
	public void findAllContactsWithFieldRestrictions() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");
		
		AbacPolicy abacUpdatePolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		abacUpdatePolicy.setCdPolicy(true, false);
		abacUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacUpdatePolicy, "administrator");
		
		AbacPolicy abacReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		abacReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacReadPolicy, "administrator");
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");
		
		AbacPolicy restrictionFieldUpdate = new AbacPolicy("RestrictedUpdate", PolicyType.UPDATE, restrictedFieldResource);
		restrictionFieldUpdate.setCdPolicy(true, false);
		restrictionFieldUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(restrictionFieldUpdate, "administrator");
		
		AbacPolicy restrictionFieldRead = new AbacPolicy("RestrictedRead", PolicyType.READ, restrictedFieldResource);
		restrictionFieldRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(restrictionFieldRead, "administrator");
		
		contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"), "administrator");
		contactService.saveContact(new Contact("Diane", null, null, "Diane@example.com"), "administrator");
		contactService.saveContact(new Contact("Ella", null, null, "ella@example.com"), "administrator");
		contactService.saveContact(new Contact("Catherine", null, null, "catherine@example.com"), "administrator");
		contactService.saveContact(new Contact("Marcela", null, null, "marcela@example.com"), "administrator");
		
		ResourceField resourceField = contactResource.getResourceFieldByName("email");
		Role role = authService.searchRoleByNameWithRestrictedFields("Administrators", "administrator");
		abacService.addFieldRestriction(role.getRoleId(), resourceField.getResourceFieldId(), "administrator");
		
		List<Contact> foundContacts = contactService.searchAllContacts("administrator");
		boolean foundEmail = false;
		for (Contact contact : foundContacts)
		{
			if (contact.getEmail() != null)
			{
				foundEmail = true;
			}
		}
		
		assertFalse(foundEmail);
	}
	
//	@Test
//	public void numberOfContactsInARangeTest() throws DuplicateRecordException
//	{
//		contactService.saveContact(new Contact("fernando", null, null, "fernando@example.com"));
//		contactService.saveContact(new Contact("Diane", null, null, "Diane@example.com"));
//		contactService.saveContact(new Contact("Bella", null, null, "bella@example.com"));
//		contactService.saveContact(new Contact("Ann", null, null, "ann@example.com"));
//		contactService.saveContact(new Contact("Ella", null, null, "ella@example.com"));
//		contactService.saveContact(new Contact("Catherine", null, null, "catherine@example.com"));
//		
//		assertTrue(contactService.hasNextContact(3, 2));
//		assertFalse(contactService.hasNextContact(4, 2));
//		assertFalse(contactService.hasNextContact(3, 3));
//	}
	
	@Test
	public void findAllContactsByRangeTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		
		AbacPolicy abacUpdatePolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		abacUpdatePolicy.setCdPolicy(true, false);
		abacUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacUpdatePolicy, "administrator");
		
		AbacPolicy abacReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		abacReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacReadPolicy, "administrator");
		
		Contact john = new Contact("John", null, "Doe");
		Contact jane = new Contact("Jane", null, "Doe");
		Contact diane = new Contact("Diane", null, null);
		Contact marcela = new Contact("Marcela", null, null);
		Contact catherine = new Contact("Catherine", null, null);
		Contact robert = new Contact("Robert", null, null);
		Contact joseph = new Contact("Joseph", null, null);
		
		contactService.saveContact(john, "administrator");
		contactService.saveContact(jane, "administrator");
		contactService.saveContact(diane, "administrator");
		contactService.saveContact(marcela, "administrator");
		contactService.saveContact(catherine, "administrator");
		contactService.saveContact(robert, "administrator");
		contactService.saveContact(joseph, "administrator");

		assertEquals(diane.getContactCharId(), contactService.searchContactsByRange(2, 2, "administrator").get(0).getContactCharId());
		assertEquals(marcela.getContactCharId(), contactService.searchContactsByRange(2, 4, "administrator").get(0).getContactCharId());
		assertEquals(joseph.getContactCharId(), contactService.searchContactsByRange(3, 2, "administrator").get(2).getContactCharId());
		assertEquals(2, contactService.searchContactsByRange(3, 3, "administrator").size());
	}
	
	@Test
	public void findAllContactsByRangeWithFieldRestrictionsTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");
		
		AbacPolicy abacUpdatePolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		abacUpdatePolicy.setCdPolicy(true, false);
		abacUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacUpdatePolicy, "administrator");
		
		AbacPolicy abacReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		abacReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacReadPolicy, "administrator");
		
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");
		
		AbacPolicy restrictionFieldUpdate = new AbacPolicy("RestrictedUpdate", PolicyType.UPDATE, restrictedFieldResource);
		restrictionFieldUpdate.setCdPolicy(true, false);
		restrictionFieldUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(restrictionFieldUpdate, "administrator");
		
		Contact john = new Contact("John", null, "Doe");
		Contact jane = new Contact("Jane", null, "Doe");
		Contact diane = new Contact("Diane", null, "A");
		Contact marcela = new Contact("Marcela", null, "B");
		Contact catherine = new Contact("Catherine", null, "C");
		Contact robert = new Contact("Robert", null, "D");
		Contact joseph = new Contact("Joseph", null, "E");
		
		contactService.saveContact(john, "administrator");
		contactService.saveContact(jane, "administrator");
		contactService.saveContact(diane, "administrator");
		contactService.saveContact(marcela, "administrator");
		contactService.saveContact(catherine, "administrator");
		contactService.saveContact(robert, "administrator");
		contactService.saveContact(joseph, "administrator");
		
		ResourceField resourceField = contactResource.getResourceFieldByName("lastName");
		Role role = authService.searchRoleByNameWithRestrictedFields("Administrators", "administrator");
		abacService.addFieldRestriction(role.getRoleId(), resourceField.getResourceFieldId(), "administrator");

		assertNull(contactService.searchContactsByRange(2, 2, "administrator").get(0).getLastName());
		assertNull(contactService.searchContactsByRange(2, 4, "administrator").get(0).getLastName());
		assertNull(contactService.searchContactsByRange(3, 2, "administrator").get(2).getLastName());
		assertEquals(2, contactService.searchContactsByRange(3, 3, "administrator").size());
	}
	
	
	@Test
	public void findContactByIdTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		
		AbacPolicy abacUpdatePolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		abacUpdatePolicy.setCdPolicy(true, false);
		abacUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacUpdatePolicy, "administrator");
		
		AbacPolicy abacReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		abacReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacReadPolicy, "administrator");
		
		Contact john = new Contact("John", null, "Doe", "john@example.com");
		
		contactService.saveContact(john, "administrator");
		Contact foundContact = contactService.searchContactByCharId(john.getContactCharId(), "administrator");
		
		assertEquals("John", contactService.searchContactById(foundContact.getContactId(), "administrator").getFirstName(), 
				"Service test to find contact by id failed");
	}
	
	@Test
	public void findContactByIdWithRestrictionTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");
		
		AbacPolicy abacUpdatePolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		abacUpdatePolicy.setCdPolicy(true, false);
		abacUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacUpdatePolicy, "administrator");
		
		AbacPolicy abacReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		abacReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacReadPolicy, "administrator");
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");
		
		AbacPolicy restrictionFieldUpdate = new AbacPolicy("RestrictedUpdate", PolicyType.UPDATE, restrictedFieldResource);
		restrictionFieldUpdate.setCdPolicy(true, false);
		restrictionFieldUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(restrictionFieldUpdate, "administrator");
		
		AbacPolicy restrictionFieldRead = new AbacPolicy("RestrictedRead", PolicyType.READ, restrictedFieldResource);
		restrictionFieldRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(restrictionFieldRead, "administrator");
		
		Contact john = new Contact("John", null, "Doe", "john@example.com");
		contactService.saveContact(john, "administrator");
		
		ResourceField resourceField = contactResource.getResourceFieldByName("lastName");
		Role role = authService.searchRoleByNameWithRestrictedFields("Administrators", "administrator");
		abacService.addFieldRestriction(role.getRoleId(), resourceField.getResourceFieldId(), "administrator");
		
		Contact foundContact = contactService.searchContactByCharId(john.getContactCharId(), "administrator");
		assertNull(contactService.searchContactById(foundContact.getContactId(), "administrator").getLastName());
	}
	
	@Test
	public void findContactByCharIdTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		
		AbacPolicy contactCreatePolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		contactCreatePolicy.setCdPolicy(true, false);
		contactCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactCreatePolicy, "administrator");
		
		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		Contact contact = new Contact("John", null, "Doe");
		contactService.saveContact(contact, "administrator");
		
		assertEquals(contact, contactService.searchContactByCharId(contact.getContactCharId(), "administrator"), 
				"Find contact by char id test failed");
	}
	
	@Test
	public void findContactByCharIdWithFieldRestrictionTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");
		
		AbacPolicy contactCreatePolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		contactCreatePolicy.setCdPolicy(true, false);
		contactCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactCreatePolicy, "administrator");
		
		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");
		
		AbacPolicy fieldRestrictPolicy = new AbacPolicy("RestrictedFieldUpdate", PolicyType.UPDATE, restrictedFieldResource);
		fieldRestrictPolicy.setCdPolicy(true, false);
		fieldRestrictPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(fieldRestrictPolicy, "administrator");
		
		Contact contact = new Contact("John", null, "Doe");
		contactService.saveContact(contact, "administrator");
		
		assertEquals("Doe", contactService.searchContactByCharId(contact.getContactCharId(), "administrator").getLastName());
		
		Role adminRole = authService.searchRoleByName("Administrators", "administrator");
		contactResource = abacService.searchResourceByNameWithFields("Contact");
		ResourceField restrictedField = contactResource.getResourceFieldByName("lastName");
		abacService.addFieldRestriction(adminRole.getRoleId(), restrictedField.getResourceFieldId(), "administrator");
		
		assertNull(contactService.searchContactByCharId(contact.getContactCharId(), "administrator").getLastName());
	}
	
	@Test
	public void findContactByEmailTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		
		AbacPolicy contactCreatePolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		contactCreatePolicy.setCdPolicy(true, false);
		contactCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactCreatePolicy, "administrator");
		
		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		String charId = contact.getContactCharId();
		contactService.saveContact(contact, "administrator");
		
		assertEquals(charId, contactService.searchContactByEmail("john@example.com", "administrator").getContactCharId(),
				"Service test  to find contact by email failed");
	}

	@Test
	public void findContactByEmailWithFieldRestrictionTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");
		
		AbacPolicy contactCreatePolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		contactCreatePolicy.setCdPolicy(true, false);
		contactCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactCreatePolicy, "administrator");
		
		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");
		
		AbacPolicy fieldRestrictPolicy = new AbacPolicy("RestrictedFieldUpdate", PolicyType.UPDATE, restrictedFieldResource);
		fieldRestrictPolicy.setCdPolicy(true, false);
		fieldRestrictPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(fieldRestrictPolicy, "administrator");
		
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		contactService.saveContact(contact, "administrator");
		
		Role adminRole = authService.searchRoleByName("Administrators", "administrator");
		contactResource = abacService.searchResourceByNameWithFields("Contact");
		ResourceField restrictedField = contactResource.getResourceFieldByName("lastName");
		abacService.addFieldRestriction(adminRole.getRoleId(), restrictedField.getResourceFieldId(), "administrator");

		assertNull(contactService.searchContactByEmail("john@example.com", "administrator").getLastName());
	}
	
	@Test
	public void updateContactTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		
		AbacPolicy contactCreatePolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		contactCreatePolicy.setCdPolicy(true, false);
		contactCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactCreatePolicy, "administrator");
		
		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		contactService.saveContact(contact, "administrator");
		
		contact.setFirstName("Jane");
		contact.setEmail("jane@example.com");
		contactService.updateContact(contact, "administrator");
		
		Contact updatedContact = contactService.searchContactById(contact.getContactId(), "administrator");
		assertEquals("Jane", updatedContact.getFirstName(), "Service test for updating contact failed");
	}

	@Test
	public void updateContactWithRestrictedFieldTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource userResource = abacService.searchResourceByName("User");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");
		Resource abacPolicy = abacService.searchResourceByName("AbacPolicy");
		
		AbacPolicy contactCreatePolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		contactCreatePolicy.setCdPolicy(true, false);
		contactCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactCreatePolicy, "administrator");
		
		AbacPolicy contactRead = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactRead, "administrator");		
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");
		
		AbacPolicy fieldRestrictPolicy = new AbacPolicy("RestrictedFieldUpdate", PolicyType.UPDATE, restrictedFieldResource);
		fieldRestrictPolicy.setCdPolicy(true, false);
		fieldRestrictPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(fieldRestrictPolicy, "administrator");
		
		// Create the new contact to be used for testing
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		contactService.saveContact(contact, "administrator");
		
		// Add another user with permission to read contacts
		AbacPolicy userUpdatePolicy = new AbacPolicy("UserUpdate", PolicyType.UPDATE, userResource);
		userUpdatePolicy.setCdPolicy(true, false);
		userUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userUpdatePolicy, "administrator");
		
		Contact managerContact = new Contact("Manager", null, null);
		contactService.saveContact(managerContact, "administrator");
		managerContact = contactService.searchContactByCharId(managerContact.getContactCharId(), "administrator");
		User managerUser = new User("manager", "mypass".toCharArray(), managerContact);
		authService.saveUser(managerUser, "administrator");
		
		AbacPolicy abacRead = new AbacPolicy("PolicyRead", PolicyType.READ, abacPolicy);
		abacRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacRead, "administrator");
		
		contactRead = abacService.searchPolicy(contactResource, PolicyType.READ, "administrator");
		contactRead.setLogicOperator(LogicOperator.OR);
		contactRead.addEntityCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, "manager");
		abacService.savePolicy(contactRead, "administrator");
		
		// Edit the contact
		contact.setFirstName("Jane");
		contact.setEmail("jane@example.com");
		
		// Add field restrictions for administrator user
		Role adminRole = authService.searchRoleByName("Administrators", "administrator");
		contactResource = abacService.searchResourceByNameWithFields("Contact");
		ResourceField restrictedField = contactResource.getResourceFieldByName("email");
		abacService.addFieldRestriction(adminRole.getRoleId(), restrictedField.getResourceFieldId(), "administrator");
		
		// The administrator user with field restrictions tries to store the updated contact
		contactService.updateContact(contact, "administrator");
		
		// The manager user makes sure the restricted field for the administrator was not changed after the update
		Contact updatedContact = contactService.searchContactById(contact.getContactId(), "manager");
		assertEquals("john@example.com", updatedContact.getEmail());
		
		// The administrator completes the testing by reading the updated contact
		updatedContact = contactService.searchContactById(contact.getContactId(), "administrator");
		assertEquals("Jane", updatedContact.getFirstName());
		assertNull(updatedContact.getEmail());
	}
	
	@Test
	public void noPersistentObjectUpdateTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		
		AbacPolicy contactCreatePolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		contactCreatePolicy.setCdPolicy(true, false);
		contactCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactCreatePolicy, "administrator");
		
		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		contactService.saveContact(contact, "administrator");
		
		contact.setFirstName("Jane");
		contact.setEmail("jane@example.com");
		
		Contact updatedContact = contactService.searchContactById(contact.getContactId(), "administrator");
		assertEquals("John", updatedContact.getFirstName(), "Service test for updating contact failed");
	}

	@Test
	public void deleteSingleContactTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		
		AbacPolicy contactCreatePolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		contactCreatePolicy.setCdPolicy(true, true);
		contactCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactCreatePolicy, "administrator");
		
		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		contactService.saveContact(contact, "administrator");
		
		assertEquals(2, contactService.findNumberOfContacts());
		contact = contactService.searchContactByCharId(contact.getContactCharId(), "administrator");

		contactService.deleteContact(contact, "administrator");
		assertEquals(1, contactService.findNumberOfContacts(), "Service test for deleting a single contact failed");
	}
		
	@Test
	public void saveContactWithAddressTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByNameWithFieldsAndPolicy("Contact");
		
		AbacPolicy contactUpdate = new AbacPolicy("contactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdate.setCdPolicy(true, false);
		contactUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdate, "administrator");
		
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		contact.setAddress("0000 MyStreet Dr", "MyCity", "FL", "00001");
		
		contactService.saveContact(contact, "administrator");
		
		assertEquals(1, contactService.findNumberOfContactAddresses());
	}
	
	@Test
	public void saveContactWithRestrictedAddressFieldTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");
		
		AbacPolicy contactCreatePolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		contactCreatePolicy.setCdPolicy(true, false);
		contactCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactCreatePolicy, "administrator");
		
		AbacPolicy contactRead = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactRead, "administrator");		
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");
		
		AbacPolicy fieldRestrictPolicy = new AbacPolicy("RestrictedFieldUpdate", PolicyType.UPDATE, restrictedFieldResource);
		fieldRestrictPolicy.setCdPolicy(true, false);
		fieldRestrictPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(fieldRestrictPolicy, "administrator");
		
		// Add field restrictions for administrator user
		Role adminRole = authService.searchRoleByName("Administrators", "administrator");
		contactResource = abacService.searchResourceByNameWithFields("Contact");
		ResourceField restrictedField = contactResource.getResourceFieldByName("contactAddress");
		abacService.addFieldRestriction(adminRole.getRoleId(), restrictedField.getResourceFieldId(), "administrator");
		
		// Create the new contact and save it
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		contact.setAddress("1122 NewStreet Ave", "NewCity", "FL", "11122");
		contactService.saveContact(contact, "administrator");

		assertEquals(0, contactService.findNumberOfContactAddresses());
		assertEquals(2, contactService.findNumberOfContacts());
	}
	
	@Test
	public void numberOfAddressesTest()
	{
		assertEquals(0, contactService.findNumberOfContactAddresses());
	}
		
	@Test
	public void findContactAddressTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		
		AbacPolicy contactUpdate = new AbacPolicy("contactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdate.setCdPolicy(true, false);
		contactUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdate, "administrator");
		
		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		Contact contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		contact.setAddress("0000 AdminStreet Dr", "MyCity", "FL", "00001");
		contactService.updateContact(contact, "administrator");
		
		assertEquals("0000 AdminStreet Dr", 
					 contactService.searchContactById(contact.getContactId(), "administrator").getAddress().getStreet());
	}

	@Test
	public void updateContactAddressTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByNameWithFieldsAndPolicy("Contact");
		
		AbacPolicy contactUpdate = new AbacPolicy("contactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdate.setCdPolicy(true, false);
		contactUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdate, "administrator");
		
		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		contact.setAddress("0000 MyStreet Dr", "MyCity", "FL", "00001");
		
		contactService.saveContact(contact, "administrator");
		
		contact = contactService.searchContactById(contact.getContactId(), "administrator");
		contact.getAddress().setStreet("1122 NextStreet Ave");
		contact.getAddress().setCity("NextCity");
		contact.getAddress().setZipCode("11122");
		contactService.updateContact(contact, "administrator");

		assertEquals("1122 NextStreet Ave", 
					 contactService.searchContactById(contact.getContactId(), "administrator").getAddress().getStreet());
	}
	
	@Test
	public void updateRestrictedContactAddressTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource userResource = abacService.searchResourceByName("User");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");
		Resource abacPolicy = abacService.searchResourceByName("AbacPolicy");
		
		AbacPolicy contactCreatePolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		contactCreatePolicy.setCdPolicy(true, false);
		contactCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactCreatePolicy, "administrator");
		
		AbacPolicy contactRead = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactRead, "administrator");		
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");
		
		AbacPolicy fieldRestrictPolicy = new AbacPolicy("RestrictedFieldUpdate", PolicyType.UPDATE, restrictedFieldResource);
		fieldRestrictPolicy.setCdPolicy(true, false);
		fieldRestrictPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(fieldRestrictPolicy, "administrator");
		
		// Create the new contact to be used for testing
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		contact.setAddress("0000 FirstStreet Dr", "InitialCity", "FL", "00111");
		contactService.saveContact(contact, "administrator");
		
		// Add another user with permission to read contacts
		AbacPolicy userUpdatePolicy = new AbacPolicy("UserUpdate", PolicyType.UPDATE, userResource);
		userUpdatePolicy.setCdPolicy(true, false);
		userUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userUpdatePolicy, "administrator");
		
		Contact managerContact = new Contact("Manager", null, null);
		contactService.saveContact(managerContact, "administrator");
		managerContact = contactService.searchContactByCharId(managerContact.getContactCharId(), "administrator");
		User managerUser = new User("manager", "mypass".toCharArray(), managerContact);
		authService.saveUser(managerUser, "administrator");
		
		AbacPolicy abacRead = new AbacPolicy("PolicyRead", PolicyType.READ, abacPolicy);
		abacRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacRead, "administrator");
		
		contactRead = abacService.searchPolicy(contactResource, PolicyType.READ, "administrator");
		contactRead.setLogicOperator(LogicOperator.OR);
		contactRead.addEntityCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, "manager");
		abacService.savePolicy(contactRead, "administrator");
		
		// Edit the contact
		contact.setFirstName("Jane");
		contact.setEmail("jane@example.com");
		contact.setAddress("1122 NewStreet Ave", "NewCity", "FL", "11122");
		
		// Add field restrictions for administrator user
		Role adminRole = authService.searchRoleByName("Administrators", "administrator");
		contactResource = abacService.searchResourceByNameWithFields("Contact");
		ResourceField restrictedField = contactResource.getResourceFieldByName("contactAddress");
		abacService.addFieldRestriction(adminRole.getRoleId(), restrictedField.getResourceFieldId(), "administrator");
		
		// The administrator user with field restrictions tries to store the updated contact
		contactService.updateContact(contact, "administrator");

		// The manager user makes sure the restricted field for the administrator was not changed after the update
		Contact updatedContact = contactService.searchContactById(contact.getContactId(), "manager");
		assertEquals("0000 FirstStreet Dr", updatedContact.getAddress().getStreet());
		
		// The administrator completes the testing by reading the updated contact
		updatedContact = contactService.searchContactById(contact.getContactId(), "administrator");
		assertEquals("Jane", updatedContact.getFirstName());
		assertNull(updatedContact.getAddress());
	}
	
	@Test
	public void deleteContactAddressTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByNameWithFieldsAndPolicy("Contact");
		
		AbacPolicy contactUpdate = new AbacPolicy("contactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdate.setCdPolicy(true, false);
		contactUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdate, "administrator");
		
		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		Contact contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		contact.setAddress("1122 NewStreet Ave", "NewCity", "FL", "11122");
		contactService.updateContact(contact, "administrator");
		
		assertEquals(1, contactService.findNumberOfContactAddresses());
		
		contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		contactService.removeAddress(contact, "administrator");
		assertEquals(0, contactService.findNumberOfContactAddresses());
	}
	
	@Test
	public void saveContactWithPhoneNumberTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByNameWithFieldsAndPolicy("Contact");
		
		AbacPolicy contactUpdate = new AbacPolicy("contactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdate.setCdPolicy(true, false);
		contactUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdate, "administrator");
		
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		contact.addPhone("9541112223344", null, "Office");
		
		contactService.saveContact(contact, "administrator");
		
		assertEquals(1, contactService.findNumberOfContactPhones());
	}
	
	@Test
	public void saveContactWithRestrictedPhoneFieldTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");
		
		AbacPolicy contactCreatePolicy = new AbacPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		contactCreatePolicy.setCdPolicy(true, false);
		contactCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactCreatePolicy, "administrator");
		
		AbacPolicy contactRead = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactRead, "administrator");		
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");
		
		AbacPolicy fieldRestrictPolicy = new AbacPolicy("RestrictedFieldUpdate", PolicyType.UPDATE, restrictedFieldResource);
		fieldRestrictPolicy.setCdPolicy(true, false);
		fieldRestrictPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(fieldRestrictPolicy, "administrator");
		
		// Add field restrictions for administrator user
		Role adminRole = authService.searchRoleByName("Administrators", "administrator");
		contactResource = abacService.searchResourceByNameWithFields("Contact");
		ResourceField restrictedField = contactResource.getResourceFieldByName("contactPhones");
		abacService.addFieldRestriction(adminRole.getRoleId(), restrictedField.getResourceFieldId(), "administrator");
		
		// Create the new contact and save it
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		contact.addPhone("9541112223344", null, "Office");
		contactService.saveContact(contact, "administrator");
		
		assertEquals(2, contactService.findNumberOfContacts());
		assertEquals(0, contactService.findNumberOfContactPhones());
	}
	
	 @Test
	 public void numberOfPhoneNumbersTest()
	 {
		 assertEquals(0, contactService.findNumberOfContactPhones());
	 }
	

	//
	// @Test
	// public void findContactPhonesByNumberTest() throws
	// InvalidPhoneNumberException
	// {
	// contactService.saveContact(new Contact("John", null, "Doe",
	// "johnd@example.com"));
	// Contact contact = contactService.findContactByEmail("johnd@example.com");
	// contactService.saveContactPhone(new ContactPhone("7775554433", null, null,
	// contact));
	// contactService.saveContactPhone(new ContactPhone("7775554433", null, null,
	// contact));
	// contactService.saveContactPhone(new ContactPhone("7775554413", null, null,
	// contact));
	// contactService.saveContactPhone(new ContactPhone("7775554433", null, null,
	// contact));
	//
	// List<ContactPhone> phones = contactService.findContactPhoneByNumber("7775554433");
	//
	// assertEquals(3, phones.size(), "Service test for finding phones by numbers");
	// }
	//
	// @Test
	// public void findContactPhoneByIdTest() throws InvalidPhoneNumberException
	// {
	// contactService.saveContact(new Contact("John", null, "Doe",
	// "johnd@example.com"));
	// Contact contact = contactService.findContactByEmail("johnd@example.com");
	// contactService.saveContactPhone(new ContactPhone("7775554433", null, null,
	// contact));
	//
	// ContactPhone initialPhone = null;
	// List<ContactPhone> phones = contactService.findContactPhoneByNumber("7775554433");
	// for (ContactPhone next : phones)
	// {
	// initialPhone = next;
	// }
	//
	// ContactPhone foundPhone =
	// contactService.findContactPhoneById(initialPhone.getPhoneId());
	// assertEquals(initialPhone, foundPhone, "Service test to find contact phone by
	// id failed");
	// }
	//
	
	// TODO: Create a test for finding a full contact with address and phone numbers

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test
	public void saveNewUserTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource userResource = abacService.searchResourceByName("User");
		
		AbacPolicy contactUpdatePolicy = new AbacPolicy("ContactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdatePolicy.setCdPolicy(true, false);
		contactUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdatePolicy, "administrator");
		
		AbacPolicy contactReadPolicy = new AbacPolicy("contactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		AbacPolicy userUpdatePolicy = new AbacPolicy("UserUpdate", PolicyType.UPDATE, userResource);
		userUpdatePolicy.setCdPolicy(true, false);
		userUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userUpdatePolicy, "administrator");
		
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"), "administrator");
		Contact contact = contactService.searchContactByEmail("johnd@example.com", "administrator");
		authService.saveUser(new User("username", "mypass".toCharArray(), contact), "administrator");
		
		assertEquals(2, authService.searchNumberOfUsers(), "Service test to save a new user failed");
	}
	
	@Test
	public void userWithDuplicatedUsernameNotAllowedTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource userResource = abacService.searchResourceByName("User");
		
		AbacPolicy contactUpdatePolicy = new AbacPolicy("ContactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdatePolicy.setCdPolicy(true, false);
		contactUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdatePolicy, "administrator");
		
		AbacPolicy contactReadPolicy = new AbacPolicy("contactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		AbacPolicy userUpdatePolicy = new AbacPolicy("UserUpdate", PolicyType.UPDATE, userResource);
		userUpdatePolicy.setCdPolicy(true, false);
		userUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userUpdatePolicy, "administrator");
		
		Contact initContact = new Contact("John", null, "Doe", "johnd@example.com");
		contactService.saveContact(initContact, "administrator");
		initContact = contactService.searchContactByCharId(initContact.getContactCharId(), "administrator");
		
		Contact dupContact = new Contact("Johnny", null, "Doe");
		contactService.saveContact(dupContact, "administrator");
		dupContact = contactService.searchContactByCharId(dupContact.getContactCharId(), "administrator");
		
		authService.saveUser(new User("username", "mypass".toCharArray(), initContact), "administrator");
		
		User user = new User("username", "mypass".toCharArray(), dupContact);
		assertThrows(DuplicateRecordException.class, () -> authService.saveUser(user, "administrator"), 
				"Service test to make sure a user with duplicated username is not allowed failed");
	}

	@Test
	public void getNumberOfUsersTest()
	{
		assertEquals(0, authService.searchNumberOfUsers(), "Service test to find the number of users failed");
	}
//	
//	@Test
//	public void findAllUsersTest() throws Exception
//	{
//		systemService.initialSetup();
//		Resource contactResource = abacService.findResourceByName("Contact");
//		Resource userResource = abacService.findResourceByName("User");
//		
//		AbacPolicy contactUpdatePolicy = new AbacPolicy("ContactUpdate", PolicyType.UPDATE, contactResource);
//		contactUpdatePolicy.setCdPolicy(true, false);
//		contactUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
//		abacService.savePolicy(contactUpdatePolicy, "administrator");
//		
//		AbacPolicy contactReadPolicy = new AbacPolicy("contactRead", PolicyType.READ, contactResource);
//		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
//		abacService.savePolicy(contactReadPolicy, "administrator");
//		
//		AbacPolicy userUpdatePolicy = new AbacPolicy("UserUpdate", PolicyType.UPDATE, userResource);
//		userUpdatePolicy.setCdPolicy(true, false);
//		userUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
//		abacService.savePolicy(userUpdatePolicy, "administrator");
//		
//		Contact firstContact = new Contact("John", null, "Doe", "johnd@example.com");
//		contactService.saveContact(firstContact, "administrator");
//		firstContact = contactService.searchContactByCharId(firstContact.getContactCharId(), "administrator");
//		authService.saveUser(new User("firstUser", "pass".toCharArray(), firstContact), "administrator");
//		
//		Contact secondContact = new Contact("Jane", null, "Doe", "janed@example.com");
//		contactService.saveContact(secondContact, "administrator");
//		secondContact = contactService.searchContactByCharId(secondContact.getContactCharId(), "administrator");
//		authService.saveUser(new User("secondUser", "pass".toCharArray(), secondContact), "administrator");
//		
//		Contact thirdContact = new Contact("Richard", null, "Roe", "richardr@example.com");
//		contactService.saveContact(thirdContact, "administrator");
//		thirdContact = contactService.searchContactByCharId(thirdContact.getContactCharId(), "administrator");
//		authService.saveUser(new User("thirdUser", "pass".toCharArray(), thirdContact), "administrator");
//		
//		assertEquals(3, authService.searchAllUsers("administrator").size(), "Find all users integration test failed");
//	}
//	
//	@Test
//	public void findUsersByPagesTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
//	{
//		
//		Contact fernando = contactService.saveContact(new Contact("fernando", null, null, "fernando@example.com"));
//		Contact diane = contactService.saveContact(new Contact("Diane", null, null, "Diane@example.com"));
//		Contact bella = contactService.saveContact(new Contact("Bella", null, null, "bella@example.com"));
//		Contact ann = contactService.saveContact(new Contact("Ann", null, null, "ann@example.com"));
//		Contact ella = contactService.saveContact(new Contact("Ella", null, null, "ella@example.com"));
//		Contact catherine = contactService.saveContact(new Contact("Catherine", null, null, "catherine@example.com"));
//		Contact marcela = contactService.saveContact(new Contact("marcela", null, null, "marcela@example.com"));
//		
//		authService.saveUser(new User("username1", "mypass".toCharArray(), fernando));
//		authService.saveUser(new User("username2", "mypass".toCharArray(), diane));
//		User user3 = authService.saveUser(new User("username3", "mypass".toCharArray(), bella));
//		User user4 = authService.saveUser(new User("username4", "mypass".toCharArray(), ann));
//		authService.saveUser(new User("username5", "mypass".toCharArray(), ella));
//		authService.saveUser(new User("username6", "mypass".toCharArray(), catherine));
//		User user7 = authService.saveUser(new User("username7", "mypass".toCharArray(), marcela));
//
//		assertEquals(user4.getUsername(), authService.searchUsersByRange(2, 3).get(0).getUsername());
//		assertEquals(user3.getUsername(), authService.searchUsersByRange(1, 3).get(2).getUsername());
//		assertEquals(user7.getUsername(), authService.searchUsersByRange(4, 2).get(0).getUsername());
//	}
	
	@Test
	public void findUserByUserId() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource userResource = abacService.searchResourceByName("User");
		
		AbacPolicy contactUpdatePolicy = new AbacPolicy("ContactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdatePolicy.setCdPolicy(true, false);
		contactUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdatePolicy, "administrator");
		
		AbacPolicy contactReadPolicy = new AbacPolicy("contactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		AbacPolicy userUpdatePolicy = new AbacPolicy("UserUpdate", PolicyType.UPDATE, userResource);
		userUpdatePolicy.setCdPolicy(true, false);
		userUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userUpdatePolicy, "administrator");
		
		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "Administrator");
		
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"), "administrator");
		Contact contact = contactService.searchContactByEmail("johnd@example.com", "administrator");
		authService.saveUser(new User("username", "mypass".toCharArray(), contact), "administrator");
		User user = authService.searchUserByUsername("username", "administrator");

		assertEquals(user, authService.searchUserById(user.getUserId(), "administrator"), 
				"Service test for finding user by userId failed");
	}
	
	@Test
	public void findUserByUsernameTest() throws Exception 
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource userResource = abacService.searchResourceByName("User");
		
		AbacPolicy contactUpdatePolicy = new AbacPolicy("ContactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdatePolicy.setCdPolicy(true, false);
		contactUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdatePolicy, "administrator");
		
		AbacPolicy contactReadPolicy = new AbacPolicy("contactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		AbacPolicy userUpdatePolicy = new AbacPolicy("UserUpdate", PolicyType.UPDATE, userResource);
		userUpdatePolicy.setCdPolicy(true, false);
		userUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userUpdatePolicy, "administrator");
		
		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "administrator");
		
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"), "administrator");
		Contact contact = contactService.searchContactByEmail("johnd@example.com", "administrator");
		authService.saveUser(new User("username", "mypass".toCharArray(), contact), "administrator");
		
		assertTrue(authService.searchUserByUsername("username", "administrator").isEnabled(), 
				"Service test for finding user by username with contact failed");
	}
//	
//	@Test
//	public void findUserByContact() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
//	{
//		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));
//		
//		User foundUser = authService.searchUserByContact(contact);
//		assertEquals(user.getUserId(), foundUser.getUserId(), "Service test for finding user by contact failed");
//	}
//	
//	@Test 
//	public void findUserWithContactTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
//	{
//		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));
//
//		assertEquals(contact, authService.searchUserByUsernameWithContact(user.getUsername()).getContact(),
//				"Integration service test to find a user with contact failed");
//		User foundUser = authService.searchUserByUserIdWithContact(user.getUserId());
//		assertEquals(contact, foundUser.getContact(),
//				"Integration service test to find a user with contact failed");
//	}
//	
//	@Test
//	public void findUserRolesTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
//	{
//		Role role1 = authService.saveRole(new Role("Administrator"));
//		Role role2 = authService.saveRole(new Role("Manager"));
//		
//		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		User user = authService.saveUser(new User("username", "mypass".toCharArray(), contact));
//		
//		authService.assignUserToRole(user.getUserId(), role1.getRoleId());
//		authService.assignUserToRole(user.getUserId(), role2.getRoleId());
//		
//		User foundUser = authService.searchFullUserByUsername(user.getUsername());
//		assertEquals(2, foundUser.getRoles().size(), "Service test for finding user role list failed");
//	}
//	
//	@Test
//	public void updateUsernameTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
//	{
//		// TODO: check this test as it might not be accurate - compare with the role updating tests
//		
//		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));
//		user.setUsername("john.doe");
//		User updatedUser = authService.updateUser(user);		
//		assertEquals("john.doe", updatedUser.getUsername(), "Service test for updating user username failed");
//	}
//	
//	@Test
//	public void successfullUserPasswordChangeTest() throws DuplicateRecordException, 
//														   RecordNotFoundException, 
//														   RecordNotCreatedException, 
//														   IncorrectPasswordException, 
//														   RecordNotChangedException
//	{
//		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));
//		String oldpass = String.valueOf(user.getPassword());
//		
//		authService.changeUserPassword(user.getUserId(), "mypass".toCharArray(), "newPass".toCharArray());
//		User foundUser = authService.searchUserByUserId(user.getUserId());
//		String newpass = String.valueOf(foundUser.getPassword());
//		
//		assertNotEquals(oldpass, newpass);
//		assertTrue(authService.passwordMatch(user.getUserId(), "newPass".toCharArray()));
//	}
//	
//	@Test
//	public void userPasswordChangeWithIncorrectPasswordTest() throws DuplicateRecordException, 
//														   RecordNotFoundException, 
//														   RecordNotCreatedException, 
//														   IncorrectPasswordException, 
//														   RecordNotChangedException
//	{
//		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));
//		
//		assertThrows(IncorrectPasswordException.class, 
//					 () -> authService.changeUserPassword(user.getUserId(), "incorrectPassword".toCharArray(), "newPass".toCharArray()));		
//	}
//	
//	@Test
//	public void userPasswordChangeWithNoChanngeTest() throws DuplicateRecordException, 
//														   RecordNotFoundException, 
//														   RecordNotCreatedException, 
//														   IncorrectPasswordException, 
//														   RecordNotChangedException
//	{
//		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));
//		
//		assertThrows(RecordNotChangedException.class, 
//					 () -> authService.changeUserPassword(user.getUserId(), "mypass".toCharArray(), "mypass".toCharArray()));		
//	}
//	
//	@Test
//	public void matchingPasswordTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
//	{
//		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));
//		
//		assertTrue(authService.passwordMatch(user.getUserId(), "mypass".toCharArray()));
//	}
//	
//	@Test
//	public void incorrectPasswordTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
//	{
//		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));
//		
//		assertFalse(authService.passwordMatch(user.getUserId(), "incorrectpassword".toCharArray()));
//	}
//	
//	// TODO: Test for encrypted password both when creating a new user and when updating the password
//	@Test
//	public void successfullEncryptedNewUserPasswordTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
//	{
//		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));
//		
//		assertNotEquals(Arrays.toString("mypass".toCharArray()), Arrays.toString(user.getPassword()));
//		assertTrue(authService.passwordMatch(user.getUserId(), "mypass".toCharArray()));
//	}
//	
//	@Test
//	public void successfullEncryptedChangedUserPasswordTest() throws DuplicateRecordException, 
//																	 RecordNotFoundException, 
//																	 RecordNotCreatedException, 
//																	 IncorrectPasswordException, 
//																	 RecordNotChangedException
//	{
//		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));
//		
//		authService.changeUserPassword(user.getUserId(), "mypass".toCharArray(), "newpassword".toCharArray());
//		
//		assertTrue(authService.passwordMatch(user.getUserId(), "newpassword".toCharArray()));
//	}
//	
//	@Test
//	public void updateUserStatus() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact = contactService.searchContactByEmail("johnd@example.com");
//		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));
//		user.setEnabled(false);
//		
//		User updatedUser = authService.updateUser(user);
//		assertFalse(updatedUser.isEnabled(), "Service test for updating user status failed");
//	}
//	
//	 @Test
//	 public void deleteSingleUserTest() throws DuplicateRecordException, RecordNotFoundException, 
//			 								   RecordNotCreatedException, RecordNotDeletedException
//	 {
//		 Contact contact1 = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		 Contact contact2 = contactService.saveContact(new Contact("Jane", null, "Doe", "janed@example.com"));
//		 
//		 User user = authService.saveUser(new User("johnd", "mypass".toCharArray(), contact1));
//		 authService.saveUser(new User("janed", "mypass".toCharArray(), contact2));
//		
//		 authService.deleteUser(user.getUserId());
//		
//		 // TODO: Change the assert statement to an assert that gets an exception
//		 assertEquals(1, authService.searchNumberOfUsers(), "Service test for deleting a single user failed");
//	 }

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test
	public void getNumberOfRolesTest()
	{
		assertEquals(0, authService.searchNumberOfRoles(), "Service test for finding number of roles failed");
	}

	@Test
	public void saveNewRoleTest() throws Exception
	{
		systemService.initialSetup();
		Resource roleResource = abacService.searchResourceByName("Role");
		
		AbacPolicy roleUpdate = new AbacPolicy("RoleUpdate", PolicyType.UPDATE, roleResource);
		roleUpdate.setCdPolicy(true, false);
		roleUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleUpdate, "administrator");
		
		authService.saveRole(new Role("Manager"), "administrator");
		
		assertEquals(2, authService.searchNumberOfRoles(), "Service test for saving new role failed");
	}
//	
//	@Test
//	public void findAllRolesTest() throws RecordNotCreatedException
//	{
//		authService.saveRole(new Role("Administrator"));
//		authService.saveRole(new Role("Manager"));
//		authService.saveRole(new Role("Engineer"));
//		
//		assertEquals(3, authService.searchNumberOfRoles(), "Service test for finding all roles failed");
//	}
//	
//	@Test
//	public void findRolesByRangeTest() throws RecordNotCreatedException
//	{
//		authService.saveRole(new Role("Administrator"));		//2
//		authService.saveRole(new Role("Manager"));				//5
//		authService.saveRole(new Role("Engineer"));				//4
//		authService.saveRole(new Role("Accountant"));			//1
//		authService.saveRole(new Role("Receptionist"));			//6
//		authService.saveRole(new Role("Architect"));			//3
//		
//		assertEquals("Architect", authService.searchRolesByRange(2, 2).get(0).getRoleName());
//		assertEquals("Receptionist", authService.searchRolesByRange(3, 2).get(1).getRoleName());
//		assertEquals("Administrator", authService.searchRolesByRange(1, 3).get(1).getRoleName());
//	}
	
	@Test
	public void findRoleByRoleIdTest() throws Exception
	{
		systemService.initialSetup();
		Resource roleResource = abacService.searchResourceByName("Role");
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");
		
		Role adminRole = authService.searchRoleByName("Administrators", "administrator");
		
		assertEquals("Administrators", authService.searchRoleById(adminRole.getRoleId(), "administrator").getRoleName(), 
				"Service test for finding role by roleId failed");
	}
	
	@Test
	public void findRoleByRoleNameTest() throws Exception
	{
		systemService.initialSetup();
		Resource roleResource = abacService.searchResourceByName("Role");
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");
		
		Role adminRole = authService.searchRoleByName("Administrators", "administrator");
		
		assertNotNull(adminRole.getRoleId(), "Service test for finding role by roleName failed");
	}
//	
//	@Test
//	public void findRoleByIdWithMembers() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
//	{
//		Role role = authService.saveRole(new Role("Administrator"));
//		Contact contact1 = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact2 = contactService.saveContact(new Contact("Jane", null, "Doe", "janed@example.com"));
//		Contact contact3 = contactService.saveContact(new Contact("Richard", null, "Roe", "richd@example.com"));
//		
//		User user1 = authService.saveUser(new User("johnd", "pass".toCharArray(), contact1));
//		User user2 = authService.saveUser(new User("janed", "pass".toCharArray(), contact2));
//		User user3 = authService.saveUser(new User("richd", "pass".toCharArray(), contact3));
//		
//		authService.assignUserToRole(user1.getUserId(), role.getRoleId());
//		authService.assignUserToRole(user2.getUserId(), role.getRoleId());
//		authService.assignUserToRole(user3.getUserId(), role.getRoleId());
//		
//		Role foundRole = authService.searchRoleByIdWithMembers(role.getRoleId());
//		assertEquals(3, foundRole.getMembers().size(), "Service test for finding a role with its members failed");
//	}
//	
//	@Test
//	public void searchRoleNonMembersTest() throws RecordNotCreatedException, DuplicateRecordException, RecordNotFoundException
//	{
//		Role role = authService.saveRole(new Role("Administrator"));
//		Contact contact1 = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact2 = contactService.saveContact(new Contact("Jane", null, "Doe", "janed@example.com"));
//		Contact contact3 = contactService.saveContact(new Contact("Richard", null, "Roe", "richd@example.com"));
//		
//		User user1 = authService.saveUser(new User("johnd", "pass".toCharArray(), contact1));
//		authService.saveUser(new User("janed", "pass".toCharArray(), contact2));
//		authService.saveUser(new User("richd", "pass".toCharArray(), contact3));
//		
//		authService.assignUserToRole(user1.getUserId(), role.getRoleId());
//		
//		List<User> foundUsers = authService.searchRoleNonMembers(role.getRoleId(), "Doe");
//		assertEquals(1, foundUsers.size(), "Service test for search role non members failed");
//		assertEquals("Jane", foundUsers.get(0).getContact().getFirstName(), "Service test for search role non members failed");
//		
//		foundUsers = authService.searchRoleNonMembers(role.getRoleId(), "OE");
//		assertEquals(2, foundUsers.size(), "Service test for search role non members failed");
//		assertEquals("Richard", foundUsers.get(1).getContact().getFirstName(), "Service test for search role non members failed");
//	}

	@Test
	 public void updateRoleTest() throws Exception
	 {
		systemService.initialSetup();
		Resource roleResource = abacService.searchResourceByName("Role");
		
		AbacPolicy roleUpdate = new AbacPolicy("RoleUpdate", PolicyType.UPDATE, roleResource);
		roleUpdate.setCdPolicy(true, false);
		roleUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleUpdate, "administrator");
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");
		
		authService.saveRole(new Role("Manager"), "administrator");
		Role role = authService.searchRoleByName("Manager", "administrator");
		int roleId = role.getRoleId();

		role.setRoleName("Coordinator");
		authService.updateRole(role, "administrator");
		
		assertEquals(roleId, authService.searchRoleByName("Coordinator", "administrator").getRoleId(), 
				"Service test for updating role has failed");
	 }
//	
//	@Test
//	 public void updateFailureRoleTest() throws RecordNotCreatedException
//	 {
//		Role role = authService.saveRole(new Role("Administrator"));
//		Role newrole = new Role("Administrator");
//		newrole.setRoleId(role.getRoleId());
//
//		assertThrows(RecordNotChangedException.class, () -> authService.updateRole(newrole),
//				"Service test for updating role faiure has failed");
//	 }
//	
//	@Test
//	public void deleteRoleTest() throws RecordNotCreatedException, RecordNotFoundException, RecordNotDeletedException
//	{
//		authService.saveRole(new Role("Administrators"));
//		Role role = authService.saveRole(new Role("Managers"));
//		authService.saveRole(new Role("Engineers"));
//		
//		authService.deleteRole(role.getRoleId());
//		assertThrows(RecordNotFoundException.class, () -> authService.searchRoleById(role.getRoleId()));
//	}
//
//	@Test
//	public void assignUserToRoleTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
//	{
//		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));
//		Role role = authService.saveRole(new Role("Administrator"));
//
//		authService.assignUserToRole(user.getUserId(), role.getRoleId());
//		
//		User checkUser = authService.searchFullUserByUserId(user.getUserId());
//		Role checkRole = authService.searchRoleById(role.getRoleId());
//
//		assertTrue(checkUser.getRoles().contains(role), "Service test for assigning a user to a role failed");
//		assertTrue(checkRole.getMembers().contains(user), "Service test for assigning a user to a role failed");
//	}
//
//	@Test
//	public void removeUserFromRole() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
//	{
//		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));
//		Role role = authService.saveRole(new Role("Administrator"));
//
//		authService.assignUserToRole(user.getUserId(), role.getRoleId());
//		authService.removeRoleMember(user.getUserId(), role.getRoleId());
//		
//		assertFalse(authService.searchRoleByIdWithMembers(role.getRoleId()).getMembers().contains(user), "Service test for removing a user from a role failed");
//		// TODO: this should also test if the role was removed from the user;
//	}
//
//	@Test
//	public void findUserWithContactAndRoles() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact = contactService.searchContactByEmail("johnd@example.com");
//		User user = new User("jdoe", "mypass".toCharArray(), contact);
//		authService.saveUser(user);
//		Role role1 = authService.saveRole(new Role("Administrator"));
//		Role role2 = authService.saveRole(new Role("Manager"));
//
//		User founduser = authService.searchFullUserByUsername("jdoe");
//		founduser.addRole(role1);
//		founduser.addRole(role2);
//
//		User foundUser = authService.searchFullUserByUsername("jdoe");
//
//		assertEquals(user, founduser, "Service test for finding user by username failed");
//		assertEquals(user.getContact(), contact, "Service test for finding user by username with contact failed");
//		assertEquals(2, foundUser.getRoles().size(), "Service test for finding user by username with contact failed");
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//
//	@Test
//	public void checkResourcesTest()
//	{
//		systemService.checkAllResources();
//		List<String> resources = systemService.findAllResourceNames();
//		List<ResourceField> resourceFields = systemService.findAllResourceFieldsWithResources();
//		Set<String> resourceFromFields = new HashSet<>();
//		for (ResourceField rf : resourceFields)
//		{
//			resourceFromFields.add(rf.getResource().getResourceName());
//		}
//		assertTrue(resources.size() > 0);
//		assertEquals(resources.size(), resourceFromFields.size(), "Checking resources integration test failed");
//	}
//
//	@Test
//	public void findAResourceTest()
//	{
//		systemService.checkAllResources();
//		Resource resource = systemService.findResourceByName("Contact");
//		assertEquals("Contact", resource.getResourceName(), "Find resource integration test failed");
//	}
}