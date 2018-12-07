package com.unlimitedcompanies.coms.service.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.domain.search.Operator;
import com.unlimitedcompanies.coms.domain.security.AndCondition;
import com.unlimitedcompanies.coms.domain.security.AndGroup;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.OrCondition;
import com.unlimitedcompanies.coms.domain.security.OrGroup;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.ResourceField;
import com.unlimitedcompanies.coms.domain.security.ResourcePermissions;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotCreatedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotDeletedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.AuthService;
import com.unlimitedcompanies.coms.service.security.ContactService;
import com.unlimitedcompanies.coms.service.security.SecuritySetupService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
@Transactional
@ActiveProfiles("integrationTesting")
class SecurityServiceIntegrationTest
{
	@Autowired
	ContactService contactService;

	@Autowired
	AuthService authService;

	@Autowired
	SecuritySetupService setupService;

	@Test
	public void numberOfContactsIntegrationTest()
	{
		assertEquals(0, contactService.findNumberOfContacts(), "Number of contacts service test has failed");
	}
	
	@Test
	public void numberOfContactsInARangeTest() throws DuplicateRecordException
	{
		contactService.saveContact(new Contact("fernando", null, null, "fernando@example.com"));
		contactService.saveContact(new Contact("Diane", null, null, "Diane@example.com"));
		contactService.saveContact(new Contact("Bella", null, null, "bella@example.com"));
		contactService.saveContact(new Contact("Ann", null, null, "ann@example.com"));
		contactService.saveContact(new Contact("Ella", null, null, "ella@example.com"));
		contactService.saveContact(new Contact("Catherine", null, null, "catherine@example.com"));
		
		assertTrue(contactService.hasNextContact(3, 2));
		assertFalse(contactService.hasNextContact(4, 2));
		assertFalse(contactService.hasNextContact(3, 3));
	}

	@Test
	public void saveANewSimpleContactTest() throws DuplicateRecordException
	{
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		contactService.saveContact(contact);
		assertEquals(1, contactService.findNumberOfContacts(),
				"Test for Saving a new contact from contact service failed");
	}
	
	@Test
	public void saveContactWithRepeatedEmailNotAllowedTest() throws DuplicateRecordException
	{
		Contact contact1 = new Contact("John", null, "Doe", "john@example.com");
		Contact contact2 = new Contact("Johnny", "J", "Roe", "john@example.com");
		contactService.saveContact(contact1);
		assertThrows(DuplicateRecordException.class, () -> contactService.saveContact(contact2));
	}
	
	@Test
	public void findAllContactsByPagesTest() throws DuplicateRecordException
	{
		
		contactService.saveContact(new Contact("fernando", null, null, "fernando@example.com"));
		Contact diane = contactService.saveContact(new Contact("Diane", null, null, "Diane@example.com"));
		contactService.saveContact(new Contact("Bella", null, null, "bella@example.com"));
		Contact ann = contactService.saveContact(new Contact("Ann", null, null, "ann@example.com"));
		Contact ella = contactService.saveContact(new Contact("Ella", null, null, "ella@example.com"));
		contactService.saveContact(new Contact("Catherine", null, null, "catherine@example.com"));
		contactService.saveContact(new Contact("marcela", null, null, "marcela@example.com"));

		assertEquals(ann.getContactCharId(), contactService.searchContactsByRange(1, 2).get(0).getContactCharId());
		assertEquals(ella.getContactCharId(), contactService.searchContactsByRange(3, 2).get(0).getContactCharId());
		assertEquals(diane.getContactCharId(), contactService.searchContactsByRange(2, 3).get(0).getContactCharId());
		assertEquals(ella.getContactCharId(), contactService.searchContactsByRange(2, 3).get(1).getContactCharId());
	}

	@Test
	public void findContactByEmailTest() throws DuplicateRecordException
	{
		contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"));

		assertNotNull(contactService.searchContactByEmail("john@example.com"),
				"Service test  to find contact by email failed");
	}

