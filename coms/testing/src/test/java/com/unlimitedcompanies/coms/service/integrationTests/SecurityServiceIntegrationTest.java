package com.unlimitedcompanies.coms.service.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import com.unlimitedcompanies.coms.domain.abac.ResourceField;
import com.unlimitedcompanies.coms.domain.abac.UserAttribute;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.ContactPhone;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
import com.unlimitedcompanies.coms.service.exceptions.IncorrectPasswordException;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.ABACService;
import com.unlimitedcompanies.coms.service.security.AuthService;
import com.unlimitedcompanies.coms.service.security.ContactService;
import com.unlimitedcompanies.coms.service.system.SystemService;

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
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");

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
		User managerUser = new User("manager", "mypass", managerContact);
		authService.saveUser(managerUser, "administrator");

		AbacPolicy abacRead = new AbacPolicy("PolicyRead", PolicyType.READ, abacPolicy);
		abacRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacRead, "administrator");

		contactRead = abacService.searchModifiablePolicy(contactResource, PolicyType.READ, "administrator");
		contactRead.setLogicOperator(LogicOperator.OR);
		contactRead.addEntityCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, "manager");

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
	public void findContactByContactPhoneIdTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");

		AbacPolicy contactUpdate = new AbacPolicy("contactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdate.setCdPolicy(true, false);
		contactUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdate, "administrator");

		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");

		// Add the new contact with its address for testing
		Contact contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		contact.addPhone("9542223344", null, "Office");
		contactService.updateContact(contact, "administrator");
		contact = contactService.searchContactByCharId(contact.getContactCharId(), "administrator");

		assertEquals("Administrator", 
				contactService.searchContactByPhoneId(contact.getPhones().get(0).getPhoneId(), "administrator").getFirstName());
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
		User managerUser = new User("manager", "mypass", managerContact);
		authService.saveUser(managerUser, "administrator");

		AbacPolicy abacRead = new AbacPolicy("PolicyRead", PolicyType.READ, abacPolicy);
		abacRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacRead, "administrator");

		contactReadPolicy = abacService.searchModifiablePolicy(contactResource, PolicyType.READ, "administrator");		
		contactReadPolicy.setLogicOperator(LogicOperator.OR);
		contactReadPolicy.addEntityCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, "manager");
		
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
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");

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
	public void findContactWithRestrictedAddressTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");

		AbacPolicy contactUpdate = new AbacPolicy("contactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdate.setCdPolicy(true, false);
		contactUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdate, "administrator");

		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");

		// Add the new contact with its address for testing
		Contact contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		contact.setAddress("0000 AdminStreet Dr", "MyCity", "FL", "00001");
		contactService.updateContact(contact, "administrator");

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

		assertNull(contactService.searchContactById(contact.getContactId(), "administrator").getAddress());
	}

	@Test
	public void updateContactAddressTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");

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
		User managerUser = new User("manager", "mypass", managerContact);
		authService.saveUser(managerUser, "administrator");

		AbacPolicy abacRead = new AbacPolicy("PolicyRead", PolicyType.READ, abacPolicy);
		abacRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacRead, "administrator");

		contactRead = abacService.searchModifiablePolicy(contactResource, PolicyType.READ, "administrator");
		contactRead.setLogicOperator(LogicOperator.OR);
		contactRead.addEntityCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, "manager");

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
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");

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
	public void deleteRestrictedContactAddressTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");

		AbacPolicy contactUpdate = new AbacPolicy("contactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdate.setCdPolicy(true, false);
		contactUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdate, "administrator");

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

		Contact contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		contact.setAddress("1122 NewStreet Ave", "NewCity", "FL", "11122");
		contactService.updateContact(contact, "administrator");

		assertEquals(1, contactService.findNumberOfContactAddresses());

		// Add field restrictions for administrator user
		Role adminRole = authService.searchRoleByName("Administrators", "administrator");
		ResourceField restrictedField = contactResource.getResourceFieldByName("contactAddress");
		abacService.addFieldRestriction(adminRole.getRoleId(), restrictedField.getResourceFieldId(), "administrator");

		Contact foundContact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		assertThrows(NoResourceAccessException.class, () -> contactService.removeAddress(foundContact, "administrator"));
	}

	@Test
	public void saveContactWithPhoneNumberTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");

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

	@Test
	public void findContactPhoneTest() throws Exception
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
		contact.addPhone("9542223344", null, "Office");
		contactService.updateContact(contact, "administrator");

		assertEquals("9542223344", 
				contactService.searchContactById(contact.getContactId(), "administrator").getPhones().get(0).getPhoneNumber());
	}

	@Test
	public void findContactWithRestrictedPhoneTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");

		AbacPolicy contactUpdate = new AbacPolicy("contactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdate.setCdPolicy(true, false);
		contactUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdate, "administrator");

		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");

		// Add the new contact with its address for testing
		Contact contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		contact.addPhone("9542223344", null, "Office");
		contactService.updateContact(contact, "administrator");

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

		assertNull(contactService.searchContactById(contact.getContactId(), "administrator").getPhones());
	}

	@Test
	public void updateContactPhoneTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");

		AbacPolicy contactUpdate = new AbacPolicy("contactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdate.setCdPolicy(true, false);
		contactUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdate, "administrator");

		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");

		// Add the new contact with its address for testing
		Contact contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		contact.addPhone("9542223344", null, "Office");
		contactService.updateContact(contact, "administrator");

		contact = contactService.searchContactById(contact.getContactId(), "administrator");
		ContactPhone phone = contact.getPhones().get(0);
		phone.setPhoneNumber("7545556677");
		contactService.updateContact(contact, "administrator");

		assertEquals("7545556677", 
				contactService.searchContactById(contact.getContactId(), "administrator").getPhones().get(0).getPhoneNumber());
	}

	@Test
	public void updateRestrictedContactPhoneTest() throws Exception
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

		// Add the new contact with its address for testing
		Contact contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		contact.addPhone("9542223344", null, "Office");
		contactService.updateContact(contact, "administrator");

		// Add another user with permission to read contacts
		AbacPolicy userUpdatePolicy = new AbacPolicy("UserUpdate", PolicyType.UPDATE, userResource);
		userUpdatePolicy.setCdPolicy(true, false);
		userUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userUpdatePolicy, "administrator");

		Contact managerContact = new Contact("Manager", null, null);
		contactService.saveContact(managerContact, "administrator");
		managerContact = contactService.searchContactByCharId(managerContact.getContactCharId(), "administrator");
		User managerUser = new User("manager", "mypass", managerContact);
		authService.saveUser(managerUser, "administrator");

		AbacPolicy abacRead = new AbacPolicy("PolicyRead", PolicyType.READ, abacPolicy);
		abacRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacRead, "administrator");

		contactRead = abacService.searchModifiablePolicy(contactResource, PolicyType.READ, "administrator");
		contactRead.setLogicOperator(LogicOperator.OR);
		contactRead.addEntityCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, "manager");

		// Add field restrictions for administrator user
		Role adminRole = authService.searchRoleByName("Administrators", "administrator");
		contactResource = abacService.searchResourceByNameWithFields("Contact");
		ResourceField restrictedField = contactResource.getResourceFieldByName("contactPhones");
		abacService.addFieldRestriction(adminRole.getRoleId(), restrictedField.getResourceFieldId(), "administrator");

		// Edit the contact
		contact = contactService.searchContactById(contact.getContactId(), "administrator");
		contact.addPhone("7545556677", null, "Office");

		// The administrator user with field restrictions tries to store the updated contact
		contactService.updateContact(contact, "administrator");

		// The manager user makes sure the restricted field for the administrator was not changed after the update
		Contact updatedContact = contactService.searchContactById(contact.getContactId(), "manager");
		assertTrue(updatedContact.getPhones().size() == 1);
		assertEquals("9542223344", updatedContact.getPhones().get(0).getPhoneNumber());

		// The administrator completes the testing by reading the updated contact
		updatedContact = contactService.searchContactById(contact.getContactId(), "administrator");
		assertNull(updatedContact.getPhones());
	}

	@Test
	public void deleteContactPhoneTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");

		AbacPolicy contactUpdate = new AbacPolicy("contactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdate.setCdPolicy(true, false);
		contactUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdate, "administrator");

		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");

		Contact contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		contact.addPhone("7545556677", null, "Office");
		contactService.updateContact(contact, "administrator");

		assertEquals(1, contactService.findNumberOfContactPhones());

		contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		contactService.removeContactPhone(contact.getPhones().get(0), "administrator");
		assertEquals(0, contactService.findNumberOfContactPhones());
	}

	@Test
	public void deleteRestrictedContactPhoneTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByNameWithFields("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");

		AbacPolicy contactUpdate = new AbacPolicy("contactUpdate", PolicyType.UPDATE, contactResource);
		contactUpdate.setCdPolicy(true, false);
		contactUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactUpdate, "administrator");

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

		Contact contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		contact.addPhone("7545556677", null, "Office");
		contactService.updateContact(contact, "administrator");

		contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		assertEquals(1, contact.getPhones().size());

		// Add field restrictions for administrator user
		Role adminRole = authService.searchRoleByName("Administrators", "administrator");
		ResourceField restrictedField = contactResource.getResourceFieldByName("contactPhones");
		abacService.addFieldRestriction(adminRole.getRoleId(), restrictedField.getResourceFieldId(), "administrator");

		ContactPhone phone = contact.getPhones().get(0);
		assertThrows(NoResourceAccessException.class, () -> contactService.removeContactPhone(phone, "administrator"));
	}

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
		authService.saveUser(new User("johnd", "mypass", contact), "administrator");

		assertEquals(2, authService.searchNumberOfUsers());
	}

	@Test
	public void saveNewUserWithUsernameFieldRestrictedTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource userResource = abacService.searchResourceByNameWithFields("User");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");

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
		ResourceField restrictedField = userResource.getResourceFieldByName("username");
		abacService.addFieldRestriction(role.getRoleId(), restrictedField.getResourceFieldId(), "administrator");

		// Prepare a contact to be used with the user to be tested
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"), "administrator");
		Contact contact = contactService.searchContactByEmail("johnd@example.com", "administrator");

		assertThrows(NoResourceAccessException.class, ()-> authService.saveUser(new User("john", "mypass", contact), "administrator"));
	}


	@Test
	public void saveNewUserWithPasswordFieldRestrictedTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource userResource = abacService.searchResourceByNameWithFields("User");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");

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
		ResourceField restrictedField = userResource.getResourceFieldByName("password");
		abacService.addFieldRestriction(role.getRoleId(), restrictedField.getResourceFieldId(), "administrator");

		// Prepare a contact to be used with the user to be tested
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"), "administrator");
		Contact contact = contactService.searchContactByEmail("johnd@example.com", "administrator");

		assertThrows(NoResourceAccessException.class, ()-> authService.saveUser(new User("john", "mypass", contact), "administrator"));
	}

	@Test
	public void saveNewUserWithContactFieldRestrictedTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource userResource = abacService.searchResourceByNameWithFields("User");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");

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
		ResourceField restrictedField = userResource.getResourceFieldByName("contact");
		abacService.addFieldRestriction(role.getRoleId(), restrictedField.getResourceFieldId(), "administrator");

		// Prepare a contact to be used with the user to be tested
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"), "administrator");
		Contact contact = contactService.searchContactByEmail("johnd@example.com", "administrator");

		assertThrows(NoResourceAccessException.class, ()-> authService.saveUser(new User("john", "mypass", contact), "administrator"));
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

		authService.saveUser(new User("username", "mypass", initContact), "administrator");

		User user = new User("username", "mypass", dupContact);
		assertThrows(DuplicateRecordException.class, () -> authService.saveUser(user, "administrator"));
	}

	@Test
	public void getNumberOfUsersTest() throws Exception
	{
		systemService.initialSetup();
		assertEquals(1, authService.searchNumberOfUsers());
	}

	@Test
	public void findAllUsersTest() throws Exception
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

		Contact firstContact = new Contact("John", null, "Doe", "johnd@example.com");
		contactService.saveContact(firstContact, "administrator");
		firstContact = contactService.searchContactByCharId(firstContact.getContactCharId(), "administrator");
		authService.saveUser(new User("firstUser", "pass", firstContact), "administrator");

		Contact secondContact = new Contact("Jane", null, "Doe", "janed@example.com");
		contactService.saveContact(secondContact, "administrator");
		secondContact = contactService.searchContactByCharId(secondContact.getContactCharId(), "administrator");
		authService.saveUser(new User("secondUser", "pass", secondContact), "administrator");

		Contact thirdContact = new Contact("Richard", null, "Roe", "richardr@example.com");
		contactService.saveContact(thirdContact, "administrator");
		thirdContact = contactService.searchContactByCharId(thirdContact.getContactCharId(), "administrator");
		authService.saveUser(new User("thirdUser", "pass", thirdContact), "administrator");

		List<User> users = authService.searchAllUsers("administrator");
		assertEquals(4, users.size());
		assertTrue(users.contains(secondContact.getUser()));
	}

	@Test
	public void findAllUsersWithFieldRestrictionTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource userResource = abacService.searchResourceByNameWithFields("User");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");

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

		// Add ABAC policies to allow storing field restrictions
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");

		AbacPolicy fieldRestrictPolicy = new AbacPolicy("RestrictedFieldUpdate", PolicyType.UPDATE, restrictedFieldResource);
		fieldRestrictPolicy.setCdPolicy(true, false);
		fieldRestrictPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(fieldRestrictPolicy, "administrator");

		Contact firstContact = new Contact("John", null, "Doe", "johnd@example.com");
		contactService.saveContact(firstContact, "administrator");
		firstContact = contactService.searchContactByCharId(firstContact.getContactCharId(), "administrator");
		authService.saveUser(new User("firstUser", "pass", firstContact), "administrator");

		Contact secondContact = new Contact("Jane", null, "Doe", "janed@example.com");
		contactService.saveContact(secondContact, "administrator");
		secondContact = contactService.searchContactByCharId(secondContact.getContactCharId(), "administrator");
		authService.saveUser(new User("secondUser", "pass", secondContact), "administrator");

		Contact thirdContact = new Contact("Richard", null, "Roe", "richardr@example.com");
		contactService.saveContact(thirdContact, "administrator");
		thirdContact = contactService.searchContactByCharId(thirdContact.getContactCharId(), "administrator");
		authService.saveUser(new User("thirdUser", "pass", thirdContact), "administrator");

		List<User> foundUsers = authService.searchAllUsers("administrator");
		for (User testUser : foundUsers)
		{
			assertNotNull(testUser.getLastAccess());
		}

		// Add field restrictions
		Role role = authService.searchRoleByName("Administrators", "administrator");
		ResourceField restrictedField = userResource.getResourceFieldByName("lastAccess");
		abacService.addFieldRestriction(role.getRoleId(), restrictedField.getResourceFieldId(), "administrator");

		foundUsers = authService.searchAllUsers("administrator");
		for (User testUser : foundUsers)
		{
			assertNull(testUser.getLastAccess());
		}
	}

	@Test
	public void findUsersByRangeTest() throws Exception
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

		Contact firstContact = new Contact("John", null, "Doe", "johnd@example.com");
		contactService.saveContact(firstContact, "administrator");
		firstContact = contactService.searchContactByCharId(firstContact.getContactCharId(), "administrator");
		authService.saveUser(new User("john", "pass", firstContact), "administrator");

		Contact secondContact = new Contact("Jane", null, "Doe", "janed@example.com");
		contactService.saveContact(secondContact, "administrator");
		secondContact = contactService.searchContactByCharId(secondContact.getContactCharId(), "administrator");
		authService.saveUser(new User("jane", "pass", secondContact), "administrator");

		Contact thirdContact = new Contact("Richard", null, "Roe", "richardr@example.com");
		contactService.saveContact(thirdContact, "administrator");
		thirdContact = contactService.searchContactByCharId(thirdContact.getContactCharId(), "administrator");
		authService.saveUser(new User("richard", "pass", thirdContact), "administrator");

		Contact fourthContact = new Contact("Diane", null, null, "Diane@example.com");
		contactService.saveContact(fourthContact, "administrator");
		fourthContact = contactService.searchContactByCharId(fourthContact.getContactCharId(), "administrator");
		authService.saveUser(new User("diane", "pass", fourthContact), "administrator");

		Contact fifthContact = new Contact("Catherine", null, null, "catherine@example.com");
		contactService.saveContact(fifthContact, "administrator");
		fifthContact = contactService.searchContactByCharId(fifthContact.getContactCharId(), "administrator");
		authService.saveUser(new User("catherine", "pass", fifthContact), "administrator");

		Contact sixthContact = new Contact("Marcela", null, null, "marcela@example.com");
		contactService.saveContact(sixthContact, "administrator");
		sixthContact = contactService.searchContactByCharId(sixthContact.getContactCharId(), "administrator");
		authService.saveUser(new User("marcela", "pass", sixthContact), "administrator");

		assertEquals(5, authService.searchAllUsers(5, 1, "administrator").size());
		assertEquals(2, authService.searchAllUsers(5, 2, "administrator").size());
		assertEquals(3, authService.searchAllUsers(3, 2, "administrator").size());
		assertEquals(1, authService.searchAllUsers(3, 3, "administrator").size());
	}

	@Test
	public void findUsersByRangeWithFieldRestrictionTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource userResource = abacService.searchResourceByNameWithFields("User");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");

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

		Contact firstContact = new Contact("John", null, "Doe", "johnd@example.com");
		contactService.saveContact(firstContact, "administrator");
		firstContact = contactService.searchContactByCharId(firstContact.getContactCharId(), "administrator");
		authService.saveUser(new User("john", "pass", firstContact), "administrator");

		Contact secondContact = new Contact("Jane", null, "Doe", "janed@example.com");
		contactService.saveContact(secondContact, "administrator");
		secondContact = contactService.searchContactByCharId(secondContact.getContactCharId(), "administrator");
		authService.saveUser(new User("jane", "pass", secondContact), "administrator");

		Contact thirdContact = new Contact("Richard", null, "Roe", "richardr@example.com");
		contactService.saveContact(thirdContact, "administrator");
		thirdContact = contactService.searchContactByCharId(thirdContact.getContactCharId(), "administrator");
		authService.saveUser(new User("richard", "pass", thirdContact), "administrator");

		Contact fourthContact = new Contact("Diane", null, null, "Diane@example.com");
		contactService.saveContact(fourthContact, "administrator");
		fourthContact = contactService.searchContactByCharId(fourthContact.getContactCharId(), "administrator");
		authService.saveUser(new User("diane", "pass", fourthContact), "administrator");

		Contact fifthContact = new Contact("Catherine", null, null, "catherine@example.com");
		contactService.saveContact(fifthContact, "administrator");
		fifthContact = contactService.searchContactByCharId(fifthContact.getContactCharId(), "administrator");
		authService.saveUser(new User("catherine", "pass", fifthContact), "administrator");

		Contact sixthContact = new Contact("Marcela", null, null, "marcela@example.com");
		contactService.saveContact(sixthContact, "administrator");
		sixthContact = contactService.searchContactByCharId(sixthContact.getContactCharId(), "administrator");
		authService.saveUser(new User("marcela", "pass", sixthContact), "administrator");

		// Add ABAC policies to allow storing field restrictions
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");

		AbacPolicy fieldRestrictPolicy = new AbacPolicy("RestrictedFieldUpdate", PolicyType.UPDATE, restrictedFieldResource);
		fieldRestrictPolicy.setCdPolicy(true, false);
		fieldRestrictPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(fieldRestrictPolicy, "administrator");

		List<User> foundUsers = authService.searchAllUsers(10, 1, "administrator");
		for (User user : foundUsers)
		{
			assertNotNull(user.getLastAccess());
		}

		// Add field restrictions
		Role role = authService.searchRoleByName("Administrators", "administrator");
		ResourceField restrictedField = userResource.getResourceFieldByName("lastAccess");
		abacService.addFieldRestriction(role.getRoleId(), restrictedField.getResourceFieldId(), "administrator");

		foundUsers = authService.searchAllUsers(10, 1, "administrator");
		for (User user : foundUsers)
		{
			assertNull(user.getLastAccess());
		}
	}

	@Test
	public void findUserByUserIdTest() throws Exception
	{
		systemService.initialSetup();
		Resource userResource = abacService.searchResourceByName("User");

		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "administrator");

		User foundUser = authService.searchUserByUsername("administrator", "administrator");

		assertEquals("administrator", authService.searchUserById(foundUser.getUserId(), "administrator").getUsername());
	}

	@Test
	public void findUserByUserIdWithFieldRestrictionsTest() throws Exception
	{
		systemService.initialSetup();
		Resource userResource = abacService.searchResourceByNameWithFields("User");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");

		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "administrator");		

		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");

		AbacPolicy fieldRestrictPolicy = new AbacPolicy("RestrictedFieldUpdate", PolicyType.UPDATE, restrictedFieldResource);
		fieldRestrictPolicy.setCdPolicy(true, false);
		fieldRestrictPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(fieldRestrictPolicy, "administrator");

		Role role = authService.searchRoleByName("Administrators", "administrator");
		ResourceField restrictedField = userResource.getResourceFieldByName("lastAccess");
		abacService.addFieldRestriction(role.getRoleId(), restrictedField.getResourceFieldId(), "administrator");

		User foundUser = authService.searchUserByUsername("administrator", "administrator");

		assertNull(authService.searchUserById(foundUser.getUserId(), "administrator").getLastAccess());
	}

	@Test
	public void findUserByUsernameTest() throws Exception 
	{
		systemService.initialSetup();
		Resource userResource = abacService.searchResourceByName("User");

		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "administrator");

		User foundUser = authService.searchUserByUsername("administrator", "administrator");

		assertTrue(foundUser.isEnabled());
	}

	@Test
	public void findUserByUsernameWithFieldRestrictionTest() throws Exception 
	{
		systemService.initialSetup();
		Resource userResource = abacService.searchResourceByNameWithFields("User");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");

		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "administrator");

		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");

		AbacPolicy fieldRestrictPolicy = new AbacPolicy("RestrictedFieldUpdate", PolicyType.UPDATE, restrictedFieldResource);
		fieldRestrictPolicy.setCdPolicy(true, false);
		fieldRestrictPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(fieldRestrictPolicy, "administrator");

		Role role = authService.searchRoleByName("Administrators", "administrator");
		ResourceField restrictedField = userResource.getResourceFieldByName("lastAccess");
		abacService.addFieldRestriction(role.getRoleId(), restrictedField.getResourceFieldId(), "administrator");

		assertNull(authService.searchUserByUsername("administrator", "administrator").getLastAccess());
	}

	@Test 
	public void findUserByIdWithContactTest() throws Exception
	{
		systemService.initialSetup();
		Resource userResource = abacService.searchResourceByName("User");
		Resource contactResource = abacService.searchResourceByName("Contact");

		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "administrator");

		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");

		int userId = authService.searchUserByUsername("administrator", "administrator").getUserId();

		assertEquals("uec_ops_support@unlimitedcompanies.com", 
				authService.searchUserByIdWithContact(userId, "administrator").getContact().getEmail());
	}

	@Test 
	public void findUserByUsernameWithContactTest() throws Exception
	{
		systemService.initialSetup();
		Resource userResource = abacService.searchResourceByName("User");
		Resource contactResource = abacService.searchResourceByName("Contact");

		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "administrator");

		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");

		assertEquals("uec_ops_support@unlimitedcompanies.com", 
				authService.searchUserByUsernameWithContact("administrator", "administrator").getContact().getEmail());
	}

	@Test
	public void findUserByContactTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource userResource = abacService.searchResourceByName("User");

		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");

		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "administrator");

		Contact contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");

		assertEquals("administrator", authService.searchUserByContact(contact, "administrator").getUsername());
	}

	@Test 
	public void findUserByIdWithContactAndContactRestrictionTest() throws Exception
	{
		systemService.initialSetup();
		Resource userResource = abacService.searchResourceByNameWithFields("User");
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource restrictedFieldResource = abacService.searchResourceByName("RestrictedField");

		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "administrator");

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

		Role role = authService.searchRoleByName("Administrators", "administrator");
		ResourceField restrictedField = userResource.getResourceFieldByName("contact");
		abacService.addFieldRestriction(role.getRoleId(), restrictedField.getResourceFieldId(), "administrator");

		int userId = authService.searchUserByUsername("administrator", "administrator").getUserId();

		assertNull(authService.searchUserByIdWithContact(userId, "administrator").getContact());
	}

	@Test
	public void findUserWithContactAndRoleTest() throws Exception
	{
		systemService.initialSetup();
		Resource userResource = abacService.searchResourceByName("User");
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");

		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "administrator");
		
		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");

		AbacPolicy roleReadPolicy = new AbacPolicy("roleRead", PolicyType.READ, roleResource);
		roleReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleReadPolicy, "administrator");
		
		User user = authService.searchFullUserByUsername("administrator", "administrator");
		assertEquals("Administrator", user.getContact().getFirstName());
		assertTrue(user.getRoles().size() > 0);
	}

	@Test
	public void updateUserTest() throws Exception
	{
		systemService.initialSetup();
		Resource userResource = abacService.searchResourceByName("User");

		AbacPolicy userCreatePolicy = new AbacPolicy("UserCreate", PolicyType.UPDATE, userResource);
		userCreatePolicy.setCdPolicy(false, false);
		userCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userCreatePolicy, "administrator");

		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "administrator");

		User user = authService.searchUserByUsername("administrator", "administrator");
		user.setUsername("admin");
		user.setEnabled(false);
		authService.updateUser(user, "administrator");

		assertFalse(authService.searchUserById(user.getUserId(), "admin").isEnabled());
	}
	
	@Test
	public void updateUserWithProhibitedFieldsTest() throws Exception
	{
		systemService.initialSetup();
		Resource userResource = abacService.searchResourceByName("User");

		AbacPolicy userCreatePolicy = new AbacPolicy("UserCreate", PolicyType.UPDATE, userResource);
		userCreatePolicy.setCdPolicy(false, false);
		userCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userCreatePolicy, "administrator");

		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "administrator");

		User user = authService.searchUserByUsername("administrator", "administrator");
		user.setUsername("admin");
		user.setEnabled(false);
		user.setPassword("testingnewpass");
		
		authService.updateUser(user, "administrator");

		assertFalse(authService.searchUserById(user.getUserId(), "admin").isEnabled());
		assertFalse(authService.searchUserById(user.getUserId(), "admin").isPassword("testingnewpass"));
	}

	@Test
	public void updateUserNoPermissionTest() throws Exception
	{
		systemService.initialSetup();
		Resource userResource = abacService.searchResourceByName("User");

		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "administrator");

		User user = authService.searchUserByUsername("administrator", "administrator");
		user.setUsername("admin");
		user.setEnabled(false);

		assertThrows(NoResourceAccessException.class, ()-> authService.updateUser(user, "administrator"));
	}

	@Test
	public void successfullUserPasswordChangeTest() throws Exception
	{
		systemService.initialSetup();
		Resource userResource = abacService.searchResourceByName("User");

		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "administrator");

		AbacPolicy userCreatePolicy = new AbacPolicy("UserCreate", PolicyType.UPDATE, userResource);
		userCreatePolicy.setCdPolicy(false, false);
		userCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userCreatePolicy, "administrator");

		User user = authService.searchUserByUsername("administrator", "administrator");
		authService.changeUserPassword(user, "uec123", "pass123", "administrator");

		assertTrue(authService.searchUserByUsername("administrator", "administrator").isPassword("pass123"));
	}

	@Test
	public void userPasswordChangeWithIncorrectPasswordTest() throws Exception
	{
		systemService.initialSetup();
		Resource userResource = abacService.searchResourceByName("User");

		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "administrator");

		AbacPolicy userCreatePolicy = new AbacPolicy("UserCreate", PolicyType.UPDATE, userResource);
		userCreatePolicy.setCdPolicy(false, false);
		userCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userCreatePolicy, "administrator");

		User user = authService.searchUserByUsername("administrator", "administrator");
		assertThrows(IncorrectPasswordException.class, ()-> authService.changeUserPassword(user, "Uec123", "pass123", "administrator"));
	}

	@Test
	public void deleteSingleUserTest() throws Exception
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
		userUpdatePolicy.setCdPolicy(true, true);
		userUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userUpdatePolicy, "administrator");
		
		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "administrator");

		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"), "administrator");
		Contact contact = contactService.searchContactByEmail("johnd@example.com", "administrator");
		authService.saveUser(new User("john", "mypass", contact), "administrator");
		User user = authService.searchUserByUsername("john", "administrator");
		
		assertEquals(2, authService.searchNumberOfUsers());
		authService.deleteUser(user.getUserId(), "administrator");
		assertEquals(1, authService.searchNumberOfUsers());
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

		assertEquals(2, authService.searchNumberOfRoles());
	}
	
	@Test
	public void getNumberOfRolesTest() throws Exception
	{
		systemService.initialSetup();
		assertEquals(1, authService.searchNumberOfRoles());
	}

	@Test
	public void findAllRolesTest() throws Exception
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

		authService.saveRole(new Role("Managers"), "administrator");
		authService.saveRole(new Role("Architects"), "administrator");
		authService.saveRole(new Role("Engineers"), "administrator");
		authService.saveRole(new Role("Developers"), "administrator");

		assertEquals(5, authService.searchAllRoles("administrator").size());
	}

	@Test
	public void findRolesByRangeTest() throws Exception
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
		
		authService.saveRole(new Role("Managers"), "administrator");			// 11
		authService.saveRole(new Role("Architects"), "administrator");			// 2
		authService.saveRole(new Role("Engineers"), "administrator");			// 8
		authService.saveRole(new Role("Developers"), "administrator");			// 5
		authService.saveRole(new Role("IT Techs"), "administrator");			// 10
		authService.saveRole(new Role("Project Managers"), "administrator");	// 13
		authService.saveRole(new Role("Superintendents"), "administrator");		// 16
		authService.saveRole(new Role("Electricians"), "administrator");		// 7
		authService.saveRole(new Role("Mechanics"), "administrator");			// 12
		authService.saveRole(new Role("Accountants"), "administrator");			// 1
		authService.saveRole(new Role("Drawing Techs"), "administrator");		// 6
		authService.saveRole(new Role("Coordinators"), "administrator");		// 4
		authService.saveRole(new Role("Service Agents"), "administrator");		// 15
		authService.saveRole(new Role("HR Agents"), "administrator");			// 9
		authService.saveRole(new Role("Receptionists"), "administrator");		// 14
		// Administrators														// 2

		assertEquals("Drawing Techs", authService.searchAllRoles(5, 2, "administrator").get(0).getRoleName());
		assertEquals("Mechanics", authService.searchAllRoles(3, 4, "administrator").get(2).getRoleName());
		assertEquals("Superintendents", authService.searchAllRoles(10, 2, "administrator").get(5).getRoleName());
	}

	@Test
	public void findRoleByIdTest() throws Exception
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

	@Test
	public void findRoleByIdWithMembers() throws Exception
	{
		systemService.initialSetup();
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource userResource = abacService.searchResourceByName("User");
		Resource contactResource = abacService.searchResourceByName("Contact");
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");
		
		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "administrator");
		
		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");

		User user = authService.searchUserByUsername("administrator", "administrator");
		
		Role singleRole = authService.searchRoleByName("Administrators", "administrator");
		Role fullRole = authService.searchRoleByIdWithMembers(singleRole.getRoleId(), "administrator");

		assertTrue(fullRole.getUsers().contains(user));

	}
	
	@Test
	public void findRoleByRoleNameWithMembers() throws Exception
	{
		systemService.initialSetup();
		Resource roleResource = abacService.searchResourceByName("Role");
		Resource userResource = abacService.searchResourceByName("User");
		Resource contactResource = abacService.searchResourceByName("Contact");
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");
		
		AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
		userReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(userReadPolicy, "administrator");
		
		AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");

		User user = authService.searchUserByUsername("administrator", "administrator");
		
		Role fullRole = authService.searchRoleByNameWithMembers("Administrators", "administrator");
		assertTrue(fullRole.getUsers().contains(user));

	}

