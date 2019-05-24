package com.unlimitedcompanies.coms.service.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.domain.abac.ABACPolicy;
import com.unlimitedcompanies.coms.domain.abac.ComparisonOperator;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.UserAttribute;
import com.unlimitedcompanies.coms.domain.security.Address;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.service.abac.SystemAbacService;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
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
	SystemAbacService setupService;
	
	@Autowired
	ABACService abacService;
	
	@Test
	public void saveSimpleContactTest() throws Exception
	{
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByNameWithFieldsAndPolicy("Contact");
		
		ABACPolicy abacPolicy = new ABACPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		abacPolicy.setCdPolicy(true, false);
		abacPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacPolicy, "administrator");
		
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		contactService.saveContact(contact, "administrator");
		
		assertEquals(2, contactService.findNumberOfContacts(),
				"Test for Saving a new contact from contact service failed");
	}
	
	@Test
	public void saveContactWithRepeatedEmailNotAllowedTest() throws Exception
	{
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByName("Contact");
		
		ABACPolicy abacPolicy = new ABACPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
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
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByName("Contact");
		
		ABACPolicy abacUpdatePolicy = new ABACPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		abacUpdatePolicy.setCdPolicy(true, false);
		abacUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacUpdatePolicy, "administrator");
		
		ABACPolicy abacReadPolicy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		abacReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacReadPolicy, "administrator");
		
		contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"), "administrator");
		contactService.saveContact(new Contact("Diane", null, null, "Diane@example.com"), "administrator");
		contactService.saveContact(new Contact("Ella", null, null, "ella@example.com"), "administrator");
		contactService.saveContact(new Contact("Catherine", null, null, "catherine@example.com"), "administrator");
		contactService.saveContact(new Contact("Marcela", null, null, "marcela@example.com"), "administrator");
		
		assertEquals(6, contactService.searchAllContacts("administrator").size());
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
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByName("Contact");
		
		ABACPolicy abacUpdatePolicy = new ABACPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		abacUpdatePolicy.setCdPolicy(true, false);
		abacUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacUpdatePolicy, "administrator");
		
		ABACPolicy abacReadPolicy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
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
	public void findContactByIdTest() throws Exception
	{
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByName("Contact");
		
		ABACPolicy abacUpdatePolicy = new ABACPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		abacUpdatePolicy.setCdPolicy(true, false);
		abacUpdatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacUpdatePolicy, "administrator");
		
		ABACPolicy abacReadPolicy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		abacReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(abacReadPolicy, "administrator");
		
		Contact john = new Contact("John", null, "Doe");
		
		contactService.saveContact(john, "administrator");
		Contact foundContact = contactService.searchContactByCharId(john.getContactCharId(), "administrator");
		
		assertEquals("John", contactService.searchContactById(foundContact.getContactId(), "administrator").getFirstName(), 
				"Service test to find contact by id failed");
	}
	
	@Test
	public void findContactByCharIdTest() throws Exception
	{
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByName("Contact");
		
		ABACPolicy contactCreatePolicy = new ABACPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		contactCreatePolicy.setCdPolicy(true, false);
		contactCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactCreatePolicy, "administrator");
		
		ABACPolicy contactReadPolicy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		Contact contact = new Contact("John", null, "Doe");
		contactService.saveContact(contact, "administrator");
		
		assertEquals(contact, contactService.searchContactByCharId(contact.getContactCharId(), "administrator"), 
				"Find contact by char id test failed");
	}

	@Test
	public void findContactByEmailTest() throws Exception
	{
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByName("Contact");
		
		ABACPolicy contactCreatePolicy = new ABACPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		contactCreatePolicy.setCdPolicy(true, false);
		contactCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactCreatePolicy, "administrator");
		
		ABACPolicy contactReadPolicy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		String charId = contact.getContactCharId();
		contactService.saveContact(contact, "administrator");

		assertEquals(charId, contactService.searchContactByEmail("john@example.com", "administrator").getContactCharId(),
				"Service test  to find contact by email failed");
	}

	@Test
	public void updateContactTest() throws Exception
	{
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByName("Contact");
		
		ABACPolicy contactCreatePolicy = new ABACPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		contactCreatePolicy.setCdPolicy(true, false);
		contactCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactCreatePolicy, "administrator");
		
		ABACPolicy contactReadPolicy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
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
	public void noPersistentObjectUpdateTest() throws Exception
	{
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByName("Contact");
		
		ABACPolicy contactCreatePolicy = new ABACPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		contactCreatePolicy.setCdPolicy(true, false);
		contactCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactCreatePolicy, "administrator");
		
		ABACPolicy contactReadPolicy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
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
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByName("Contact");
		
		ABACPolicy contactCreatePolicy = new ABACPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		contactCreatePolicy.setCdPolicy(true, true);
		contactCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactCreatePolicy, "administrator");
		
		ABACPolicy contactReadPolicy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
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
	public void saveContactAddressTest() throws Exception
	{
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByName("Contact");
		Resource addressContactResource = abacService.findResourceByName("Address");
		
		ABACPolicy contactCreatePolicy = new ABACPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		contactCreatePolicy.setCdPolicy(true, false);
		contactCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactCreatePolicy, "administrator");
		
		ABACPolicy contactReadPolicy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		ABACPolicy addressCreatePolicy = new ABACPolicy("AddressCreate", PolicyType.UPDATE, addressContactResource);
		addressCreatePolicy.setCdPolicy(true, false);
		addressCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(addressCreatePolicy, "administrator");

		Contact contact = new Contact("John", null, "Doe");
		contactService.saveContact(contact, "administrator");
		contact = contactService.searchContactByCharId(contact.getContactCharId(), "administrator");
		
		contactService.saveContactAddress(new Address("0000 MyStreet Dr", "MyCity", "FL", "00001", contact), "administrator");
		
		assertEquals(1, contactService.findNumberOfContactAddresses(), "Service test for saving new contact address failed");
	}

	@Test
	public void numberOfAddressesTest()
	{
		assertEquals(0, contactService.findNumberOfContactAddresses(), "Service test to get the number of addresses failed");
	}
	
	@Test
	public void findAllContactAddressesTest() throws Exception
	{
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByName("Contact");
		Resource addressContactResource = abacService.findResourceByName("Address");
		
		ABACPolicy contactCreatePolicy = new ABACPolicy("ContactCreate", PolicyType.UPDATE, contactResource);
		contactCreatePolicy.setCdPolicy(true, false);
		contactCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactCreatePolicy, "administrator");
		
		ABACPolicy contactReadPolicy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		ABACPolicy addressCreatePolicy = new ABACPolicy("AddressCreate", PolicyType.UPDATE, addressContactResource);
		addressCreatePolicy.setCdPolicy(true, false);
		addressCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(addressCreatePolicy, "administrator");

		ABACPolicy addressReadPolicy = new ABACPolicy("AddressRead", PolicyType.READ, addressContactResource);
		addressReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(addressReadPolicy, "administrator");
		
		Contact contact0 = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");

		Contact contact1 = new Contact("John", null, "Doe");
		contactService.saveContact(contact1, "administrator");
		contact1 = contactService.searchContactByCharId(contact1.getContactCharId(), "administrator");
		
		Contact contact2 = new Contact("Jane", null, "Doe");
		contactService.saveContact(contact2, "administrator");
		contact2 = contactService.searchContactByCharId(contact2.getContactCharId(), "administrator");
		
		contactService.saveContactAddress(new Address("0000 AdminStreet Dr", "MyCity", "FL", "00001", contact0), "administrator");
		contactService.saveContactAddress(new Address("1111 JohnStreet Dr", "DoeCity", "FL", "00002", contact1), "administrator");
		contactService.saveContactAddress(new Address("2222 JaneStreet Dr", "DoeCity", "FL", "00003", contact2), "administrator");
		
		assertEquals(3, contactService.searchAllContactAddresses("administrator").size(), "Find all contact addresses test failed");
	}
	
	@Test
	public void findContactAddressTest() throws Exception
	{
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByName("Contact");
		Resource addressContactResource = abacService.findResourceByName("Address");
		
		ABACPolicy contactReadPolicy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		ABACPolicy addressCreatePolicy = new ABACPolicy("AddressCreate", PolicyType.UPDATE, addressContactResource);
		addressCreatePolicy.setCdPolicy(true, false);
		addressCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(addressCreatePolicy, "administrator");

		ABACPolicy addressReadPolicy = new ABACPolicy("AddressRead", PolicyType.READ, addressContactResource);
		addressReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(addressReadPolicy, "administrator");
		
		Contact contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		contactService.saveContactAddress(new Address("0000 AdminStreet Dr", "MyCity", "FL", "00001", contact), "administrator");
		
		assertEquals("0000 AdminStreet Dr", contactService.searchContactAddress(contact, "administrator").getStreet(), 
				"Find a contact address test failed");
	}
	
	@Test
	public void findAddressByIdTest() throws Exception
	{
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByName("Contact");
		Resource addressContactResource = abacService.findResourceByName("Address");
		
		ABACPolicy contactReadPolicy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		ABACPolicy addressCreatePolicy = new ABACPolicy("AddressCreate", PolicyType.UPDATE, addressContactResource);
		addressCreatePolicy.setCdPolicy(true, false);
		addressCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(addressCreatePolicy, "administrator");

		ABACPolicy addressReadPolicy = new ABACPolicy("AddressRead", PolicyType.READ, addressContactResource);
		addressReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(addressReadPolicy, "administrator");
		
		Contact contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		contactService.saveContactAddress(new Address("0000 AdminStreet Dr", "MyCity", "FL", "00001", contact), "administrator");
		Address address = contactService.searchContactAddress(contact, "administrator");
		int id = address.getAddressId();
		
		assertEquals("0000 AdminStreet Dr", contactService.searchContactAddressById(id, "administrator").getStreet(), 
				"Find a contact address by id test failed");
	}
	
	// TODO: Possibly add methods and tests for search functions such as finding addresses by zip, state and city
	
	@Test
	public void updateContactAddressTest() throws Exception
	{
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByName("Contact");
		Resource addressContactResource = abacService.findResourceByName("Address");
		
		ABACPolicy contactReadPolicy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		ABACPolicy addressCreatePolicy = new ABACPolicy("AddressCreate", PolicyType.UPDATE, addressContactResource);
		addressCreatePolicy.setCdPolicy(true, false);
		addressCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(addressCreatePolicy, "administrator");

		ABACPolicy addressReadPolicy = new ABACPolicy("AddressRead", PolicyType.READ, addressContactResource);
		addressReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(addressReadPolicy, "administrator");
		
		Contact contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		contactService.saveContactAddress(new Address("0000 AdminStreet Dr", "MyCity", "FL", "00001", contact), "administrator");
		Address address = contactService.searchContactAddress(contact, "administrator");
		
		address.setFullAddress("0101 New Admin St", "New City", "FL", "12123");		
		contactService.updateAddress(address, "administrator");
		
		assertEquals("0101 New Admin St", contactService.searchContactAddress(contact, "administrator").getStreet(), 
				"Updating the contact address test failed");
	}
	
	@Test
	public void noPersistentAutoUpdateContactAddressTest() throws Exception
	{
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByName("Contact");
		Resource addressContactResource = abacService.findResourceByName("Address");
		
		ABACPolicy contactReadPolicy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		ABACPolicy addressCreatePolicy = new ABACPolicy("AddressCreate", PolicyType.UPDATE, addressContactResource);
		addressCreatePolicy.setCdPolicy(true, false);
		addressCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(addressCreatePolicy, "administrator");

		ABACPolicy addressReadPolicy = new ABACPolicy("AddressRead", PolicyType.READ, addressContactResource);
		addressReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(addressReadPolicy, "administrator");
		
		Contact contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		contactService.saveContactAddress(new Address("0000 AdminStreet Dr", "MyCity", "FL", "00001", contact), "administrator");
		Address address = contactService.searchContactAddress(contact, "administrator");
		
		address.setFullAddress("0101 New Admin St", "New City", "FL", "12123");
		
		assertEquals("0000 AdminStreet Dr", contactService.searchContactAddress(contact, "administrator").getStreet(), 
				"Updating the contact address test failed");
	}
	
	@Test
	public void deleteContactAddressTest() throws Exception
	{
		setupService.initialSetup();
		Resource contactResource = abacService.findResourceByName("Contact");
		Resource addressContactResource = abacService.findResourceByName("Address");
		
		ABACPolicy contactReadPolicy = new ABACPolicy("ContactRead", PolicyType.READ, contactResource);
		contactReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(contactReadPolicy, "administrator");
		
		ABACPolicy addressCreatePolicy = new ABACPolicy("AddressCreate", PolicyType.UPDATE, addressContactResource);
		addressCreatePolicy.setCdPolicy(true, true);
		addressCreatePolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(addressCreatePolicy, "administrator");

		ABACPolicy addressReadPolicy = new ABACPolicy("AddressRead", PolicyType.READ, addressContactResource);
		addressReadPolicy.addEntityCondition(UserAttribute.ROLES, ComparisonOperator.EQUALS, "Administrators");
		abacService.savePolicy(addressReadPolicy, "administrator");
		
		Contact contact = contactService.searchContactByEmail("uec_ops_support@unlimitedcompanies.com", "administrator");
		contactService.saveContactAddress(new Address("0000 AdminStreet Dr", "MyCity", "FL", "00001", contact), "administrator");
		Address address = contactService.searchContactAddress(contact, "administrator");
		
		contactService.deleteContactAddress(address, "administrator");
		
		assertEquals(0, contactService.findNumberOfContactAddresses(), "Delete contact address test failed");
	}
	
	//
	// @Test
	// public void getNumberOfPhoneNumbersTest()
	// {
	// assertEquals(0, contactService.findNumberOfContacPhones(),
	// "Service Integration Test for to find number of contact phone numbers
	// failed");
	// }
	//
	// @Test
	// public void saveContactPhoneTest() throws InvalidPhoneNumberException
	// {
	// contactService.saveContact(new Contact("John", null, "Doe",
	// "johnd@example.com"));
	// Contact contact = contactService.findContactByEmail("johnd@example.com");
	//
	// contactService.saveContactPhone(new Phone("7775554433", null, null,
	// contact));
	// assertEquals(1, contactService.findNumberOfContacPhones());
	// }
	//
	// @Test
	// public void findContactPhonesByNumberTest() throws
	// InvalidPhoneNumberException
	// {
	// contactService.saveContact(new Contact("John", null, "Doe",
	// "johnd@example.com"));
	// Contact contact = contactService.findContactByEmail("johnd@example.com");
	// contactService.saveContactPhone(new Phone("7775554433", null, null,
	// contact));
	// contactService.saveContactPhone(new Phone("7775554433", null, null,
	// contact));
	// contactService.saveContactPhone(new Phone("7775554413", null, null,
	// contact));
	// contactService.saveContactPhone(new Phone("7775554433", null, null,
	// contact));
	//
	// List<Phone> phones = contactService.findContactPhoneByNumber("7775554433");
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
	// contactService.saveContactPhone(new Phone("7775554433", null, null,
	// contact));
	//
	// Phone initialPhone = null;
	// List<Phone> phones = contactService.findContactPhoneByNumber("7775554433");
	// for (Phone next : phones)
	// {
	// initialPhone = next;
	// }
	//
	// Phone foundPhone =
	// contactService.findContactPhoneById(initialPhone.getPhoneId());
	// assertEquals(initialPhone, foundPhone, "Service test to find contact phone by
	// id failed");
	// }
	//
	
	// TODO: Create a test for finding a full contact with address and phone numbers

