package com.unlimitedcompanies.coms.service.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
import com.unlimitedcompanies.coms.domain.security.exen.UserStatus;
import com.unlimitedcompanies.coms.securityService.AuthService;
import com.unlimitedcompanies.coms.securityService.ContactService;
import com.unlimitedcompanies.coms.securityService.SecuritySetupService;
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
	AuthService authService;

	@Autowired
	SecuritySetupService setupService;

	@Test
	public void numberOfContactsIntegrationTest()
	{
		assertEquals(0, contactService.findNumberOfContacts(), "Number of contacts service test has failed");
	}

	@Test
	public void saveANewSimpleContactTest()
	{
		Contact contact = new Contact("John", null, "Doe", "john@example.com");
		contactService.saveContact(contact);
		assertEquals(1, contactService.findNumberOfContacts(),
				"Test for Saving a new contact from contact service failed");
	}

	@Test
	public void findContactByEmailTest()
	{
		contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"));

		assertNotNull(contactService.searchContactByEmail("john@example.com"),
				"Service test  to find contact by email failed");
	}

	@Test
	public void findContactByIdTest()
	{
		contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"));
		Contact initialContact = contactService.searchContactByEmail("john@example.com");

		Contact foundContact = contactService.searchContactById(initialContact.getContactId());

		assertEquals(initialContact, foundContact, "Service test to find contact by id failed");
	}

	@Test
	public void updateContactTest()
	{
		Contact initialContact = contactService.saveContact(new Contact("John", null, "Doe", "john@example.com"));
		Contact correctedContact = new Contact("Jane", null, "Roe", "jane@example.com");

		contactService.updateContact(initialContact.getContactId(), correctedContact);
		Contact foundContact = contactService.searchContactById(initialContact.getContactId());

		assertEquals(correctedContact, foundContact, "Service test for updating contact failed");
	}

	// @Test
	// public void deleteSingleContactTest()
	// {
	// contactService.saveContact(new Contact("John", null, "Doe",
	// "johnd@example.com"));
	// contactService.saveContact(new Contact("Jane", null, "Doe",
	// "janed@example.com"));
	//
	// Contact deleteContact =
	// contactService.searchContactByEmail("johnd@example.com");
	// contactService.deleteContact(deleteContact);
	//
	// assertEquals(1, contactService.findNumberOfContacts(), "Service test for
	// deleting a single contact failed");
	// }

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
		assertEquals(0, authService.findNumberOfUsers(), "Service test to find the number of users failed");
	}

	@Test
	public void saveNewUserTest() throws NonExistingContactException
	{
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		Contact contact = contactService.searchContactByEmail("johnd@example.com");

		User user = authService.saveUser(new User("username", "mypass", contact));
		assertEquals(1, authService.findNumberOfUsers(), "Service test to save a new user failed");
		assertNotNull(user.getUserId(), "Service test to save a new user failed");
	}

	@Test
	public void updateUsernameTest() throws NonExistingContactException
	{
		
		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		User user = authService.saveUser(new User("jdoe", "mypass", contact));
		user.setUsername("john.doe");

		User updatedUser = authService.updateUser(user.getUserId(), user);
		assertEquals("john.doe", updatedUser.getUsername(), "Service test for updating user username failed");
	}

	@Test
	 public void updateUserStatus() throws NonExistingContactException
	 {
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		Contact contact = contactService.searchContactByEmail("johnd@example.com");
		User user = authService.saveUser(new User("jdoe", "mypass", contact));
		user.setEnabled(0);
		
		User updatedUser = authService.updateUser(user.getUserId(), user);
		assertEquals(UserStatus.INACTIVE, updatedUser.getEnabledStatus(), "Service test for updating user status failed");
	 }
	
	@Test
	public void findAllUsersTest()
	{
		Contact contact1 = contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		Contact contact2 = contactService.saveContact(new Contact("Jane", null, "Doe", "janed@example.com"));
		Contact contact3 = contactService.saveContact(new Contact("Richard", null, "Doe", "rich@example.com"));

		try
		{
			authService.saveUser(new User("username1", "mypass", contact1));
			authService.saveUser(new User("username2", "mypass", contact2));
			authService.saveUser(new User("username3", "mypass", contact3));
		} catch (NonExistingContactException e)
		{
			e.printStackTrace();
		}
		
		assertEquals(3, authService.searchAllUsers().size(), "Find all users integration test failed");
	}
	
	@Test
	public void findUserByUsernameTest() throws NonExistingContactException
	{
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		Contact contact = contactService.searchContactByEmail("johnd@example.com");
		User user = new User("jdoe", "mypass", contact);
		authService.saveUser(user);

		User founduser = authService.searchUserByUsernameWithContact("jdoe");

		assertEquals(user, founduser, "Service test for finding user by username with contact failed");
	}

	@Test
	public void findUserByUsernameWithContactTest() throws NonExistingContactException
	{
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		Contact contact = contactService.searchContactByEmail("johnd@example.com");
		User user = new User("jdoe", "mypass", contact);
		authService.saveUser(user);

		User founduser = authService.searchUserByUsername("jdoe");

		assertEquals(user, founduser, "Service test for finding user by username failed");
		assertEquals(user.getContact(), contact, "Service test for finding user by username with contact failed");
	}

	@Test
	public void findUserByUserId() throws NonExistingContactException
	{
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		Contact contact = contactService.searchContactByEmail("johnd@example.com");
		User user = authService.saveUser(new User("jdoe", "mypass", contact));

		User foundUser = authService.searchUserByUserId(user.getUserId());
		assertEquals(user, foundUser, "Service test for finding user by userId failed");
	}

	@Test
	public void getNumberOfRolesTest()
	{
		assertEquals(0, authService.findNumberOfRoles(), "Service test for finding number of roles failed");
	}

	@Test
	public void saveNewRoleTest()
	{
		Role role = authService.saveRole(new Role("Administrator"));
		assertEquals(1, authService.findNumberOfRoles(), "Service test for saving new role failed");
		assertNotNull(role.getRoleId(), "Service test for saving new role failed");
	}

	@Test
	 public void updateRoleTest()
	 {
		Role role = authService.saveRole(new Role("Administrator"));
		Role newrole = new Role("Admins");
		role = authService.updateRole(role.getRoleId(), newrole);
		
		assertEquals("Admins", role.getRoleName(), "Service test for updating role has failed");
		assertEquals(1, authService.findNumberOfRoles(), "Service test for updating role has failed");
	 }

	 @Test
	 public void findAllRolesTest()
	 {
		 authService.saveRole(new Role("Administrator"));
		 authService.saveRole(new Role("Manager"));
		 authService.saveRole(new Role("Engineer"));
		
		 assertEquals(3, authService.findNumberOfRoles(), "Service test for finding all roles failed");
	 }
	
	 @Test
	 public void findRoleByRoleIdTest()
	 {
		 Role initialrole = new Role("Administrator");
		 Role savedRole = authService.saveRole(initialrole);
		 Role foundRole = authService.searchRoleById(savedRole.getRoleId());
		
		 assertEquals(initialrole, foundRole, "Service test for finding role by roleId failed");
	 }

	@Test
	public void findRoleByRoleNameTest()
	{
		Role initialrole = new Role("Administrator");
		authService.saveRole(initialrole);
		Role foundrole = authService.findRoleByRoleName("Administrator");

		assertEquals(initialrole, foundrole, "Service test for finding role by roleName failed");
	}

	@Test
	public void assignUserToRoleTest() throws NonExistingContactException
	{
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));

		Contact contact = contactService.searchContactByEmail("johnd@example.com");
		User user = authService.saveUser(new User("jdoe", "mypass", contact));
		Role role = authService.saveRole(new Role("Administrator"));

		assertFalse(user.getRoles().contains(role), "Service test for assigning a user to a role failed");
		assertFalse(role.getMembers().contains(user), "Service test for assigning a user to a role failed");

		authService.assignUserToRole(user, role);

		assertTrue(user.getRoles().contains(role), "Service test for assigning a user to a role failed");
		assertTrue(role.getMembers().contains(user), "Service test for assigning a user to a role failed");
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
	public void findUserWithContactAndRoles() throws NonExistingContactException
	{
		contactService.saveContact(new Contact("John", null, "Doe", "johnd@example.com"));
		Contact contact = contactService.searchContactByEmail("johnd@example.com");
		User user = new User("jdoe", "mypass", contact);
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

		ResourcePermissions permission = new ResourcePermissions(role, resource, true, true, true, false, andGroup);
		authService.savePermission(permission);

		List<ResourcePermissions> foundPermissions = authService.searchAllRolePermissions(role);
		assertEquals(1, foundPermissions.size(), "Save permission with contact integration test failed");

		for (ResourcePermissions rp : foundPermissions)
		{
			assertEquals(resource.getResourceName(), rp.getResource().getResourceName(),
					"Save permission with contact integration test failed");
			assertTrue(rp.getAndGroup().getConditions().contains(condition2),
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

		ResourcePermissions permission = new ResourcePermissions(role, resource, true, true, true, false, andGroup1);
		permission = authService.savePermission(permission);

		assertEquals(permission.getAndGroup().getAndGroupId(),
				authService.searchPermissionById(permission.getPermissionId()).getAndGroup().getAndGroupId(),
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

		ResourcePermissions permission = new ResourcePermissions(role, resource, true, true, true, false, andGroup1);
		permission = authService.savePermission(permission);

		assertEquals(permission.getAndGroup().getAndGroupId(),
				authService.searchPermissionById(permission.getPermissionId()).getAndGroup().getAndGroupId(),
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
		ResourcePermissions permission = new ResourcePermissions(role, resource, true, true, true, false, andGroup1);
		authService.savePermission(permission);

		ResourcePermissions foundPermission = authService.searchPermissionById(permission.getPermissionId());
		assertEquals(permission.getAndGroup(), foundPermission.getAndGroup());
		assertEquals(permission.getAndGroup().getOrGroups(), foundPermission.getAndGroup().getOrGroups());
		if (foundPermission.getAndGroup().getOrGroups().get(0) == orGroup2)
		{
			assertTrue(foundPermission.getAndGroup().getOrGroups().get(0).getAndGroups().contains(andGroup4));
		} else
		{
			assertTrue(foundPermission.getAndGroup().getOrGroups().get(1).getAndGroups().contains(andGroup4));
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
