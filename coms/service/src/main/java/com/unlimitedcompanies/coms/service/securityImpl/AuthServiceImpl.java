package com.unlimitedcompanies.coms.service.securityImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.AuthDao;
import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.domain.abac.ResourceAttribs;
import com.unlimitedcompanies.coms.domain.abac.ResourceReadPolicy;
import com.unlimitedcompanies.coms.domain.abac.UserAttribs;
import com.unlimitedcompanies.coms.domain.employee.Employee;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
import com.unlimitedcompanies.coms.service.exceptions.IncorrectPasswordException;
import com.unlimitedcompanies.coms.service.exceptions.LastAdminRemovalException;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotDeletedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.ABACService;
import com.unlimitedcompanies.coms.service.security.AuthService;
import com.unlimitedcompanies.coms.service.system.SystemService;

@Service
@Transactional
public class AuthServiceImpl implements AuthService
{
	@Autowired
	private AuthDao authDao;
	
	@Autowired
	private ABACService abacService;
	
	@Autowired
	private SystemService systemService;

	@Override
	public void saveUser(User user, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException, DuplicateRecordException
	{
		Resource userResource = abacService.searchResourceByName("User");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy userPolicy = systemService.searchPolicy(userResource, PolicyType.UPDATE);
		UserAttribs userAttribs = systemService.getUserAttribs(signedUser.getUserId());
		
		if (user.getContact() == null || user.getContact().getContactId() == null)
		{
			String exceptionMessage = "The contact associated with the user you are trying to create could not be found";
			throw new RecordNotFoundException(exceptionMessage);
		}
		
		if (userPolicy.getModifyPolicy(null, userAttribs, signedUser) && userPolicy.getCdPolicy().isCreatePolicy())
		{
			try
			{
				List<String> restrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), userResource.getResourceId());
				if (restrictedFields.contains("username") || restrictedFields.contains("password") || restrictedFields.contains("contact"))
				{
					throw new NoResourceAccessException();
				}
				
				authDao.createUser(user);
				systemService.clearEntityManager();
			}
			catch (ConstraintViolationException e)
			{
				if (e.getConstraintName() != null && e.getConstraintName().endsWith("_UNIQUE"))
				{
					throw new DuplicateRecordException();
				}
				throw e;
			}
		}
		
	}
	
	@Override
	public int searchNumberOfUsers()
	{
		return authDao.getNumberOfUsers();
	}
