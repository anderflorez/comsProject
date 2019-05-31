package com.unlimitedcompanies.coms.service.abacImpl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.ABACDao;
import com.unlimitedcompanies.coms.dao.security.AuthDao;
import com.unlimitedcompanies.coms.dao.security.ContactDao;
import com.unlimitedcompanies.coms.data.exceptions.DuplicatedResourcePolicyException;
import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.ComparisonOperator;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.domain.abac.ResourceField;
import com.unlimitedcompanies.coms.domain.abac.UserAttribs;
import com.unlimitedcompanies.coms.domain.abac.UserAttribute;
import com.unlimitedcompanies.coms.domain.employee.Employee;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.abac.SystemService;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;

@Service
@Transactional
public class SystemServiceImpl implements SystemService
{
	
	@Autowired
	private ABACDao abacDao;
	
	@Autowired
	private ContactDao contactDao;
	
	@Autowired
	private AuthDao authDao;
	
	
	/*
	 * Initial setup of resources for the security system
	 */
	
	@Override
	public void initialSetup() throws DuplicatedResourcePolicyException
	{
		// Check and make sure there are no risks by performing this operation
		// Get the number of records for several important resources
		// This code will run only for initial setup; it will run if and only if the app has never been used before		
		
		// This method will create an initial administrator contact, user and role
		// and then it will check all the entities and their fields available and save the lists in the db
		// with that information it will create the permissions for the administrator role
		
		int contacts = contactDao.getNumberOfContacts();
		int users = authDao.getNumberOfUsers();
		int roles = authDao.getNumberOfRoles();
		int permissions = abacDao.getNumberOfPolicies();
		
		if (users == 0 && roles == 0 && contacts == 0 && permissions == 0)
		{
			this.checkAllResources();
			
			Resource policyResource = abacDao.searchResourceByNameWithFieldsAndPolicy("AbacPolicy");
			AbacPolicy abacPolicy = new AbacPolicy("PolicyUpdate", PolicyType.UPDATE, policyResource);
			abacPolicy.setCdPolicy(true, false);
			abacPolicy.addEntityCondition(UserAttribute.USERNAME, ComparisonOperator.EQUALS, "administrator");
			abacDao.savePolicy(abacPolicy);
			
			authDao.createRole(new Role("Administrators"));
			Role adminRole = authDao.getRoleByName("Administrators", null);
			
			Contact initialContact = new Contact("Administrator", null, null, "uec_ops_support@unlimitedcompanies.com");
			contactDao.createContact(initialContact);
			Contact adminContact = contactDao.getContactByCharId(initialContact.getContactCharId(), null);
			
			PasswordEncoder pe = new BCryptPasswordEncoder();
			authDao.createUser(new User("administrator", pe.encode("uec123").toCharArray(), adminContact));
			User adminUser = authDao.getUserByUsername("administrator", null);
			
			authDao.assignUserToRole(adminUser.getUserId(), adminRole.getRoleId());
			
			// TODO: Remove the next print line
			System.out.println("Created Administrator contact, user and role");
		}
	}
	
	@Override
	public void checkAllResources()
	{
		abacDao.checkResourceList();
		abacDao.checkResourceFieldList();
	}

	@Override
	public List<String> searchAllResourceNames()
	{
		return abacDao.searchAllResourceNames();
	}
	
	
	/*
	 * Get objects with system rights which is without checking any permissions to obtain them
	 */
	
	@Override
	public AbacPolicy searchPolicy(Resource resource, PolicyType policyType) throws NoResourceAccessException
	{
		try
		{
			return abacDao.searchPolicy(resource, policyType, null);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public Role searchRoleById(int roleId) throws NoResourceAccessException
	{
		// TODO: This method has not been tested
		try
		{
			return authDao.getRoleById(roleId, null);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public Role roleWithAllRestrictedFields(Integer roleId) throws RecordNotFoundException
	{
		Role role = authDao.getRoleWithRestrictedFields(roleId);
		if (role == null)
		{
			throw new RecordNotFoundException("The role referenced by the id provided could not be found");
		}
		return role;
	}
	
	@Override
	public ResourceField searchResourceFieldById(int fieldId) throws NoResourceAccessException
	{
		// TODO: This method has not been tested
		try
		{
			return abacDao.searchResourceFieldById(fieldId);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public List<ResourceField> searchRestrictedFields(Resource resource, String signedUser)
	{
		return abacDao.getRestrictedFields();
	}
	
	@Override
	public List<String> searchRestrictedFieldNames(Resource resource, String signedUser)
	{
		
	}
	
	@Override
	public User searchFullUserByUsername(String username)
	{
		return authDao.getFullUserByUsername(username, null);
	}

	@Override
	public UserAttribs getUserAttribs(int userId)
	{
		User user = authDao.getFullUserWithAttribs(userId);

		UserAttribs userAttribs = new UserAttribs(user.getUsername());
		
		Employee employee = user.getContact().getEmployee();
		List<String> projectNames = new ArrayList<>();
		if (employee != null)
		{
			projectNames = employee.getAssociatedProjectNames();
		}

		userAttribs.setProjects(projectNames);
		userAttribs.setRoles(user.getRoleNames());
		return userAttribs;
	}

}