//	@Test
//	public void searchRoleNonMembersTest() throws Exception
//	{
//
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

		assertEquals(roleId, authService.searchRoleByName("Coordinator", "administrator").getRoleId());
	}

	@Test
	public void deleteRoleTest() throws Exception
	{		
		systemService.initialSetup();
		Resource roleResource = abacService.searchResourceByName("Role");

		AbacPolicy roleUpdate = new AbacPolicy("RoleUpdate", PolicyType.UPDATE, roleResource);
		roleUpdate.setCdPolicy(true, true);
		roleUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleUpdate, "administrator");
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");

		authService.saveRole(new Role("Manager"), "administrator");
		int roleId = authService.searchRoleByName("Manager", "administrator").getRoleId();

		authService.deleteRole(roleId, "administrator");		
		assertThrows(RecordNotFoundException.class, () -> authService.searchRoleById(roleId, "administrator"));
	}

	@Test
	public void assignUserToRoleTest() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource userResource = abacService.searchResourceByName("User");
		Resource roleResource = abacService.searchResourceByName("Role");

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
		
		AbacPolicy roleUpdate = new AbacPolicy("RoleUpdate", PolicyType.UPDATE, roleResource);
		roleUpdate.setCdPolicy(true, false);
		roleUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleUpdate, "administrator");
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");

		// Store a new contact and user and get a reference to the user
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"), "administrator");
		Contact contact = contactService.searchContactByEmail("johnd@example.com", "administrator");
		authService.saveUser(new User("johnd", "mypass", contact), "administrator");
		User user = authService.searchUserByUsername("johnd", "administrator");
		
		// Store a new role and get a reference to it
		authService.saveRole(new Role("Manager"), "administrator");
		Role role = authService.searchRoleByName("Manager", "administrator"); 
		
		authService.assignUserToRole(user.getUserId(), role.getRoleId(), "administrator");
		
		assertTrue(authService.searchRoleByIdWithMembers(role.getRoleId(), "administrator").getUsers().contains(user));
		
	}

	@Test
	public void removeUserFromRole() throws Exception
	{
		systemService.initialSetup();
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource userResource = abacService.searchResourceByName("User");
		Resource roleResource = abacService.searchResourceByName("Role");

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
		
		AbacPolicy roleUpdate = new AbacPolicy("RoleUpdate", PolicyType.UPDATE, roleResource);
		roleUpdate.setCdPolicy(true, false);
		roleUpdate.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleUpdate, "administrator");
		
		AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
		roleRead.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(roleRead, "administrator");

		// Store a new contact and user and get a reference to the user
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"), "administrator");
		Contact contact = contactService.searchContactByEmail("johnd@example.com", "administrator");
		authService.saveUser(new User("johnd", "mypass", contact), "administrator");
		User user = authService.searchUserByUsername("johnd", "administrator");
		
		int roleId = authService.searchRoleByName("Administrators", "administrator").getRoleId();
		authService.assignUserToRole(user.getUserId(), roleId, "administrator");
				
		authService.removeRoleMember(user.getUserId(), roleId, "administrator");
		
		Role adminRole = authService.searchRoleByIdWithMembers(roleId, "administrator");		
		assertFalse(adminRole.getUsers().contains(user));

		// TODO: this should also test if the role was removed from the user;
	}

}