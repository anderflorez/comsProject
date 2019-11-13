package com.unlimitedcompanies.coms.service.systemImpl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.ABACDao;
import com.unlimitedcompanies.coms.dao.security.AuthDao;
import com.unlimitedcompanies.coms.dao.security.ContactDao;
import com.unlimitedcompanies.coms.dao.system.SystemDao;
import com.unlimitedcompanies.coms.data.exceptions.InvalidPolicyException;
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
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.system.SystemService;

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
	
	@Autowired
	private SystemDao systemDao;
	
	/*
	 * Initial setup of resources for the security system
	 */
	@Override
	public void initialSetup() throws InvalidPolicyException
	{
		/** TODO: Check and make sure there are no risks by performing this operation
		* Get the number of records for several important resources
		* This code will run only for initial setup; it will run if and only if the app has never been used before		
		*/
		
		/** This method will check all the entities with their fields available and it will 
		 * register them in the database. Having that information, it will create policies to allow
		 * the role "Administrators" CRUD access to policies, contacts, users, and roles.
		 * After that setting is done, it will create the actual "Administrators" role as well as a contact
		 * and user with the names of "Administrator" and "administrator" respectively assigning it to 
		 * the "Administrators" role.
		 */
		
		int contacts = contactDao.getNumberOfContacts();
		int users = authDao.getNumberOfUsers();
		int roles = authDao.getNumberOfRoles();
		int permissions = abacDao.getNumberOfPolicies();
		
		if (users == 0 && roles == 0 && contacts == 0 && permissions == 0)
		{
			this.checkAllResources();
			
			Resource policyResource = abacDao.getResourceByNameWithFields("AbacPolicy");
			Resource contactResource = abacDao.getResourceByName("Contact");
			Resource userResource = abacDao.getResourceByName("User");
			Resource roleResource = abacDao.getResourceByName("Role");
			
			AbacPolicy abacUpdatePolicy = new AbacPolicy("PolicyUpdate", PolicyType.UPDATE, policyResource);
			abacUpdatePolicy.setCdPolicy(true, true);
			abacUpdatePolicy.addEntityCondition(UserAttribute.ROLE, ComparisonOperator.EQUALS, "Administrators");
			abacDao.savePolicy(abacUpdatePolicy);
			
			AbacPolicy abacPolicy = new AbacPolicy("PolicyRead", PolicyType.READ, policyResource);
			abacPolicy.addEntityCondition(UserAttribute.ROLE, ComparisonOperator.EQUALS, "Administrators");
			abacDao.savePolicy(abacPolicy);
			
			AbacPolicy contactUpdatePolicy = new AbacPolicy("ContactUpdate", PolicyType.UPDATE, contactResource);
			contactUpdatePolicy.setCdPolicy(true, true);
			contactUpdatePolicy.addEntityCondition(UserAttribute.ROLE, ComparisonOperator.EQUALS, "Administrators");
			abacDao.savePolicy(contactUpdatePolicy);
			
			AbacPolicy contactReadPolicy = new AbacPolicy("ContactRead", PolicyType.READ, contactResource);
			contactReadPolicy.addEntityCondition(UserAttribute.ROLE, ComparisonOperator.EQUALS, "Administrators");
			abacDao.savePolicy(contactReadPolicy);
			
			AbacPolicy userCreatePolicy = new AbacPolicy("UserUpdate", PolicyType.UPDATE, userResource);
			userCreatePolicy.setCdPolicy(true, true);
			userCreatePolicy.addEntityCondition(UserAttribute.ROLE, ComparisonOperator.EQUALS, "Administrators");
			abacDao.savePolicy(userCreatePolicy);
			
			AbacPolicy userReadPolicy = new AbacPolicy("UserRead", PolicyType.READ, userResource);
			userReadPolicy.addEntityCondition(UserAttribute.ROLE, ComparisonOperator.EQUALS, "Administrators");
			abacDao.savePolicy(userReadPolicy);
			
			AbacPolicy roleUpdate = new AbacPolicy("RoleUpdate", PolicyType.UPDATE, roleResource);
			roleUpdate.setCdPolicy(true, true);
			roleUpdate.addEntityCondition(UserAttribute.ROLE, ComparisonOperator.EQUALS, "Administrators");
			abacDao.savePolicy(roleUpdate);
			
			AbacPolicy roleRead = new AbacPolicy("RoleRead", PolicyType.READ, roleResource);
			roleRead.addEntityCondition(UserAttribute.ROLE, ComparisonOperator.EQUALS, "Administrators");
			abacDao.savePolicy(roleRead);
			
			authDao.createRole(new Role("Administrators"));
			Role adminRole = authDao.getRoleByName("Administrators", null);
			
			Contact initialContact = new Contact("Administrator", null, null, "uec_ops_support@unlimitedcompanies.com");
			contactDao.createContact(initialContact);
			Contact adminContact = contactDao.getContactByCharId(initialContact.getContactCharId(), null);
			
			authDao.createUser(new User("administrator", "uec123", adminContact));
			User adminUser = authDao.getUserByUsername("administrator", null);
			
			authDao.assignUserToRole(adminUser.getUserId(), adminRole.getRoleId());
			
			this.clearEntityManager();
		}
		
//		Resource projectResource = abacDao.getResourceByNameWithFields("Project");
//		
//		AbacPolicy abacUpdatePolicy = new AbacPolicy("ProjectRead", PolicyType.READ, projectResource);
//		abacUpdatePolicy.setCdPolicy(true, true);
//		abacUpdatePolicy.addEntityCondition(UserAttribute.ROLE, ComparisonOperator.EQUALS, "Administrators");
//		abacUpdatePolicy.addEntityCondition(UserAttribute.ROLE, ComparisonOperator.EQUALS, "Managers");
//		abacUpdatePolicy.addEntityCondition(UserAttribute.ROLE, ComparisonOperator.EQUALS, "RootGroup");
//		abacUpdatePolicy.addAttributeCondition(ResourceAttribute.P_MANAGERS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
//		abacUpdatePolicy.addAttributeCondition(ResourceAttribute.P_FOREMEN, ComparisonOperator.NOT_EQUALS, UserAttribute.USERNAME);
//		abacUpdatePolicy.addFieldConditions("jobNumber", ComparisonOperator.EQUALS, "36549651");
//		abacUpdatePolicy.addFieldConditions("projectName", ComparisonOperator.EQUALS, "Sample Project");
//		abacUpdatePolicy.addSubPolicy();
//		abacUpdatePolicy.addSubPolicy();
//		AbacPolicy subpolicy = abacUpdatePolicy.addSubPolicy(LogicOperator.OR);
//		subpolicy.addEntityCondition(UserAttribute.ROLE, ComparisonOperator.EQUALS, "Administrators");
//		subpolicy.addEntityCondition(UserAttribute.ROLE, ComparisonOperator.EQUALS, "Managers");
//		subpolicy.addAttributeCondition(ResourceAttribute.P_MANAGERS, ComparisonOperator.EQUALS, UserAttribute.USERNAME);
//		subpolicy.addAttributeCondition(ResourceAttribute.P_FOREMEN, ComparisonOperator.NOT_EQUALS, UserAttribute.USERNAME);
//		subpolicy.addFieldConditions("jobNumber", ComparisonOperator.EQUALS, "36549651");
//		subpolicy.addFieldConditions("projectName", ComparisonOperator.EQUALS, "Sample Project");
//		abacDao.savePolicy(abacUpdatePolicy);
	}
	
	@Override
	public void checkAllResources()
	{
		abacDao.checkResourceList();
		abacDao.checkResourceFieldList();
	}
	
	@Override
	public List<Resource> searchAllResources()
	{
		return abacDao.getAllResources();
	}

	@Override
	public List<String> searchAllResourceNames()
	{
		return abacDao.getAllResourceNames();
	}
	
	
	/*
	 * Get objects with system rights which is without checking any permissions to obtain them
	 */
	
	@Override
	public AbacPolicy searchPolicy(Resource resource, PolicyType policyType) throws NoResourceAccessException
	{
		try
		{
			return abacDao.getPolicy(resource, policyType, null);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public Role searchRoleById(int roleId) throws NoResourceAccessException
	{
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
		try
		{
			return abacDao.getResourceFieldById(fieldId);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public List<String> searchRestrictedFields(int userId, int resourceId)
	{
		List<ResourceField> fields = abacDao.getRestrictedFields(userId, resourceId);
		
		List<String> restrictedFields = new ArrayList<>();
		for (ResourceField field : fields)
		{
			restrictedFields.add(field.getResourceFieldName());
		}
		return restrictedFields;
	}
	
	@Override
	public User searchFullUserByUsername(String username)
	{
		return authDao.getFullUserByUsername(username, null, null, null);
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

	@Override
	public void clearEntityManager() 
	{
		systemDao.clearEntityManager();
	}
}
