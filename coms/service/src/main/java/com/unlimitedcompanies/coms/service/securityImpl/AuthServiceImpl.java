package com.unlimitedcompanies.coms.service.securityImpl;

import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.AuthDao;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.exceptions.IncorrectPasswordException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotChangedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotCreatedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotDeletedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.AuthService;
import com.unlimitedcompanies.coms.service.security.ContactService;

@Service
@Transactional
public class AuthServiceImpl implements AuthService
{
	@Autowired
	private AuthDao authDao;

	@Autowired
	private ContactService contactService;

	@Override
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public User saveUser(User user) throws RecordNotFoundException, RecordNotCreatedException
	{
		if (user.getContact() == null)
		{
			String exceptionMessage = "The contact associated with the user you are trying to create could not be found";
			throw new RecordNotFoundException(exceptionMessage);
		}

		Contact contact = contactService.searchContactById(user.getContact().getContactId());
		user.setContact(contact);
		
		PasswordEncoder pe = new BCryptPasswordEncoder();
		String encoded = pe.encode(String.valueOf(user.getPassword()));
		char[] passencoded = encoded.toCharArray();
		user.setPassword(passencoded);

		authDao.createUser(user);
		try
		{
			User savedUser = this.searchUserByUsername(user.getUsername());
			return savedUser;
		} 
		catch (RecordNotFoundException e)
		{
			throw new RecordNotCreatedException();
		}
	}
	
	@Override
	public int searchNumberOfUsers()
	{
		return authDao.getNumberOfUsers();
	}
	
	@Override
	public boolean hasNextUser(int page, int elements)
	{
		List<User> foundUsers = authDao.getUsersByRange((page - 1) * elements, 1);
		if (foundUsers.isEmpty()) 
		{
			return false;
		}
		else 
		{
			return true;
		}
	}

	@Override
	public List<User> searchAllUsers()
	{
		return authDao.getAllUsers();
	}
	
	@Override
	public List<User> searchUsersByRange(int page, int elements)
	{
		return authDao.getUsersByRange(page - 1, elements);
	}

	@Override
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public User searchUserByUserId(Integer id) throws RecordNotFoundException
	{
		try
		{
			return authDao.getUserByUserId(id);
		} 
		catch (NoResultException e)
		{
			throw new RecordNotFoundException("The user could not be found");
		}
	}

	@Override
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public User searchUserByUsername(String username) throws RecordNotFoundException
	{
		try
		{
			return authDao.getUserByUsername(username);
		} 
		catch (NoResultException e)
		{
			throw new RecordNotFoundException("The user could not be found");
		}
	}
	
	@Override
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public User searchUserByContact(Contact contact) throws RecordNotFoundException
	{
		try
		{
			return authDao.getUserByContact(contact);
		} 
		catch (NoResultException e)
		{
			throw new RecordNotFoundException("The user could not be found");
		}
	}
	
	@Override
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public User searchUserByUserIdWithContact(int userId) throws RecordNotFoundException
	{
		try
		{
			return authDao.getUserByUserIdWithContact(userId);
		}
		catch (NoResultException e)
		{
			throw new RecordNotFoundException("The user could not be found");
		}
	}
	
	@Override
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public User searchUserByUsernameWithContact(String username) throws RecordNotFoundException
	{
		try
		{
			return authDao.getUserByUsernameWithContact(username);
		} 
		catch (NoResultException e)
		{
			throw new RecordNotFoundException("The user could not be found");
		}
	}
	
//	@Override
//	public User searchAUserByIdWithRoles(int userId)
//	{
//		return authDao.getAUserByIdWithRoles(userId);
//	}
	
	@Override
	public User searchFullUserByUserId(int id)
	{
		return authDao.getFullUserByUserId(id);
	}

	@Override
	public User searchFullUserByUsername(String username)
	{
		return authDao.getFullUserByUsername(username);
	}
	
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
	
	@Override
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public User updateUser(User user) throws RecordNotFoundException
	{
		authDao.updateUser(user);
		return this.searchUserByUserId(user.getUserId());
	}
	
	@Override
	public boolean passwordMatch(int userId, char[] password) throws RecordNotFoundException
	{
		User user = this.searchUserByUserId(userId);
		
		PasswordEncoder pe = new BCryptPasswordEncoder();
		if (pe.matches(String.valueOf(password), String.valueOf(user.getPassword())))
		{
			return true;
		}
		else 
		{
			return false;
		}
	}
	
	@Override
	public void changeUserPassword(int userId, char[] currentPassword, char[] newPassword) throws RecordNotFoundException, IncorrectPasswordException, RecordNotChangedException
	{
		if (this.passwordMatch(userId, currentPassword))
		{
			// Change the password
			PasswordEncoder pe = new BCryptPasswordEncoder();
			newPassword = pe.encode(String.valueOf(newPassword)).toCharArray();
			authDao.changeUserPassword(userId, newPassword);
			
			// Check if user password was actually changed
			if (this.passwordMatch(userId, currentPassword))
			{
				throw new RecordNotChangedException("Unknown error - The user password was not changed");
			}
		}
		else
		{
			throw new IncorrectPasswordException();
		}
	}
	

//	@Override
//	public void changePassword(int userId, char[] oldPassword, char[] newPassword) throws RecordNotFoundException
//	{
//		User user = this.searchUserByUserId(userId);
//		
//		PasswordEncoder pe = new BCryptPasswordEncoder();
//		if (pe.matches(oldPassword.toString(), user.getPassword().toString()))
//		{
//			user.setPassword(pe.encode(newPassword.toString()).toCharArray());
//		}
//
//	}

