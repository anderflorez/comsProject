package com.unlimitedcompanies.coms.service.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.unlimitedcompanies.coms.domain.security.Address;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Phone;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.domain.security.exceptions.InvalidPhoneNumberException;
import com.unlimitedcompanies.coms.securityService.AuthenticationService;
import com.unlimitedcompanies.coms.securityService.ContactService;
import com.unlimitedcompanies.coms.securityServiceExceptions.NonExistingContactException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
@Transactional
@ActiveProfiles("integrationTesting")
class SecurityServiceIntegrationTest
{
	@Autowired
	ContactService contactService;

	@Autowired
	AuthenticationService authenticationService;

//	@Test
//	public void numberOfContactsIntegrationTest()
//	{
//		assertEquals(0, contactService.findNumberOfContacts(), "Number of contacts service test has failed");
//	}
//
//	@Test
//	public void saveANewSimpleContactTest()
//	{
//		Contact contact = new Contact("John", null, "Doe", "john@example.com");
//		contactService.saveContact(contact);
//		assertEquals(1, contactService.findNumberOfContacts(), "Test for Saving a new contact from contact service failed");
//	}
//
//	@Test
//	public void findContactByEmailTest()
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"));
//
//		assertNotNull(contactService.findContactByEmail("john@example.com"),
//				"Service test  to find contact by email failed");
//	}
//
//	@Test
//	public void findContactByIdTest()
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"));
//		Contact initialContact = contactService.findContactByEmail("john@example.com");
//
//		Contact foundContact = contactService.findContactById(initialContact.getContactId());
//
//		assertEquals(initialContact, foundContact, "Service test to find contact by id failed");
//	}
//
//	@Test
//	public void updateContactTest()
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"));
//		Contact initialContact = contactService.findContactByEmail("john@example.com");
//		Contact correctedContact = new Contact("Jane", null, "Roe", "jane@example.com");
//
//		contactService.updateContact(initialContact.getContactId(), correctedContact);
//		Contact foundContact = contactService.findContactById(initialContact.getContactId());
//
//		assertEquals(correctedContact, foundContact, "Service test for updating contact failed");
//	}
//
//	@Test
//	public void deleteSingleContactTest()
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		contactService.saveContact(new Contact("Jane", null, "Doe", "janed@example.com"));
//
//		Contact deleteContact = contactService.findContactByEmail("johnd@example.com");
//		contactService.deleteContact(deleteContact);
//
//		assertEquals(1, contactService.findNumberOfContacts(), "Service test for deleting a single contact failed");
//	}
//
//	@Test
//	public void numberOfAddressesTest()
//	{
//		assertEquals(0, contactService.findNumberOfContactAddresses(),
//				"Service test to get the number of addresses failed");
//	}
//
//	@Test
//	public void saveContactAddressTest()
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact = contactService.findContactByEmail("johnd@example.com");
//
//		contactService.saveContactAddress(new Address("0000 MyStreet Dr", "MyCity", "FL", "00001", contact));
//
//		assertEquals(1, contactService.findNumberOfContactAddresses(), "Service test for saving new contact address failed");
//	}
//
//	@Test
//	public void findAddressByZipCodeTest()
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		contactService.saveContact(new Contact("Jane", null, "Doe", "janed@example.com"));
//		contactService.saveContact(new Contact("Robert", null, "Roe", "rroe@example.com"));
//		
//		Contact contact1 = contactService.findContactByEmail("johnd@example.com");
//		Contact contact2 = contactService.findContactByEmail("janed@example.com");
//		Contact contact3 = contactService.findContactByEmail("rroe@example.com");
//		
//		contactService.saveContactAddress(new Address("0000 MyStreet Dr", "MyCity", "FL", "00001", contact1));
//		contactService.saveContactAddress(new Address("1111 YourStreet Dr", "MyCity", "FL", "00011", contact2));
//		contactService.saveContactAddress(new Address("2222 HisStreet Dr", "MyCity", "FL", "00001", contact3));
//
//		List<Address> addresses = contactService.findContactAddressesByZipCode("00001");
//
//		assertEquals(2, addresses.size(), "Service test for finding address by zip code failed");
//	}
//
//	@Test
//	public void findContactAddressByIdTest()
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact = contactService.findContactByEmail("johnd@example.com");
//		Address initialAddress = new Address("0000 MyStreet Dr", "MyCity", "FL", "00001", contact);
//		contactService.saveContactAddress(initialAddress);
//
//		List<Address> addresses = contactService.findContactAddressesByZipCode("00001");
//		Address foundAddress = null;
//		for (Address next : addresses)
//		{
//			foundAddress = next;
//		}
//
//		assertEquals(initialAddress, foundAddress, "Service test for finding contact address by id failed");
//	}
//
//	@Test
//	public void getNumberOfPhoneNumbersTest()
//	{
//		assertEquals(0, contactService.findNumberOfContacPhones(),
//				"Service Integration Test for to find number of contact phone numbers failed");
//	}
//
//	@Test
//	public void saveContactPhoneTest() throws InvalidPhoneNumberException
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact = contactService.findContactByEmail("johnd@example.com");
//
//		contactService.saveContactPhone(new Phone("7775554433", null, null, contact));
//		assertEquals(1, contactService.findNumberOfContacPhones());
//	}
//
//	@Test
//	public void findContactPhonesByNumberTest() throws InvalidPhoneNumberException
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact = contactService.findContactByEmail("johnd@example.com");
//		contactService.saveContactPhone(new Phone("7775554433", null, null, contact));
//		contactService.saveContactPhone(new Phone("7775554433", null, null, contact));
//		contactService.saveContactPhone(new Phone("7775554413", null, null, contact));
//		contactService.saveContactPhone(new Phone("7775554433", null, null, contact));
//
//		List<Phone> phones = contactService.findContactPhoneByNumber("7775554433");
//
//		assertEquals(3, phones.size(), "Service test for finding phones by numbers");
//	}
//
//	@Test
//	public void findContactPhoneByIdTest() throws InvalidPhoneNumberException
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact = contactService.findContactByEmail("johnd@example.com");
//		contactService.saveContactPhone(new Phone("7775554433", null, null, contact));
//
//		Phone initialPhone = null;
//		List<Phone> phones = contactService.findContactPhoneByNumber("7775554433");
//		for (Phone next : phones)
//		{
//			initialPhone = next;
//		}
//
//		Phone foundPhone = contactService.findContactPhoneById(initialPhone.getPhoneId());
//		assertEquals(initialPhone, foundPhone, "Service test to find contact phone by id failed");
//	}
//
//	@Test
//	public void getNumberOfUsersTest()
//	{
//		assertEquals(0, authenticationService.findNumberOfUsers(), "Service test to find the number of users failed");
//	}
//
//	@Test
//	public void saveNewUserTest() throws NonExistingContactException
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact = contactService.findContactByEmail("johnd@example.com");
//
//		User user = authenticationService.saveUser(new User("username", "mypass", contact));
//		assertEquals(1, authenticationService.findNumberOfUsers(), "Service test to save a new user failed");
//		assertNotNull(user.getUserId(), "Service test to save a new user failed");
//	}
//	
//	@Test
//	public void updateUsernameTest() throws NonExistingContactException
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact = contactService.findContactByEmail("johnd@example.com");
//		User user = authenticationService.saveUser(new User("jdoe", "mypass", contact));
//		user.setUsername("john.doe");
//		
//		User updatedUser = authenticationService.updateUser(user.getUserId(), user);
//		assertEquals("john.doe", updatedUser.getUsername(), "Service test for updating user username failed");
//	}
//	
//	@Test
//	public void updateUserStatus() throws NonExistingContactException
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact = contactService.findContactByEmail("johnd@example.com");
//		User user = authenticationService.saveUser(new User("jdoe", "mypass", contact));
//		user.setEnabled((byte) 0);
//		
//		User updatedUser = authenticationService.updateUser(user.getUserId(), user);
//		assertEquals((byte) 0, updatedUser.getEnabled(), "Service test for updating user status failed");
//	}
//
//	@Test
//	public void findUserByUsernameTest() throws NonExistingContactException 
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact = contactService.findContactByEmail("johnd@example.com");
//		User user = new User("jdoe", "mypass", contact);
//		authenticationService.saveUser(user);
//
//		User founduser = authenticationService.findUserByUsername("jdoe");
//
//		assertEquals(user, founduser, "Service test for finding user by username failed");
//	}
//	
//	@Test
//	public void findUserByUserId() throws NonExistingContactException
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact = contactService.findContactByEmail("johnd@example.com");
//		User user = authenticationService.saveUser(new User("jdoe", "mypass", contact));
//		
//		User foundUser = authenticationService.findUserByUserId(user.getUserId());
//		assertEquals(user, foundUser, "Service test for finding user by userId failed");
//	}
//
//	@Test
//	public void getNumberOfRolesTest()
//	{
//		assertEquals(0, authenticationService.findNumberOfRoles(), "Service test for finding number of roles failed");
//	}
//
//	@Test
//	public void saveNewRoleTest() 
//	{
//		Role role = authenticationService.saveRole(new Role("Administrator"));
//		assertEquals(1, authenticationService.findNumberOfRoles(), "Service test for saving new role failed");
//		assertNotNull(role.getRoleId(), "Service test for saving new role failed");
//	}
//	
//	@Test
//	public void updateRoleTest()
//	{
//		Role role = authenticationService.saveRole(new Role("Administrator"));
//		Role newrole = new Role("Admins");
//		role = authenticationService.updateRole(role.getRoleId(), newrole);
//		
//		assertEquals("Admins", role.getRoleName(), "Service test for updating role has failed");
//		assertEquals(1, authenticationService.findNumberOfRoles(), "Service test for updating role has failed");
//	}
//	
//	@Test
//	public void findAllRolesTest()
//	{
//		authenticationService.saveRole(new Role("Administrator"));
//		authenticationService.saveRole(new Role("Manager"));
//		authenticationService.saveRole(new Role("Engineer"));
//		
//		assertEquals(3, authenticationService.findNumberOfRoles(), "Service test for finding all roles failed");
//	}
//	
//	@Test
//	public void findRoleByRoleIdTest()
//	{
//		Role initialrole = new Role("Administrator");
//		Role savedRole = authenticationService.saveRole(initialrole);
//		Role foundRole = authenticationService.findRoleById(savedRole.getRoleId());
//		
//		assertEquals(initialrole, foundRole, "Service test for finding role by roleId failed");
//	}
//
//	@Test
//	public void findRoleByRoleNameTest() 
//	{
//		Role initialrole = new Role("Administrator");
//		authenticationService.saveRole(initialrole);
//		Role foundrole = authenticationService.findRoleByRoleName("Administrator");
//
//		assertEquals(initialrole, foundrole, "Service test for finding role by roleName failed");
//	}
//
//	@Test
//	public void getNumberOfUser_RoleAssignments()
//	{
//		assertEquals(0, authenticationService.findNumberOfAssignments(),
//				"Service test for finding number of user-role assignments failed");
//	}
//
//	@Test
//	public void assignUserToRoleTest() throws NonExistingContactException 
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//
//		Contact contact = contactService.findContactByEmail("johnd@example.com");
//		User user = authenticationService.saveUser(new User("jdoe", "mypass", contact));
//		Role role = authenticationService.saveRole(new Role("Administrator"));
//		
//		assertFalse(user.getRoles().contains(role), "Service test for assigning a user to a role failed");
//		assertFalse(role.getMembers().contains(user), "Service test for assigning a user to a role failed");
//
//		authenticationService.assignUserToRole(role, user);
//
//		assertEquals(1, authenticationService.findNumberOfAssignments(), "Service test for assigning a user to a role failed");
//		assertTrue(user.getRoles().contains(role), "Service test for assigning a user to a role failed");
//		assertTrue(role.getMembers().contains(user), "Service test for assigning a user to a role failed");
//	}
//	
//	@Test
//	public void removeUserFromRole() throws NonExistingContactException 
//	{
//		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
//		Contact contact = contactService.findContactByEmail("johnd@example.com");
//		authenticationService.saveUser(new User("jdoe", "mypass", contact));
//		authenticationService.saveRole(new Role("Administrator"));
//
//		Role role = authenticationService.findRoleByRoleName("Administrator");
//		User user = authenticationService.findUserByUsername("jdoe");
//		authenticationService.assignUserToRole(role, user);
//		
//		authenticationService.removeUserFromRole(role, user);
//		assertEquals(0, authenticationService.findNumberOfAssignments(),
//				"Service test for removing a user from a role failed");
//	}

}
