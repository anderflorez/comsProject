package com.unlimitedcompanies.coms.service.securityImpl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.abac.SystemService;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.ABACService;
import com.unlimitedcompanies.coms.service.security.AuthService;

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
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public void saveUser(User user, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException, DuplicateRecordException
	{
		Resource userResource = abacService.searchResourceByName("User");
		User loggedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy userPolicy = systemService.searchPolicy(userResource, PolicyType.UPDATE);
		UserAttribs userAttribs = systemService.getUserAttribs(loggedUser.getUserId());
		
		if (user.getContact() == null || user.getContact().getContactId() == null)
		{
			String exceptionMessage = "The contact associated with the user you are trying to create could not be found";
			throw new RecordNotFoundException(exceptionMessage);
		}
		
		if (userPolicy.getModifyPolicy(null, userAttribs, loggedUser) && userPolicy.getCdPolicy().isCreatePolicy())
		{
			try
			{
				PasswordEncoder pe = new BCryptPasswordEncoder();
				String encoded = pe.encode(String.valueOf(user.getPassword()));
				user.setPassword(encoded.toCharArray());
				authDao.createUser(user);
				authDao.clearEntityManager();
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
//
//	@Override
//	public List<User> searchAllUsers(String username)
//	{
//		Resource resource = abacService.findResourceByName("User");
//		User currentUser = systemService.findFullUserByUsername(username);
//		
//		AbacPolicy policy = abacService.findPolicy(resource, PolicyType.READ, username);
//		ResourceReadPolicy readPolicy = policy.getReadPolicy("user", "project", currentUser);
//		
//		if (readPolicy.isReadGranted())
//		{
//			List<User> users = authDao.getAllUsers(readPolicy.getReadConditions());
//			authDao.clearEntityManager();
//			return users;
//		}
//		else
//		{
//			throw new NoResourceAccessException();
//		}
//		
//	}
//	
//	@Override
//	public List<User> searchUsersByRange(int page, int elements)
//	{
//		return authDao.getUsersByRange(page - 1, elements);
//	}

	@Override
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public User searchUserById(int id, String requestedByUsername) throws RecordNotFoundException, NoResourceAccessException
	{
		Resource resource = abacService.searchResourceByName("User");
		User currentUser = systemService.searchFullUserByUsername(requestedByUsername);
		
		AbacPolicy policy = systemService.searchPolicy(resource, PolicyType.READ);
		ResourceReadPolicy readPolicy = policy.getReadPolicy("user", "project", currentUser);
		
		if (readPolicy.isReadGranted())
		{
			try
			{
				User user = authDao.getUserByUserId(id, readPolicy.getReadConditions());
				authDao.clearEntityManager();
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
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public User searchUserByUsername(String username, String requestedByUsername) 
			throws NoResourceAccessException, RecordNotFoundException
	{
		Resource resource = abacService.searchResourceByName("User");
		User user = systemService.searchFullUserByUsername(requestedByUsername);
		
		AbacPolicy policy = systemService.searchPolicy(resource, PolicyType.READ);
		ResourceReadPolicy readPolicy = policy.getReadPolicy("user", "project", user);
		
		if (readPolicy.isReadGranted())
		{
			try
			{
				User foundUser = authDao.getUserByUsername(username, readPolicy.getReadConditions());
				authDao.clearEntityManager();
				return foundUser;
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
//	
//	@Override
//	@Transactional(rollbackFor = RecordNotFoundException.class)
//	public User searchUserByContact(Contact contact) throws RecordNotFoundException
//	{
//		try
//		{
//			return authDao.getUserByContact(contact);
//		} 
//		catch (NoResultException e)
//		{
//			throw new RecordNotFoundException("The user could not be found");
//		}
//	}
//	
//	@Override
//	@Transactional(rollbackFor = RecordNotFoundException.class)
//	public User searchUserByUserIdWithContact(int userId) throws RecordNotFoundException
//	{
//		try
//		{
//			return authDao.getUserByUserIdWithContact(userId);
//		}
//		catch (NoResultException e)
//		{
//			throw new RecordNotFoundException("The user could not be found");
//		}
//	}
//	
//	@Override
//	@Transactional(rollbackFor = RecordNotFoundException.class)
//	public User searchUserByUsernameWithContact(String username) throws RecordNotFoundException
//	{
//		try
//		{
//			return authDao.getUserByUsernameWithContact(username);
//		} 
//		catch (NoResultException e)
//		{
//			throw new RecordNotFoundException("The user could not be found");
//		}
//	}
//	
////	@Override
////	public User searchAUserByIdWithRoles(int userId)
////	{
////		return authDao.getAUserByIdWithRoles(userId);
////	}
//	
//	@Override
//	public User searchFullUserByUserId(int id)
//	{
//		return authDao.getFullUserByUserId(id);
//	}
//
//	@Override
//	public User searchFullUserByUsername(String username)
//	{
//		return authDao.getFullUserByUsername(username);
//	}
//	
////	@Override
////	public boolean passwordMatch(int userId, char[] password) throws RecordNotFoundException
////	{
////		User user = this.searchUserByUserId(userId);
////		
////		PasswordEncoder pe = new BCryptPasswordEncoder();
////		if (pe.matches(String.valueOf(password), String.valueOf(user.getPassword())))
////		{
////			return true;
////		}
////		else 
////		{
////			return false;
////		}
////	}
//	
//	@Override
//	@Transactional(rollbackFor = RecordNotFoundException.class)
//	public User updateUser(User user) throws RecordNotFoundException
//	{
//		authDao.updateUser(user);
//		return this.searchUserByUserId(user.getUserId());
//	}
//	
//	@Override
//	public boolean passwordMatch(int userId, char[] password) throws RecordNotFoundException
//	{
//		User user = this.searchUserByUserId(userId);
//		
//		PasswordEncoder pe = new BCryptPasswordEncoder();
//		if (pe.matches(String.valueOf(password), String.valueOf(user.getPassword())))
//		{
//			return true;
//		}
//		else 
//		{
//			return false;
//		}
//	}
//	
//	@Override
//	public void changeUserPassword(int userId, char[] currentPassword, char[] newPassword) throws RecordNotFoundException, IncorrectPasswordException, RecordNotChangedException
//	{
//		if (this.passwordMatch(userId, currentPassword))
//		{
//			// Change the password
//			PasswordEncoder pe = new BCryptPasswordEncoder();
//			newPassword = pe.encode(String.valueOf(newPassword)).toCharArray();
//			authDao.changeUserPassword(userId, newPassword);
//			
//			// Check if user password was actually changed
//			if (this.passwordMatch(userId, currentPassword))
//			{
//				throw new RecordNotChangedException("Unknown error - The user password was not changed");
//			}
//		}
//		else
//		{
//			throw new IncorrectPasswordException();
//		}
//	}
//	
//
////	@Override
////	public void changePassword(int userId, char[] oldPassword, char[] newPassword) throws RecordNotFoundException
////	{
////		User user = this.searchUserByUserId(userId);
////		
////		PasswordEncoder pe = new BCryptPasswordEncoder();
////		if (pe.matches(oldPassword.toString(), user.getPassword().toString()))
////		{
////			user.setPassword(pe.encode(newPassword.toString()).toCharArray());
////		}
////
////	}
//
//	@Override
//	@Transactional(rollbackFor = {RecordNotFoundException.class, RecordNotDeletedException.class})
//	public void deleteUser(int userId) throws RecordNotFoundException, RecordNotDeletedException
//	{
//		// TODO: Prevent this method from deleting the last user in the administrator role
//		// TODO: Provide error message if user tries to delete the last administrator user
//		
//		try
//		{
//			authDao.deleteUser(userId); 
//		} 
//		catch (NoResultException e)
//		{
//			throw new RecordNotFoundException("The user you are trying to delete could not be found");
//		}
//		
//		if (authDao.existingUser(userId))
//		{
//			throw new RecordNotDeletedException("The user could not be deleted");
//		}
//	}

	@Override
	public void saveRole(Role role, String signedUsername) throws NoResourceAccessException
	{
		Resource roleResource = abacService.searchResourceByName("Role");
		User loggedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy roleUpdate = systemService.searchPolicy(roleResource, PolicyType.UPDATE);
		UserAttribs userAttribs = systemService.getUserAttribs(loggedUser.getUserId());
		
		if (roleUpdate.getModifyPolicy(null, userAttribs, loggedUser))
		{
			// TODO: Return an exception if the role is not created
			
			role.clearRestrictedFields();
			authDao.createRole(role);
			authDao.clearEntityManager();
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
//
//	@Override
//	public List<Role> searchAllRoles()
//	{
//		return authDao.getAllRoles();
//	}
//
//	@Override
//	public List<Role> searchRolesByRange(int page, int elements)
//	{
//		return authDao.getAllRolesByRange(page - 1, elements);
//	}

	@Override
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public Role searchRoleById(int roleId, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		Resource roleResource = abacService.searchResourceByName("Role");
		User loggedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy roleRead = systemService.searchPolicy(roleResource, PolicyType.READ);
		ResourceReadPolicy readPolicy = roleRead.getReadPolicy("role", "project", loggedUser);
		
		if (readPolicy.isReadGranted())
		{
			try
			{
				Role role = authDao.getRoleById(roleId, readPolicy.getReadConditions());
				authDao.clearEntityManager();
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
		ResourceReadPolicy readPolicy = roleRead.getReadPolicy("role", "project", loggedUser);
		
		if (readPolicy.isReadGranted())
		{
			try
			{
				Role role = authDao.getRoleByName(roleName, readPolicy.getReadConditions());
				authDao.clearEntityManager();
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
		ResourceReadPolicy readPolicy = roleRead.getReadPolicy("role", "project", loggedUser);
		
		if (readPolicy.isReadGranted())
		{
			try
			{
				Role role = authDao.getRoleByNameWithRestrictedFields(roleName, readPolicy.getReadConditions());
				authDao.clearEntityManager();
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
//	// TODO: Check if this method is actually needed - delete it if not being used
//	@Override
//	@Transactional(rollbackFor = RecordNotFoundException.class)
//	public Role searchRoleByIdWithMembers(int id) throws RecordNotFoundException
//	{
//		try
//		{
//			return authDao.getRoleByIdWithMembers(id);
//		}
//		catch (NoResultException e)
//		{
//			throw new RecordNotFoundException("The role could not be found");
//		}
//	}
//	
//	@Override
//	public List<User> searchRoleNonMembers(int roleId, String searchCriteria)
//	{
//		return authDao.getRoleNonMembersByCriteria(roleId, searchCriteria);
//	}
	
	@Override
	public void updateRole(Role role, String signedUser) throws NoResourceAccessException
	{
		Resource roleResource = abacService.searchResourceByName("Role");
		User loggedUser = systemService.searchFullUserByUsername(signedUser);
		
		AbacPolicy rolePolicy = systemService.searchPolicy(roleResource, PolicyType.UPDATE);
		ResourceAttribs resourceAttribs = this.getRoleResourceAttribs(role.getRoleId());
		UserAttribs userAttribs = systemService.getUserAttribs(loggedUser.getUserId());
		
		if (rolePolicy.getModifyPolicy(resourceAttribs, userAttribs, loggedUser))
		{
			role.clearRestrictedFields();
			authDao.updateRole(role);
			authDao.clearEntityManager();
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
//	
//	@Override
//	@Transactional(rollbackFor = {RecordNotFoundException.class, RecordNotDeletedException.class})
//	public void deleteRole(int roleId) throws RecordNotFoundException, RecordNotDeletedException
//	{
//		// TODO: Prevent this method from deleting the original administrator role
//		// TODO: Provide an error message if the user tries to delete the original administrator role
//		
//		try
//		{
//			authDao.deleteRole(roleId);
//		}
//		catch (NoResultException e)
//		{
//			throw new RecordNotFoundException("The role to be deleted could not be found");
//		}
//		
//		if (authDao.existingRole(roleId))
//		{
//			throw new RecordNotDeletedException("Error: The role could not be deleted");
//		}
//	}
//
//	@Override
//	@Transactional(rollbackFor = RecordNotFoundException.class)
//	public void assignUserToRole(int userId, int roleId) throws RecordNotFoundException
//	{
//		try
//		{
//			authDao.assignUserToRole(userId, roleId);
//		}
//		catch (NoResultException e)
//		{
//			throw new RecordNotFoundException("Error: The role or some users scheduled to be added as members of the role could not be found");
//		}
//		
//		// TODO: Need to create some checking and throw an exception when the user is not added to the role
//	}
//	
//	@Override
//	@Transactional(rollbackFor = RecordNotFoundException.class)
//	public void removeRoleMember(int userId, int roleId) throws RecordNotFoundException
//	{
//		try
//		{
//			authDao.removeUserFromRole(userId, roleId);
//		}
//		catch (NoResultException e)
//		{
//			throw new RecordNotFoundException("Error: The role or some members scheduled to be removed from the role could not be found");
//		}
//		
//		// TODO: Create some checking and throw a new exception if the member is not removed.
//	}
	
	private ResourceAttribs getRoleResourceAttribs(int roleId)
	{
		Role role = authDao.getRolePathWithFullEmployees(roleId);
		List<User> users = role.getUsers();
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
			resourceAttribs.addProjectManager(employee.getPmProjectNames());
			resourceAttribs.addProjectSuperintendent(employee.getSuperintendentProjectNames());
			resourceAttribs.addProjectForman(employee.getForemanProjectNames());
		}
		
		return resourceAttribs;
	}
}