//	
//	@Override
//	public boolean hasNextUser(int page, int elements)
//	{
//		List<User> foundUsers = authDao.getUsersByRange((page - 1) * elements, 1);
//		if (foundUsers.isEmpty()) 
//		{
//			return false;
//		}
//		else 
//		{
//			return true;
//		}
//	}

	@Override
	public List<User> searchAllUsers(String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		Resource userResource = abacService.searchResourceByName("User");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy policy = systemService.searchPolicy(userResource, PolicyType.READ);
		ResourceReadPolicy readPolicy = policy.getReadPolicy(User.class, signedUser);
		
		if (readPolicy.isReadGranted())
		{
			List<User> users = authDao.getAllUsers(readPolicy.getReadConditions());
			systemService.clearEntityManager();
			
			List<String> restrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), userResource.getResourceId());
			if (restrictedFields.size() > 0)
			{
				for (User foundUser : users)
				{
					foundUser.cleanRestrictedFields(restrictedFields);
				}
			}
			
			return users;
		}
		else
		{
			throw new NoResourceAccessException();
		}
		
	}
	
	@Override
	public List<User> searchAllUsers(int elements, int page, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		Resource userResource = abacService.searchResourceByName("User");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy policy = systemService.searchPolicy(userResource, PolicyType.READ);
		ResourceReadPolicy readPolicy = policy.getReadPolicy(User.class, signedUser);
		
		if (readPolicy.isReadGranted())
		{
			List<User> users = authDao.getAllUsers(elements, page-1, readPolicy.getReadConditions());
			systemService.clearEntityManager();
			
			List<String> restrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), userResource.getResourceId());
			if (restrictedFields.size() > 0)
			{
				for (User foundUser : users)
				{
					foundUser.cleanRestrictedFields(restrictedFields);
				}
			}
			
			return users;
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}

	@Override
	public User searchUserById(int id, String signedUsername) throws RecordNotFoundException, NoResourceAccessException
	{
		Resource userResource = abacService.searchResourceByName("User");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy policy = systemService.searchPolicy(userResource, PolicyType.READ);
		ResourceReadPolicy readPolicy = policy.getReadPolicy(User.class, signedUser);
		
		if (readPolicy.isReadGranted())
		{
			try
			{
				User user = authDao.getUserById(id, readPolicy.getReadConditions());
				systemService.clearEntityManager();
				
				List<String> restrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), userResource.getResourceId());
				if (restrictedFields.size() > 0)
				{
					user.cleanRestrictedFields(restrictedFields);
				}
				return user;
			} 
			catch (NoResultException e)
			{
				throw new RecordNotFoundException("The user could not be found");
			}			
		}
		else
		{
			throw new NoResourceAccessException();
		}
		
	}

	@Override
	public User searchUserByUsername(String username, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException
	{
		Resource userResource = abacService.searchResourceByName("User");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy policy = systemService.searchPolicy(userResource, PolicyType.READ);
		ResourceReadPolicy readPolicy = policy.getReadPolicy(User.class, signedUser);
		
		if (readPolicy.isReadGranted())
		{
			try
			{
				User user = authDao.getUserByUsername(username, readPolicy.getReadConditions());
				systemService.clearEntityManager();
				
				List<String> restrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), userResource.getResourceId());
				if (restrictedFields.size() > 0)
				{
					user.cleanRestrictedFields(restrictedFields);
				}
				return user;
			} 
			catch (NoResultException e)
			{
				throw new RecordNotFoundException("The user could not be found");
			}			
		}
		else
		{
			throw new NoResourceAccessException();
		}
		
	}
	
	@Override
	public User searchUserByIdWithContact(int userId, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		Resource userResource = abacService.searchResourceByName("User");
		Resource contactResource = abacService.searchResourceByName("Contact");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy userPolicy = systemService.searchPolicy(userResource, PolicyType.READ);
		ResourceReadPolicy userReadPolicy = userPolicy.getReadPolicy(User.class, signedUser);
		
		AbacPolicy contactPolicy = systemService.searchPolicy(contactResource, PolicyType.READ);
		ResourceReadPolicy contactReadPolicy = contactPolicy.getReadPolicy(Contact.class, signedUser);		
		
		if (userReadPolicy.isReadGranted() && contactReadPolicy.isReadGranted())
		{
			try
			{
				User user = authDao.getUserByIdWithContact(userId, userReadPolicy.getReadConditions(), contactReadPolicy.getReadConditions());
				systemService.clearEntityManager();
				
				List<String> contactRestrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), contactResource.getResourceId());
				if (contactRestrictedFields.size() > 0)
				{
					user.getContact().cleanRestrictedFields(contactRestrictedFields);
				}
				
				List<String> userRestrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), userResource.getResourceId());
				if (userRestrictedFields.size() > 0)
				{
					user.cleanRestrictedFields(userRestrictedFields);
				}
				
				return user;
			} 
			catch (NoResultException e)
			{
				throw new RecordNotFoundException("The user could not be found");
			}			
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public User searchUserByUsernameWithContact(String username, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		Resource userResource = abacService.searchResourceByName("User");
		Resource contactResource = abacService.searchResourceByName("Contact");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy userPolicy = systemService.searchPolicy(userResource, PolicyType.READ);
		ResourceReadPolicy userReadPolicy = userPolicy.getReadPolicy(User.class, signedUser);
		
		AbacPolicy contactPolicy = systemService.searchPolicy(contactResource, PolicyType.READ);
		ResourceReadPolicy contactReadPolicy = contactPolicy.getReadPolicy(Contact.class, signedUser);		
		
		if (userReadPolicy.isReadGranted() && contactReadPolicy.isReadGranted())
		{
			try
			{
				User user = authDao.getUserByUsernameWithContact(username, userReadPolicy.getReadConditions(), contactReadPolicy.getReadConditions());
				systemService.clearEntityManager();
				
				List<String> contactRestrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), contactResource.getResourceId());
				if (contactRestrictedFields.size() > 0)
				{
					user.getContact().cleanRestrictedFields(contactRestrictedFields);
				}
				
				List<String> userRestrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), userResource.getResourceId());
				if (userRestrictedFields.size() > 0)
				{
					user.cleanRestrictedFields(userRestrictedFields);
				}
				
				return user;
			} 
			catch (NoResultException e)
			{
				throw new RecordNotFoundException("The user could not be found");
			}			
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public User searchUserByContact(Contact contact, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		Resource userResource = abacService.searchResourceByName("User");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy policy = systemService.searchPolicy(userResource, PolicyType.READ);
		ResourceReadPolicy readPolicy = policy.getReadPolicy(User.class, signedUser);
		
		if (readPolicy.isReadGranted())
		{
			try
			{
				User user = authDao.getUserByContact(contact, readPolicy.getReadConditions());
				systemService.clearEntityManager();
				
				List<String> restrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), userResource.getResourceId());
				if (restrictedFields.size() > 0)
				{
					user.cleanRestrictedFields(restrictedFields);
				}
				return user;
			} 
			catch (NoResultException e)
			{
				throw new RecordNotFoundException("The user could not be found");
			}			
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public User searchFullUserByUsername(String username, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		Resource userResource = abacService.searchResourceByName("User");
		Resource contactResource = abacService.searchResourceByName("Contact");
		Resource roleResource = abacService.searchResourceByName("Role");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy userPolicy = systemService.searchPolicy(userResource, PolicyType.READ);
		ResourceReadPolicy userReadPolicy = userPolicy.getReadPolicy(User.class, signedUser);
		
		AbacPolicy contactPolicy = systemService.searchPolicy(contactResource, PolicyType.READ);
		ResourceReadPolicy contactReadPolicy = contactPolicy.getReadPolicy(Contact.class, signedUser);
		
		AbacPolicy rolePolicy = systemService.searchPolicy(roleResource, PolicyType.READ);
		ResourceReadPolicy roleReadPolicy = rolePolicy.getReadPolicy(Role.class, signedUser);
		
		if (userReadPolicy.isReadGranted() && contactReadPolicy.isReadGranted() && roleReadPolicy.isReadGranted())
		{
			try
			{
				User user = authDao.getFullUserByUsername(username, 
															userReadPolicy.getReadConditions(), 
															contactReadPolicy.getReadConditions(), 
															roleReadPolicy.getReadConditions());
				systemService.clearEntityManager();
				
				List<String> contactRestrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), roleResource.getResourceId());
				if (contactRestrictedFields.size() > 0)
				{
					user.getContact().cleanRestrictedFields(contactRestrictedFields);
				}
				
				List<String> userRestrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), userResource.getResourceId());
				if (userRestrictedFields.size() > 0)
				{
					user.cleanRestrictedFields(userRestrictedFields);
				}
				
				return user;
			} 
			catch (NoResultException e)
			{
				throw new RecordNotFoundException("The user could not be found");
			}			
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public void updateUser(User user, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		Resource userResource = abacService.searchResourceByName("User");
		AbacPolicy userPolicy = systemService.searchPolicy(userResource, PolicyType.UPDATE);
		
		ResourceAttribs resourceAttribs = this.getUserResourceAttribs(user.getUserId());
		UserAttribs userAttribs = systemService.getUserAttribs(signedUser.getUserId());
		
		if (userPolicy.getModifyPolicy(resourceAttribs, userAttribs, signedUser))
		{
			
			// Check if there are any restricted fields for the requesting user
			List<String> restrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), userResource.getResourceId());
			restrictedFields.add("password");
			restrictedFields.add("dateAdded");
			restrictedFields.add("lastAccess");
			User foundUser = authDao.getUserById(user.getUserId(), null);
			user.cleanRestrictedFields(restrictedFields, foundUser);
			
			authDao.updateUser(user);
			systemService.clearEntityManager();
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public void changeUserPassword(User user, String currentPassword, String newPassword, String signedUsername) 
			throws IncorrectPasswordException, NoResourceAccessException, RecordNotFoundException
	{
		if (!user.isPassword(currentPassword))
		{
			throw new IncorrectPasswordException();
		}
		
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		Resource userResource = abacService.searchResourceByName("User");
		AbacPolicy userPolicy = systemService.searchPolicy(userResource, PolicyType.UPDATE);
		
		ResourceAttribs resourceAttribs = this.getUserResourceAttribs(user.getUserId());
		UserAttribs userAttribs = systemService.getUserAttribs(signedUser.getUserId());
		
		if (userPolicy.getModifyPolicy(resourceAttribs, userAttribs, signedUser))
		{
			// Check if the user password is a restricted field for the requesting user
			List<String> restrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), userResource.getResourceId());
			if (restrictedFields.contains("password"))
			{
				throw new NoResourceAccessException();
			}
			else
			{
				user.setPassword(newPassword);
				authDao.updateUser(user);
				systemService.clearEntityManager();
			}
		}
	}

	@Override
	public void deleteUser(int userId, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException, RecordNotDeletedException, LastAdminRemovalException
	{		
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		Resource userResource = abacService.searchResourceByName("User");
		AbacPolicy userPolicy = systemService.searchPolicy(userResource, PolicyType.UPDATE);
		
		UserAttribs userAttribs = systemService.getUserAttribs(signedUser.getUserId());
		ResourceAttribs resourceAttribs = this.getUserResourceAttribs(userId);
		
		if (userPolicy.getModifyPolicy(resourceAttribs, userAttribs, signedUser) && userPolicy.getCdPolicy().isDeletePolicy())
		{
			Set<User> users = authDao.getRoleByNameWithMembers("Administrators", null).getUsers();
			if (users.size() < 2)
			{
				for (User user : users)
				{
					if (user.getUserId() == userId)
					{
						throw new LastAdminRemovalException();
					}

				}
			}
			
			authDao.deleteUser(userId);
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}

	@Override
	public void saveRole(Role role, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		User signedUser = systemService.searchFullUserByUsername(signedUsername);

		Resource roleResource = abacService.searchResourceByName("Role");		
		AbacPolicy roleUpdatePolicy = systemService.searchPolicy(roleResource, PolicyType.UPDATE);
		
		UserAttribs userAttribs = systemService.getUserAttribs(signedUser.getUserId());
		
		if (roleUpdatePolicy.getModifyPolicy(null, userAttribs, signedUser))
		{			
			role.clearRestrictedFields();
			authDao.createRole(role);
			systemService.clearEntityManager();
		}
		else
		{
			throw new NoResourceAccessException();
		}
		
	}
	
	@Override
	public int searchNumberOfRoles()
	{
		return authDao.getNumberOfRoles();
	}
//	
//	@Override
//	public boolean hasNextRole(int page, int elements)
//	{
//		List<Role> foundRoles = authDao.getAllRolesByRange((page - 1) * elements, 1);
//		if (foundRoles.isEmpty()) 
//		{
//			return false;
//		}
//		else 
//		{
//			return true;
//		}
//	}

	@Override
	public List<Role> searchAllRoles(String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		Resource roleResource = abacService.searchResourceByName("Role");
		AbacPolicy rolePolicy = systemService.searchPolicy(roleResource, PolicyType.READ);
		ResourceReadPolicy readPolicy = rolePolicy.getReadPolicy(Role.class, signedUser);
		
		if (readPolicy.isReadGranted())
		{
			List<Role> allRoles = authDao.getAllRoles(readPolicy.getReadConditions());
			systemService.clearEntityManager();
			
			return allRoles;
		}		
		else
		{
			throw new NoResourceAccessException();
		}
	}

	@Override
	public List<Role> searchAllRoles(int elements, int page, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		Resource roleResource = abacService.searchResourceByName("Role");
		AbacPolicy rolePolicy = systemService.searchPolicy(roleResource, PolicyType.READ);
		ResourceReadPolicy readPolicy = rolePolicy.getReadPolicy(Role.class, signedUser);
		
		if (readPolicy.isReadGranted())
		{
			List<Role> roles = authDao.getAllRoles(elements, page-1, readPolicy.getReadConditions());
			systemService.clearEntityManager();
			
			return roles;
		}		
		else
		{
			throw new NoResourceAccessException();
		}
		
	}

	@Override
	public Role searchRoleById(int roleId, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		Resource roleResource = abacService.searchResourceByName("Role");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy roleRead = systemService.searchPolicy(roleResource, PolicyType.READ);
		ResourceReadPolicy readPolicy = roleRead.getReadPolicy(Role.class, signedUser);
		
		if (readPolicy.isReadGranted())
		{
			try
			{
				Role role = authDao.getRoleById(roleId, readPolicy.getReadConditions());
				systemService.clearEntityManager();
				return role;
			}
			catch (NoResultException e)
			{
				throw new RecordNotFoundException("The role could not be found");
			}
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	private Role searchRoleById(int roleId, User signedUser) throws NoResourceAccessException, RecordNotFoundException
	{
		Resource roleResource = abacService.searchResourceByName("Role");
		
		AbacPolicy roleRead = systemService.searchPolicy(roleResource, PolicyType.READ);
		ResourceReadPolicy readPolicy = roleRead.getReadPolicy(Role.class, signedUser);
		
		if (readPolicy.isReadGranted())
		{
			try
			{
				Role role = authDao.getRoleById(roleId, readPolicy.getReadConditions());
				return role;
			}
			catch (NoResultException e)
			{
				throw new RecordNotFoundException("The role could not be found");
			}
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public Role searchRoleByName(String roleName, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		Resource roleResource = abacService.searchResourceByName("Role");
		User loggedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy roleRead = systemService.searchPolicy(roleResource, PolicyType.READ);
		ResourceReadPolicy readPolicy = roleRead.getReadPolicy(Role.class, loggedUser);
		
		if (readPolicy.isReadGranted())
		{
			try
			{
				Role role = authDao.getRoleByName(roleName, readPolicy.getReadConditions());
				systemService.clearEntityManager();
				return role;
			}
			catch (NoResultException e)
			{
				throw new RecordNotFoundException("The role could not be found");
			}			
		}
		else
		{
			throw new NoResourceAccessException();
		}
		
	}
	
	@Override
	public Role searchRoleByNameWithRestrictedFields(String roleName, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		Resource roleResource = abacService.searchResourceByName("Role");
		User loggedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy roleRead = systemService.searchPolicy(roleResource, PolicyType.READ);
		ResourceReadPolicy readPolicy = roleRead.getReadPolicy(Role.class, loggedUser);
		
		if (readPolicy.isReadGranted())
		{
			try
			{
				Role role = authDao.getRoleByNameWithRestrictedFields(roleName, readPolicy.getReadConditions());
				systemService.clearEntityManager();
				return role;
			}
			catch (NoResultException e)
			{
				throw new RecordNotFoundException("The role could not be found");
			}			
		}
		else
		{
			throw new NoResourceAccessException();
		}
		
	}

	@Override
	public Role searchRoleByIdWithMembers(int roleId, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		User signedUser = systemService.searchFullUserByUsername(signedUsername);

		Resource roleResource = abacService.searchResourceByName("Role");
		Resource userResource = abacService.searchResourceByName("User");
		Resource contactResource = abacService.searchResourceByName("Contact");
		
		AbacPolicy roleRead = systemService.searchPolicy(roleResource, PolicyType.READ);
		AbacPolicy userRead = systemService.searchPolicy(userResource, PolicyType.READ);
		AbacPolicy contactRead = systemService.searchPolicy(contactResource, PolicyType.READ);
		
		ResourceReadPolicy roleReadPolicy = roleRead.getReadPolicy(Role.class, signedUser);
		ResourceReadPolicy userReadPolicy = userRead.getReadPolicy(User.class, signedUser);
		ResourceReadPolicy contactReadPolicy = contactRead.getReadPolicy(Contact.class, signedUser);
		
		if (roleReadPolicy.isReadGranted() && userReadPolicy.isReadGranted() && contactReadPolicy.isReadGranted())
		{
			try
			{
				Role role = authDao.getRoleByIdWithMembers(roleId, roleReadPolicy.getReadConditions());
				systemService.clearEntityManager();
				
				List<String> userRestrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), userResource.getResourceId());
				if (userRestrictedFields.size() > 0)
				{
					for (User user : role.getUsers())
					{
						user.cleanRestrictedFields(userRestrictedFields);
					}
				}
				
				List<String> contactRestrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), contactResource.getResourceId());
				if (contactRestrictedFields.size() > 0)
				{
					for (User user : role.getUsers())
					{
						user.getContact().cleanRestrictedFields(contactRestrictedFields);
					}
				}
				
				return role;
			}
			catch (NoResultException e)
			{
				throw new RecordNotFoundException("The role could not be found");
			}
		}
		else
		{
			throw new NoResourceAccessException();
		}
		
	}
	
	@Override
	public Role searchRoleByNameWithMembers(String roleName, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		User signedUser = systemService.searchFullUserByUsername(signedUsername);

		Resource roleResource = abacService.searchResourceByName("Role");
		Resource userResource = abacService.searchResourceByName("User");
		Resource contactResource = abacService.searchResourceByName("Contact");
		
		AbacPolicy roleRead = systemService.searchPolicy(roleResource, PolicyType.READ);
		AbacPolicy userRead = systemService.searchPolicy(userResource, PolicyType.READ);
		AbacPolicy contactRead = systemService.searchPolicy(contactResource, PolicyType.READ);
		
		ResourceReadPolicy roleReadPolicy = roleRead.getReadPolicy(Role.class, signedUser);
		ResourceReadPolicy userReadPolicy = userRead.getReadPolicy(User.class, signedUser);
		ResourceReadPolicy contactReadPolicy = contactRead.getReadPolicy(Contact.class, signedUser);
		
		if (roleReadPolicy.isReadGranted() && userReadPolicy.isReadGranted() && contactReadPolicy.isReadGranted())
		{
			try
			{
				Role role = authDao.getRoleByNameWithMembers(roleName, roleReadPolicy.getReadConditions());
				systemService.clearEntityManager();
				
				List<String> userRestrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), userResource.getResourceId());
				if (userRestrictedFields.size() > 0)
				{
					for (User user : role.getUsers())
					{
						user.cleanRestrictedFields(userRestrictedFields);
					}
				}
				
				List<String> contactRestrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), contactResource.getResourceId());
				if (contactRestrictedFields.size() > 0)
				{
					for (User user : role.getUsers())
					{
						user.getContact().cleanRestrictedFields(contactRestrictedFields);
					}
				}
				
				return role;
			}
			catch (NoResultException e)
			{
				throw new RecordNotFoundException("The role could not be found");
			}
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}