//	@Test
//	public void saveNewUserTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact = contactService.searchContactByEmail("johnd@example.com");
//
//		User user = authService.saveUser(new User("username", "mypass".toCharArray(), contact));
//		assertEquals(1, authService.searchNumberOfUsers(), "Service test to save a new user failed");
//		assertNotNull(user.getUserId(), "Service test to save a new user failed");
//	}

//	@Test
//	public void getNumberOfUsersTest()
//	{
//		assertEquals(0, authService.searchNumberOfUsers(), "Service test to find the number of users failed");
//	}
	
//	@Test
//	public void findAllUsersTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
//	{
//		Contact contact1 = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact2 = contactService.saveContact(new Contact("Jane", null, "Doe", "janed@example.com"));
//		Contact contact3 = contactService.saveContact(new Contact("Richard", null, "Doe", "rich@example.com"));
//
//		authService.saveUser(new User("username1", "mypass".toCharArray(), contact1));
//		User user = new User("username2", "mypass".toCharArray(), contact2);
//		user.setEnabled(false);
//		authService.saveUser(user);
//		authService.saveUser(new User("username3", "mypass".toCharArray(), contact3));
//		
//		assertEquals(3, authService.searchAllUsers().size(), "Find all users integration test failed");
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
//	
//	@Test
//	public void findUserByUserId() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact = contactService.searchContactByEmail("johnd@example.com");
//		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));
//		
//		User foundUser = authService.searchUserByUserId(user.getUserId());
//		assertEquals(user, foundUser, "Service test for finding user by userId failed");
//	}
//	
//	@Test
//	public void findUserByUsernameTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact = contactService.searchContactByEmail("johnd@example.com");
//		User user = new User("jdoe", "mypass".toCharArray(), contact);
//		authService.saveUser(user);
//
//		User founduser = authService.searchUserByUsername("jdoe");
//
//		assertEquals(user, founduser, "Service test for finding user by username with contact failed");
//	}
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
//
//	@Test
//	public void getNumberOfRolesTest()
//	{
//		assertEquals(0, authService.searchNumberOfRoles(), "Service test for finding number of roles failed");
//	}
//
//	@Test
//	public void saveNewRoleTest() throws RecordNotCreatedException
//	{
//		Role role = authService.saveRole(new Role("Administrator"));
//		assertEquals(1, authService.searchNumberOfRoles(), "Service test for saving new role failed");
//		assertNotNull(role.getRoleId(), "Service test for saving new role failed");
//	}
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
//	
//	@Test
//	public void findRoleByRoleIdTest() throws RecordNotCreatedException, RecordNotFoundException
//	{
//		Role initialrole = new Role("Administrator");
//		System.out.println(initialrole.getRoleId());
//		Role savedRole = authService.saveRole(initialrole);
//		System.out.println("Searching for role with id: " + savedRole.getRoleId());
//		Role foundRole = authService.searchRoleById(savedRole.getRoleId());
//		System.out.println("found Role: " + foundRole.getRoleId());
//		
//		assertEquals(initialrole, foundRole, "Service test for finding role by roleId failed");
//	}
//	
//	@Test
//	public void findRoleByRoleNameTest() throws RecordNotCreatedException, RecordNotFoundException
//	{
//		Role initialrole = new Role("Administrator");
//		authService.saveRole(initialrole);
//		Role foundrole = authService.searchRoleByName("Administrator");
//		
//		assertEquals(initialrole, foundrole, "Service test for finding role by roleName failed");
//	}
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
//
//	@Test
//	 public void updateRoleTest() throws RecordNotCreatedException, RecordNotFoundException, RecordNotChangedException
//	 {
//		Role role = authService.saveRole(new Role("Administrator"));
//		Role newrole = new Role("Admins");
//		newrole.setRoleId(role.getRoleId());
//		
//		role = authService.updateRole(newrole);
//		
//		assertNotEquals("Administrator", role.getRoleName(), "Service test for updating role has failed");
//		assertEquals("Admins", role.getRoleName(), "Service test for updating role has failed");
//	 }
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
//		setupService.checkAllResources();
//		List<String> resources = setupService.findAllResourceNames();
//		List<ResourceField> resourceFields = setupService.findAllResourceFieldsWithResources();
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
//		setupService.checkAllResources();
//		Resource resource = setupService.findResourceByName("Contact");
//		assertEquals("Contact", resource.getResourceName(), "Find resource integration test failed");
//	}
}