	@Test
	public void findContactByIdTest() throws DuplicateRecordException, RecordNotFoundException
	{
		contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"));
		Contact initialContact = contactService.searchContactByEmail("john@example.com");

		Contact foundContact = contactService.searchContactById(initialContact.getContactId());

		assertEquals(initialContact, foundContact, "Service test to find contact by id failed");
	}

	@Test
	public void updateContactTest() throws DuplicateRecordException, RecordNotFoundException
	{
		Contact initialContact = contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"));
		Contact correctedContact = new Contact(initialContact);
		correctedContact.setFirstName("Jane");
		correctedContact.setEmail("jane@example.com");

		Contact foundContact = contactService.updateContact(correctedContact);

		assertEquals(correctedContact, foundContact, "Service test for updating contact failed");
	}

	 @Test
	 public void deleteSingleContactTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotDeletedException
	 {
		 contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		 contactService.saveContact(new Contact("Jane", null, "Doe", "janed@example.com"));
		
		 Contact deleteContact = contactService.searchContactByEmail("johnd@example.com");
		 contactService.deleteContact(deleteContact.getContactId());
		
		 assertEquals(1, contactService.findNumberOfContacts(), "Service test for deleting a single contact failed");
	 }

	// @Test
	// public void numberOfAddressesTest()
	// {
	// assertEquals(0, contactService.findNumberOfContactAddresses(),
	// "Service test to get the number of addresses failed");
	// }
	//
	// @Test
	// public void saveContactAddressTest()
	// {
	// contactService.saveContact(new Contact("John", null, "Doe",
	// "johnd@example.com"));
	// Contact contact = contactService.findContactByEmail("johnd@example.com");
	//
	// contactService.saveContactAddress(new Address("0000 MyStreet Dr", "MyCity",
	// "FL", "00001", contact));
	//
	// assertEquals(1, contactService.findNumberOfContactAddresses(), "Service test
	// for saving new contact address failed");
	// }
	//
	// @Test
	// public void findAddressByZipCodeTest()
	// {
	// contactService.saveContact(new Contact("John", null, "Doe",
	// "johnd@example.com"));
	// contactService.saveContact(new Contact("Jane", null, "Doe",
	// "janed@example.com"));
	// contactService.saveContact(new Contact("Robert", null, "Roe",
	// "rroe@example.com"));
	//
	// Contact contact1 = contactService.findContactByEmail("johnd@example.com");
	// Contact contact2 = contactService.findContactByEmail("janed@example.com");
	// Contact contact3 = contactService.findContactByEmail("rroe@example.com");
	//
	// contactService.saveContactAddress(new Address("0000 MyStreet Dr", "MyCity",
	// "FL", "00001", contact1));
	// contactService.saveContactAddress(new Address("1111 YourStreet Dr", "MyCity",
	// "FL", "00011", contact2));
	// contactService.saveContactAddress(new Address("2222 HisStreet Dr", "MyCity",
	// "FL", "00001", contact3));
	//
	// List<Address> addresses =
	// contactService.findContactAddressesByZipCode("00001");
	//
	// assertEquals(2, addresses.size(), "Service test for finding address by zip
	// code failed");
	// }
	//
	// @Test
	// public void findContactAddressByIdTest()
	// {
	// contactService.saveContact(new Contact("John", null, "Doe",
	// "johnd@example.com"));
	// Contact contact = contactService.findContactByEmail("johnd@example.com");
	// Address initialAddress = new Address("0000 MyStreet Dr", "MyCity", "FL",
	// "00001", contact);
	// contactService.saveContactAddress(initialAddress);
	//
	// List<Address> addresses =
	// contactService.findContactAddressesByZipCode("00001");
	// Address foundAddress = null;
	// for (Address next : addresses)
	// {
	// foundAddress = next;
	// }
	//
	// assertEquals(initialAddress, foundAddress, "Service test for finding contact
	// address by id failed");
	// }
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
	@Test
	public void getNumberOfUsersTest()
	{
		assertEquals(0, authService.searchNumberOfUsers(), "Service test to find the number of users failed");
	}