//	
//	@Override
//	public List<User> searchRoleNonMembers(int roleId, String searchCriteria)
//	{
//		return authDao.getRoleNonMembersByCriteria(roleId, searchCriteria);
//	}
	
	@Override
	public void updateRole(Role role, String signedUsername) throws NoResourceAccessException, RecordNotFoundException, LastAdminRemovalException
	{		
		Resource roleResource = abacService.searchResourceByName("Role");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy rolePolicy = systemService.searchPolicy(roleResource, PolicyType.UPDATE);
		ResourceAttribs resourceAttribs = this.getRoleResourceAttribs(role.getRoleId());
		UserAttribs userAttribs = systemService.getUserAttribs(signedUser.getUserId());
		
		if (rolePolicy.getModifyPolicy(resourceAttribs, userAttribs, signedUser))
		{
			Role foundRole = this.searchRoleById(role.getRoleId(), signedUser);
			if (foundRole.getRoleName().equals("Administrator"))
			{
				throw new LastAdminRemovalException();
			}
			authDao.updateRole(role);
			systemService.clearEntityManager();
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public void deleteRole(int roleId, String signedUsername) throws NoResourceAccessException, RecordNotFoundException, LastAdminRemovalException
	{
		User signedUser = systemService.searchFullUserByUsername(signedUsername);

		Resource roleResource = abacService.searchResourceByName("Role");		
		AbacPolicy roleUpdatePolicy = systemService.searchPolicy(roleResource, PolicyType.UPDATE);
		
		UserAttribs userAttribs = systemService.getUserAttribs(signedUser.getUserId());
		
		if (roleUpdatePolicy.getModifyPolicy(null, userAttribs, signedUser) && roleUpdatePolicy.getCdPolicy().isDeletePolicy())
		{
			Role role = this.searchRoleById(roleId, signedUser);
			if (role.getRoleName().equals("Administrator"))
			{
				throw new LastAdminRemovalException();
			}
			authDao.deleteRole(role);
			systemService.clearEntityManager();
		}
		else
		{
			throw new NoResourceAccessException();
		}
		
	}

	@Override
	public void assignUserToRole(int userId, int roleId, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{		
		Resource roleResource = abacService.searchResourceByName("Role");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy rolePolicy = systemService.searchPolicy(roleResource, PolicyType.UPDATE);
		ResourceAttribs resourceAttribs = this.getRoleResourceAttribs(roleId);
		UserAttribs userAttribs = systemService.getUserAttribs(signedUser.getUserId());
		
		if (rolePolicy.getModifyPolicy(resourceAttribs, userAttribs, signedUser))
		{
			authDao.assignUserToRole(userId, roleId);
		}
	}
	
	@Override
	public void removeRoleMember(int userId, int roleId, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException, LastAdminRemovalException
	{	
		Resource roleResource = abacService.searchResourceByName("Role");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy rolePolicy = systemService.searchPolicy(roleResource, PolicyType.UPDATE);
		ResourceAttribs resourceAttribs = this.getRoleResourceAttribs(roleId);
		UserAttribs userAttribs = systemService.getUserAttribs(signedUser.getUserId());
		
		if (rolePolicy.getModifyPolicy(resourceAttribs, userAttribs, signedUser))
		{
			try
			{
				User user = authDao.getUserById(userId, null);
				Role role = authDao.getRoleByIdWithMembers(roleId, null);
				if (role.getRoleName().equals("Administrators") && authDao.getNumberOfRoleMembers(role.getRoleId()) <= 1)
				{
					throw new LastAdminRemovalException();
				}
				authDao.removeUserFromRole(user, role);
				systemService.clearEntityManager();
			}
			catch (NoResultException e)
			{
				throw new RecordNotFoundException("The referenced role or user member could not be found");
			}
		}
	}
	
	private ResourceAttribs getUserResourceAttribs(int userId) throws RecordNotFoundException
	{		
		User user;
		try
		{
			user = authDao.getUserWithPathToProjects(userId);
		}
		catch (NoResultException e)
		{
			throw new RecordNotFoundException("The user could not be found");
		}
		Employee employee = user.getContact().getEmployee();
		
		ResourceAttribs resourceAttribs = new ResourceAttribs();
		
		if (employee != null)
		{
			resourceAttribs.addProjectManager(employee.getPMAssociatedProjectNames());
			resourceAttribs.addProjectSuperintendent(employee.getSuperintendentAssociatedProjectNames());
			resourceAttribs.addProjectForman(employee.getForemanAssociatedProjectNames());			
		}
		
		return resourceAttribs;
	}
	
	private ResourceAttribs getRoleResourceAttribs(int roleId)
	{
		Role role = authDao.getRoleWithPathToProjects(roleId);
		Set<User> users = role.getUsers();
		List<Employee> employees = new ArrayList<>();
		
		for (User user : users)
		{
			if (user.getContact().getEmployee() != null)
			{
				employees.add(user.getContact().getEmployee());				
			}
		}
		
		ResourceAttribs resourceAttribs = new ResourceAttribs();
		for (Employee employee : employees)
		{
			resourceAttribs.addProjectManager(employee.getPMAssociatedProjectNames());
			resourceAttribs.addProjectSuperintendent(employee.getSuperintendentAssociatedProjectNames());
			resourceAttribs.addProjectForman(employee.getForemanAssociatedProjectNames());
		}
		
		return resourceAttribs;
	}
}