	@Override
	@Transactional(rollbackFor = {RecordNotFoundException.class, RecordNotDeletedException.class})
	public void deleteUser(int userId) throws RecordNotFoundException, RecordNotDeletedException
	{
		// TODO: Prevent this method from deleting the last user in the administrator role
		// TODO: Provide error message if user tries to delete the last administrator user
		
		try
		{
			authDao.deleteUser(userId); 
		} 
		catch (NoResultException e)
		{
			throw new RecordNotFoundException("The user you are trying to delete could not be found");
		}
		
		if (authDao.existingUser(userId))
		{
			throw new RecordNotDeletedException("The user could not be deleted");
		}
	}

	@Override
	@Transactional(rollbackFor = RecordNotCreatedException.class)
	public Role saveRole(Role role) throws RecordNotCreatedException
	{
		authDao.createRole(role.getRoleName());
		
		// TODO: Return an exception if the role is not created
		
		try
		{
			return this.searchRoleByName(role.getRoleName());
		}
		catch (RecordNotFoundException e)
		{
			throw new RecordNotCreatedException();
		}
	}
	
	@Override
	public int searchNumberOfRoles()
	{
		return authDao.getNumberOfRoles();
	}
	
	@Override
	public boolean hasNextRole(int page, int elements)
	{
		List<Role> foundRoles = authDao.getAllRolesByRange((page - 1) * elements, 1);
		if (foundRoles.isEmpty()) 
		{
			return false;
		}
		else 
		{
			return true;
		}
	}

	@Override
	public List<Role> searchAllRoles()
	{
		return authDao.getAllRoles();
	}

	@Override
	public List<Role> searchRolesByRange(int page, int elements)
	{
		return authDao.getAllRolesByRange(page - 1, elements);
	}

	@Override
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public Role searchRoleById(int id) throws RecordNotFoundException
	{
		// TODO: Throw an exception if the role is not found
		
		try
		{
			return authDao.getRoleById(id);
		}
		catch (NoResultException e)
		{
			throw new RecordNotFoundException("The role could not be found");
		}
	}
	
	@Override
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public Role searchRoleByName(String roleName) throws RecordNotFoundException
	{
		try
		{
			return authDao.getRoleByRoleName(roleName);
		}
		catch (NoResultException e)
		{
			throw new RecordNotFoundException("The role could not be found");
		}
	}

	// TODO: Check if this method is actually needed - delete it if not being used
	@Override
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public Role searchRoleByIdWithMembers(int id) throws RecordNotFoundException
	{
		try
		{
			return authDao.getRoleByIdWithMembers(id);
		}
		catch (NoResultException e)
		{
			throw new RecordNotFoundException("The role could not be found");
		}
	}
	
	@Override
	public List<User> searchRoleNonMembers(int roleId, String searchCriteria)
	{
		return authDao.getRoleNonMembersByCriteria(roleId, searchCriteria);
	}
	
	@Override
	@Transactional(rollbackFor = {RecordNotFoundException.class, RecordNotChangedException.class})
	public Role updateRole(Role editRole) throws RecordNotFoundException, RecordNotChangedException
	{
		Role foundRole = this.searchRoleById(editRole.getRoleId());
		String originalFoundRoleName = foundRole.getRoleName();
		
		authDao.updateRole(editRole);

		if (foundRole.getRoleName().equals(originalFoundRoleName))
		{
			throw new RecordNotChangedException("Error: The role details have not been changed");
		}
		
		return foundRole;
	}
	
	@Override
	@Transactional(rollbackFor = {RecordNotFoundException.class, RecordNotDeletedException.class})
	public void deleteRole(int roleId) throws RecordNotFoundException, RecordNotDeletedException
	{
		// TODO: Prevent this method from deleting the original administrator role
		// TODO: Provide an error message if the user tries to delete the original administrator role
		
		try
		{
			authDao.deleteRole(roleId);
		}
		catch (NoResultException e)
		{
			throw new RecordNotFoundException("The role to be deleted could not be found");
		}
		
		if (authDao.existingRole(roleId))
		{
			throw new RecordNotDeletedException("Error: The role could not be deleted");
		}
	}

	@Override
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public void assignUserToRole(int userId, int roleId) throws RecordNotFoundException
	{
		try
		{
			authDao.assignUserToRole(userId, roleId);
		}
		catch (NoResultException e)
		{
			throw new RecordNotFoundException("Error: The role or some users scheduled to be added as members of the role could not be found");
		}
		
		// TODO: Need to create some checking and throw an exception when the user is not added to the role
	}
	
	@Override
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public void removeRoleMember(int userId, int roleId) throws RecordNotFoundException
	{
		try
		{
			authDao.removeUserFromRole(userId, roleId);
		}
		catch (NoResultException e)
		{
			throw new RecordNotFoundException("Error: The role or some members scheduled to be removed from the role could not be found");
		}
		
		// TODO: Create some checking and throw a new exception if the member is not removed.
	}

}