	@Test
	public void saveNewUserTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
	{
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		Contact contact = contactService.searchContactByEmail("johnd@example.com");

		User user = authService.saveUser(new User("username", "mypass".toCharArray(), contact));
		assertEquals(1, authService.searchNumberOfUsers(), "Service test to save a new user failed");
		assertNotNull(user.getUserId(), "Service test to save a new user failed");
	}
	
	@Test
	public void findAllUsersTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
	{
		Contact contact1 = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		Contact contact2 = contactService.saveContact(new Contact("Jane", null, "Doe", "janed@example.com"));
		Contact contact3 = contactService.saveContact(new Contact("Richard", null, "Doe", "rich@example.com"));

		authService.saveUser(new User("username1", "mypass".toCharArray(), contact1));
		User user = new User("username2", "mypass".toCharArray(), contact2);
		user.setEnabled(false);
		authService.saveUser(user);
		authService.saveUser(new User("username3", "mypass".toCharArray(), contact3));
		
		assertEquals(3, authService.searchAllUsers().size(), "Find all users integration test failed");
	}
	
	@Test
	public void findUsersByPagesTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
	{
		
		Contact fernando = contactService.saveContact(new Contact("fernando", null, null, "fernando@example.com"));
		Contact diane = contactService.saveContact(new Contact("Diane", null, null, "Diane@example.com"));
		Contact bella = contactService.saveContact(new Contact("Bella", null, null, "bella@example.com"));
		Contact ann = contactService.saveContact(new Contact("Ann", null, null, "ann@example.com"));
		Contact ella = contactService.saveContact(new Contact("Ella", null, null, "ella@example.com"));
		Contact catherine = contactService.saveContact(new Contact("Catherine", null, null, "catherine@example.com"));
		Contact marcela = contactService.saveContact(new Contact("marcela", null, null, "marcela@example.com"));
		
		authService.saveUser(new User("username1", "mypass".toCharArray(), fernando));
		authService.saveUser(new User("username2", "mypass".toCharArray(), diane));
		User user3 = authService.saveUser(new User("username3", "mypass".toCharArray(), bella));
		User user4 = authService.saveUser(new User("username4", "mypass".toCharArray(), ann));
		authService.saveUser(new User("username5", "mypass".toCharArray(), ella));
		authService.saveUser(new User("username6", "mypass".toCharArray(), catherine));
		User user7 = authService.saveUser(new User("username7", "mypass".toCharArray(), marcela));

		assertEquals(user4.getUsername(), authService.searchUsersByRange(2, 3).get(0).getUsername());
		assertEquals(user3.getUsername(), authService.searchUsersByRange(1, 3).get(2).getUsername());
		assertEquals(user7.getUsername(), authService.searchUsersByRange(4, 2).get(0).getUsername());
	}
	
	@Test
	public void findUserByUserId() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
	{
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		Contact contact = contactService.searchContactByEmail("johnd@example.com");
		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));
		
		User foundUser = authService.searchUserByUserId(user.getUserId());
		assertEquals(user, foundUser, "Service test for finding user by userId failed");
	}
	
	@Test
	public void findUserByUsernameTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
	{
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		Contact contact = contactService.searchContactByEmail("johnd@example.com");
		User user = new User("jdoe", "mypass".toCharArray(), contact);
		authService.saveUser(user);

		User founduser = authService.searchUserByUsernameWithContact("jdoe");

		assertEquals(user, founduser, "Service test for finding user by username with contact failed");
	}
	
	@Test
	public void findUserByContact() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
	{
		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));
		
		User foundUser = authService.searchUserByContact(contact);
		assertEquals(user.getUserId(), foundUser.getUserId(), "Service test for finding user by contact failed");
	}

	@Test
	public void findUserByUsernameWithContactTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
	{
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		Contact contact = contactService.searchContactByEmail("johnd@example.com");
		User user = new User("jdoe", "mypass".toCharArray(), contact);
		authService.saveUser(user);

		User founduser = authService.searchUserByUsernameWithContact("jdoe");

		assertEquals(user, founduser, "Service test for finding user by username failed");
		assertEquals(user.getContact(), founduser.getContact(), "Service test for finding user by username with contact failed");
	}
	
	@Test
	public void findUserRolesTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
	{
		Role role1 = authService.saveRole(new Role("Administrator"));
		Role role2 = authService.saveRole(new Role("Manager"));
		
		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		User user = authService.saveUser(new User("username", "mypass".toCharArray(), contact));
		
		authService.assignUserToRole(user, role1);
		authService.assignUserToRole(user, role2);
		
		User foundUser = authService.searchFullUserByUsername(user.getUsername());
		assertEquals(2, foundUser.getRoles().size(), "Service test for finding user role list failed");
	}
	
	@Test
	public void updateUsernameTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
	{
		
		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));
		user.setUsername("john.doe");
		User updatedUser = authService.updateUser(user);		
		assertEquals("john.doe", updatedUser.getUsername(), "Service test for updating user username failed");
	}
	
	@Test
	public void updateUserStatus() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
	{
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		Contact contact = contactService.searchContactByEmail("johnd@example.com");
		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));
		user.setEnabled(false);
		
		User updatedUser = authService.updateUser(user);
		assertFalse(updatedUser.isEnabled(), "Service test for updating user status failed");
	}
	
	 @Test
	 public void deleteSingleUserTest() throws DuplicateRecordException, RecordNotFoundException, 
	 										   RecordNotCreatedException, RecordNotDeletedException
	 {
		 Contact contact1 = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		 Contact contact2 = contactService.saveContact(new Contact("Jane", null, "Doe", "janed@example.com"));
		 
		 User user = authService.saveUser(new User("johnd", "mypass".toCharArray(), contact1));
		 authService.saveUser(new User("janed", "mypass".toCharArray(), contact2));
		
		 authService.deleteUser(user.getUserId());
		
		 assertEquals(1, authService.searchNumberOfUsers(), "Service test for deleting a single user failed");
	 }

	@Test
	public void getNumberOfRolesTest()
	{
		assertEquals(0, authService.searchNumberOfRoles(), "Service test for finding number of roles failed");
	}

	@Test
	public void saveNewRoleTest()
	{
		Role role = authService.saveRole(new Role("Administrator"));
		assertEquals(1, authService.searchNumberOfRoles(), "Service test for saving new role failed");
		assertNotNull(role.getRoleId(), "Service test for saving new role failed");
	}
	
	@Test
	public void findAllRolesTest()
	{
		authService.saveRole(new Role("Administrator"));
		authService.saveRole(new Role("Manager"));
		authService.saveRole(new Role("Engineer"));
		
		assertEquals(3, authService.searchNumberOfRoles(), "Service test for finding all roles failed");
	}
	
	@Test
	public void findRoleByRoleIdTest()
	{
		Role initialrole = new Role("Administrator");
		System.out.println(initialrole.getRoleId());
		Role savedRole = authService.saveRole(initialrole);
		System.out.println("Searching for role with id: " + savedRole.getRoleId());
		Role foundRole = authService.searchRoleById(savedRole.getRoleId());
		System.out.println("found Role: " + foundRole.getRoleId());
		
		assertEquals(initialrole, foundRole, "Service test for finding role by roleId failed");
	}
	
	@Test
	public void findRoleByRoleNameTest()
	{
		Role initialrole = new Role("Administrator");
		authService.saveRole(initialrole);
		Role foundrole = authService.searchRoleByRoleName("Administrator");
		
		assertEquals(initialrole, foundrole, "Service test for finding role by roleName failed");
	}
	
	@Test
	public void findRoleByIdWithMembers() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
	{
		Role role = authService.saveRole(new Role("Administrator"));
		Contact contact1 = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		Contact contact2 = contactService.saveContact(new Contact("Jane", null, "Doe", "janed@example.com"));
		Contact contact3 = contactService.saveContact(new Contact("Richard", null, "Roe", "richd@example.com"));
		
		User user1 = authService.saveUser(new User("johnd", "pass".toCharArray(), contact1));
		User user2 = authService.saveUser(new User("janed", "pass".toCharArray(), contact2));
		User user3 = authService.saveUser(new User("richd", "pass".toCharArray(), contact3));
		
		authService.assignUserToRole(user1, role);
		authService.assignUserToRole(user2, role);
		authService.assignUserToRole(user3, role);
		
		Role foundRole = authService.searchRoleByIdWithMembers(role.getRoleId());
		assertEquals(3, foundRole.getMembers().size(), "Service test for finding a role with its members failed");
	}

	@Test
	 public void updateRoleTest()
	 {
		Role role = authService.saveRole(new Role("Administrator"));
		Role newrole = new Role("Admins");
		role = authService.updateRole(role.getRoleId(), newrole);
		
		assertEquals("Admins", role.getRoleName(), "Service test for updating role has failed");
		assertEquals(1, authService.searchNumberOfRoles(), "Service test for updating role has failed");
	 }

	@Test
	public void assignUserToRoleTest() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
	{
		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		User user = authService.saveUser(new User("jdoe", "mypass".toCharArray(), contact));

		Role role = authService.saveRole(new Role("Administrator"));

		assertFalse(user.getRoles().contains(role), "Service test for assigning a user to a role failed");
		assertFalse(role.getMembers().contains(user), "Service test for assigning a user to a role failed");

		authService.assignUserToRole(user, role);
		
		User checkUser = authService.searchFullUserByUserId(user.getUserId());
		Role checkRole = authService.searchRoleById(role.getRoleId());

		assertTrue(checkUser.getRoles().contains(role), "Service test for assigning a user to a role failed");
		assertTrue(checkRole.getMembers().contains(user), "Service test for assigning a user to a role failed");
	}

	// @Test
	// public void removeUserFromRole() throws NonExistingContactException
	// {
	// contactService.saveContact(new Contact("John", null, "Doe",
	// "johnd@example.com"));
	// Contact contact = contactService.findContactByEmail("johnd@example.com");
	// authService.saveUser(new User("jdoe", "mypass", contact));
	// authService.saveRole(new Role("Administrator"));
	//
	// Role role = authService.findRoleByRoleName("Administrator");
	// User user = authService.findUserByUsername("jdoe");
	// authService.assignUserToRole(role, user);
	//
	// authService.removeUserFromRole(role, user);
	// assertEquals(0, authService.findNumberOfAssignments(),
	// "Service test for removing a user from a role failed");
	// }

	@Test
	public void findUserWithContactAndRoles() throws DuplicateRecordException, RecordNotFoundException, RecordNotCreatedException
	{
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		Contact contact = contactService.searchContactByEmail("johnd@example.com");
		User user = new User("jdoe", "mypass".toCharArray(), contact);
		authService.saveUser(user);
		Role role1 = authService.saveRole(new Role("Administrator"));
		Role role2 = authService.saveRole(new Role("Manager"));

		User founduser = authService.searchFullUserByUsername("jdoe");
		founduser.addRole(role1);
		founduser.addRole(role2);

		User foundUser = authService.searchFullUserByUsername("jdoe");

		assertEquals(user, founduser, "Service test for finding user by username failed");
		assertEquals(user.getContact(), contact, "Service test for finding user by username with contact failed");
		assertEquals(2, foundUser.getRoles().size(), "Service test for finding user by username with contact failed");
	}

	@Test
	public void checkResourcesTest()
	{
		setupService.checkAllResources();
		List<String> resources = setupService.findAllResourceNames();
		List<ResourceField> resourceFields = setupService.findAllResourceFieldsWithResources();
		Set<String> resourceFromFields = new HashSet<>();
		for (ResourceField rf : resourceFields)
		{
			resourceFromFields.add(rf.getResource().getResourceName());
		}
		assertTrue(resources.size() > 0);
		assertEquals(resources.size(), resourceFromFields.size(), "Checking resources integration test failed");
	}

	@Test
	public void findAResourceTest()
	{
		setupService.checkAllResources();
		Resource resource = setupService.findResourceByName("Contact");
		assertEquals("Contact", resource.getResourceName(), "Find resource integration test failed");
	}

	@Test
	void saveNewPermission()
	{
		setupService.checkAllResources();
		Role role = authService.saveRole(new Role("Administrator"));
		Resource resource = setupService.findResourceByName("Contact");
		ResourcePermissions permission = new ResourcePermissions(role, resource, true, true, true, false);
		authService.savePermission(permission);
		List<ResourcePermissions> foundPermissions = authService.searchAllRolePermissions(role);

		assertTrue(foundPermissions.size() > 0, "Save permissions integration test failed");
	}

	@Test
	void saveNewPermissionWithConditionsTest()
	{
		setupService.checkAllResources();
		Role role = authService.saveRole(new Role("Administrator"));
		Resource resource = setupService.findResourceByName("Contact");
		AndGroup andGroup = authService.saveAndGroup(new AndGroup());
		AndCondition condition1 = new AndCondition("firstName", "John", Operator.EQUALS);
		AndCondition condition2 = new AndCondition("email", "johnd@example.com", Operator.EQUALS);
		andGroup.addAndConditionBidirectional(condition1);
		andGroup.addAndConditionBidirectional(condition2);

		ResourcePermissions permission = new ResourcePermissions(role, resource, true, true, true, false);
		permission.setViewCondtitions(andGroup);
		authService.savePermission(permission);

		List<ResourcePermissions> foundPermissions = authService.searchAllRolePermissions(role);
		assertEquals(1, foundPermissions.size(), "Save permission with contact integration test failed");

		for (ResourcePermissions rp : foundPermissions)
		{
			assertEquals(resource.getResourceName(), rp.getResource().getResourceName(),
					"Save permission with contact integration test failed");
			assertTrue(rp.getViewCondtitions().getConditions().contains(condition2),
					"Save permission with contact integration test failed");
		}

	}

	@Test
	void savePermissionWithChainedIndividualConditionsTest()
	{
		setupService.checkAllResources();
		Role role = authService.saveRole(new Role("Administrator"));
		Resource resource = setupService.findResourceByName("Contact");

		AndGroup andGroup1 = authService.saveAndGroup(new AndGroup());
		AndCondition andCondition1 = new AndCondition("firstName", "John", Operator.EQUALS);
		AndCondition andCondition2 = new AndCondition("email", "johnd@example.com", Operator.EQUALS);
		andGroup1.addAndConditionBidirectional(andCondition1);
		andGroup1.addAndConditionBidirectional(andCondition2);

		OrGroup orGroup2 = authService.saveOrGroup(new OrGroup());
		OrCondition orCondition1 = new OrCondition("roleName", "Administrator", Operator.NOT_EQUAL);
		orGroup2.addOrConditionBidirectional(orCondition1);

		OrGroup orGroup3 = authService.saveOrGroup(new OrGroup());
		OrCondition orCondition2 = new OrCondition("userId", "5", Operator.LESS_THAN);
		orGroup3.addOrConditionBidirectional(orCondition2);

		AndGroup andGroup4 = authService.saveAndGroup(new AndGroup());
		AndCondition andCondition3 = new AndCondition("contactId", "2", Operator.GRATER_THAN);
		andGroup4.addAndConditionBidirectional(andCondition3);

		andGroup1.addOrGroup(orGroup2);
		andGroup1.addOrGroup(orGroup3);
		orGroup2.addAndGroup(andGroup4);

		ResourcePermissions permission = new ResourcePermissions(role, resource, true, true, true, false);
		permission.setViewCondtitions(andGroup1);
		permission = authService.savePermission(permission);

		assertEquals(permission.getViewCondtitions().getAndGroupId(),
				authService.searchPermissionById(permission.getPermissionId()).getViewCondtitions().getAndGroupId(),
				"Save permission with chained conditions integration test failed");

		assertTrue(authService.searchOrGroupById(orGroup2.getOrGroupId()).getConditions().size() > 0);
		assertTrue(authService.searchOrGroupById(orGroup3.getOrGroupId()).getConditions().size() > 0);
		assertTrue(authService.searchOrGroupById(orGroup2.getOrGroupId()).getAndGroups().contains(andGroup4));

	}

	@Test
	public void saveFullPermissionTest()
	{
		setupService.checkAllResources();
		Role role = authService.saveRole(new Role("Administrator"));
		Resource resource = setupService.findResourceByName("Contact");

		AndGroup andGroup1 = new AndGroup();
		AndCondition andCondition1 = new AndCondition("firstName", "John", Operator.EQUALS);
		AndCondition andCondition2 = new AndCondition("email", "johnd@example.com", Operator.EQUALS);
		andGroup1.addAndConditionBidirectional(andCondition1);
		andGroup1.addAndConditionBidirectional(andCondition2);

		OrGroup orGroup2 = new OrGroup();
		OrCondition orCondition1 = new OrCondition("roleName", "Administrator", Operator.NOT_EQUAL);
		orGroup2.addOrConditionBidirectional(orCondition1);

		OrGroup orGroup3 = new OrGroup();
		OrCondition orCondition2 = new OrCondition("userId", "5", Operator.LESS_THAN);
		orGroup3.addOrConditionBidirectional(orCondition2);

		AndGroup andGroup4 = new AndGroup();
		AndCondition andCondition3 = new AndCondition("contactId", "2", Operator.GRATER_THAN);
		andGroup4.addAndConditionBidirectional(andCondition3);

		andGroup1.addOrGroup(orGroup2);
		andGroup1.addOrGroup(orGroup3);
		orGroup2.addAndGroup(andGroup4);

		ResourcePermissions permission = new ResourcePermissions(role, resource, true, true, true, false);
		permission.setViewCondtitions(andGroup1);
		permission = authService.savePermission(permission);

		assertEquals(permission.getViewCondtitions().getAndGroupId(),
				authService.searchPermissionById(permission.getPermissionId()).getViewCondtitions().getAndGroupId(),
				"Save permission with chained conditions integration test failed");

		assertTrue(authService.searchOrGroupById(orGroup2.getOrGroupId()).getConditions().size() > 0,
				"Save permission with chained conditions integration test failed");
		assertTrue(authService.searchOrGroupById(orGroup3.getOrGroupId()).getConditions().size() > 0,
				"Save permission with chained conditions integration test failed");
		assertTrue(authService.searchOrGroupById(orGroup2.getOrGroupId()).getAndGroups().contains(andGroup4),
				"Save permission with chained conditions integration test failed");
	}

	@Test
	public void retrieveFullPermissionWithAllConditionsTest()
	{
		setupService.checkAllResources();
		Role role = authService.saveRole(new Role("Administrator"));
		Resource resource = setupService.findResourceByName("Contact");
		AndGroup andGroup1 = new AndGroup();
		AndCondition andCondition1 = new AndCondition("firstName", "John", Operator.EQUALS);
		AndCondition andCondition2 = new AndCondition("email", "johnd@example.com", Operator.EQUALS);
		andGroup1.addAndConditionBidirectional(andCondition1);
		andGroup1.addAndConditionBidirectional(andCondition2);
		OrGroup orGroup2 = new OrGroup();
		OrCondition orCondition1 = new OrCondition("roleName", "Administrator", Operator.NOT_EQUAL);
		orGroup2.addOrConditionBidirectional(orCondition1);
		OrGroup orGroup3 = new OrGroup();
		OrCondition orCondition2 = new OrCondition("userId", "5", Operator.LESS_THAN);
		orGroup3.addOrConditionBidirectional(orCondition2);
		AndGroup andGroup4 = new AndGroup();
		AndCondition andCondition3 = new AndCondition("contactId", "2", Operator.GRATER_THAN);
		andGroup4.addAndConditionBidirectional(andCondition3);
		andGroup1.addOrGroup(orGroup2);
		andGroup1.addOrGroup(orGroup3);
		orGroup2.addAndGroup(andGroup4);
		ResourcePermissions permission = new ResourcePermissions(role, resource, true, true, true, false);
		permission.setViewCondtitions(andGroup1);
		authService.savePermission(permission);

		ResourcePermissions foundPermission = authService.searchPermissionById(permission.getPermissionId());
		assertEquals(permission.getViewCondtitions(), foundPermission.getViewCondtitions());
		assertEquals(permission.getViewCondtitions().getOrGroups(), foundPermission.getViewCondtitions().getOrGroups());
		if (foundPermission.getViewCondtitions().getOrGroups().get(0) == orGroup2)
		{
			assertTrue(foundPermission.getViewCondtitions().getOrGroups().get(0).getAndGroups().contains(andGroup4));
		} else
		{
			assertTrue(foundPermission.getViewCondtitions().getOrGroups().get(1).getAndGroups().contains(andGroup4));
		}
	}

	@Test
	public void saveAndGroupTest()
	{
		AndGroup conditionGroup = new AndGroup();
		authService.saveAndGroup(conditionGroup);
		AndGroup foundCondition = authService.searchAndGroupById(conditionGroup.getAndGroupId());

		assertEquals(conditionGroup, foundCondition, "Save andGroup test failed");
	}

	@Test
	public void saveAndGroupWithAndConditionTest()
	{
		AndGroup andGroup = new AndGroup();

		AndCondition andCondition1 = new AndCondition("firstName", "John", Operator.EQUALS);
		AndCondition andCondition2 = new AndCondition("email", "johnd@example.com", Operator.EQUALS);
		andCondition1.assignToGroupBidirectional(andGroup);
		andCondition2.assignToGroupBidirectional(andGroup);

		authService.saveAndGroup(andGroup);

		AndGroup foundAndGroupWithConditions = authService.searchAndGroupById(andGroup.getAndGroupId());

		for (AndCondition c : foundAndGroupWithConditions.getConditions())
		{
			assertTrue(c.getAndGroup().equals(andGroup), "Save AndGroup with conditions test for conditions failed");
		}
		assertTrue(foundAndGroupWithConditions.getConditions().size() > 0,
				"Save AndGroup with conditions test for group failed");
	}

	@Test
	public void saveOrGroupWithOrConditionTest()
	{
		OrGroup orGroup = new OrGroup();

		OrCondition orCondition1 = new OrCondition("firstName", "John", Operator.EQUALS);
		OrCondition orCondition2 = new OrCondition("email", "johnd@example.com", Operator.EQUALS);
		orCondition1.assignToGroupBidirectional(orGroup);
		orCondition2.assignToGroupBidirectional(orGroup);

		authService.saveOrGroup(orGroup);

		OrGroup foundOrGroup = authService.searchOrGroupById(orGroup.getOrGroupId());

		for (OrCondition condition : foundOrGroup.getConditions())
		{
			assertTrue(condition.getOrGroup().equals(foundOrGroup),
					"Save OrGroup with conditions test for conditions failed");
		}
		assertEquals(2, foundOrGroup.getConditions().size(), "Save OrGroup with conditions test for group failed");
	}

}