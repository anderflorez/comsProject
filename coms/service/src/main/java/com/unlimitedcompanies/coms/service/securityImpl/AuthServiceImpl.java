package com.unlimitedcompanies.coms.service.securityImpl;

import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.AuthDao;
import com.unlimitedcompanies.coms.domain.security.AndCondition;
import com.unlimitedcompanies.coms.domain.security.AndGroup;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.OrCondition;
import com.unlimitedcompanies.coms.domain.security.OrGroup;
import com.unlimitedcompanies.coms.domain.security.ResourcePermissions;
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
	public Role saveRole(Role role)
	{
		authDao.createRole(role);
		return this.searchRoleByRoleName(role.getRoleName());
	}
	
	@Override
	public int searchNumberOfRoles()
	{
		return authDao.getNumberOfRoles();
	}

	@Override
	public List<Role> searchAllRoles()
	{
		return authDao.getAllRoles();
	}

	@Override
	public Role searchRoleById(String id)
	{
		return authDao.getRoleById(id);
	}

	@Override
	public Role searchRoleByIdWithMembers(String id)
	{
		return authDao.getRoleByIdWithMembers(id);
	}

	@Override
	public Role searchRoleByRoleName(String roleName)
	{
		return authDao.getRoleByRoleName(roleName);
	}
	
	@Override
	public Role updateRole(String roleId, Role role)
	{
		authDao.updateRole(roleId, role);
		return this.searchRoleById(roleId);
	}
	
	@Override
	public void deleteRole(String roleId)
	{
		authDao.deleteRole(roleId);
	}

	@Override
	public void assignUserToRole(User user, Role role)
	{
		authDao.assignUserToRole(user.getUserId().intValue(), role.getRoleId());
	}
	
//	@Override
//	public void removeUserFromRole(Role role, User user)
//	{
//		authDao.removeUserFromRole(role, user);
//	}

	@Override
	public ResourcePermissions savePermission(ResourcePermissions permission)
	{
		authDao.createResourcePermission(permission);
		if (permission.getViewCondtitions() != null && permission.getViewCondtitions().getOrGroups().size() > 0)
		{
			for (OrGroup o : permission.getViewCondtitions().getOrGroups())
			{
				this.saveFullOrGroup(o);
			}
		}
		return this.searchPermissionById(permission.getPermissionId());
	}
	
	@Override
	public ResourcePermissions searchPermissionById(String id)
	{
		return authDao.searchPermissionById(id);
	}
	
	@Override
	public List<ResourcePermissions> searchAllRolePermissions(Role role)
	{
		return authDao.getAllRolePermissions(role);
	}

	@Override
	public AndGroup saveAndGroup(AndGroup andGroup)
	{
		authDao.createAndGroup(andGroup);
		return authDao.getAndGroupById(andGroup.getAndGroupId());
	}

	@Override
	public AndGroup searchAndGroupById(String andGroupId)
	{
		return authDao.getAndGroupById(andGroupId);
	}

	@Override
	public void saveAndCondition(AndCondition andCondition)
	{
		authDao.createAndCondition(andCondition);		
	}

	@Override
	public OrGroup saveOrGroup(OrGroup orGroup)
	{
		authDao.createOrGroup(orGroup);
		return authDao.getOrGroupById(orGroup.getOrGroupId());
	}

	@Override
	public OrGroup searchOrGroupById(String orGroupId)
	{
		return authDao.getOrGroupById(orGroupId);
	}

	@Override
	public void saveOrCondition(OrCondition orCondition)
	{
		authDao.createOrCondition(orCondition);
	}
	
	private void saveFullAndGroup(AndGroup andGroup)
	{
		if (!andGroup.getOrGroups().isEmpty())
		{
			for (OrGroup o : andGroup.getOrGroups())
			{
				this.saveFullOrGroup(o);
			}
		}
		authDao.createAndGroup(andGroup);
	}
	
	private void saveFullOrGroup(OrGroup orGroup)
	{
		if (!orGroup.getAndGroups().isEmpty())
		{
			for (AndGroup a : orGroup.getAndGroups())
			{
				this.saveFullAndGroup(a);
			}
		}
		authDao.createOrGroup(orGroup);
	}
	
	@Override
	public AndGroup fullAndGroupSearch(AndGroup andGroup)
	{
		List<OrGroup> orGroups = this.searchAssociatedOrGroups(andGroup);
		if (!orGroups.isEmpty())
		{
			for (OrGroup o : orGroups)
			{
				o = this.fullOrGroupSearch(o);				
			}
			andGroup.assignOrGroupList(orGroups);
		}
		return andGroup;
	}
	
	private OrGroup fullOrGroupSearch(OrGroup orGroup)
	{
		List<AndGroup> andGroups = this.searchAssociatedAndGroups(orGroup);
		if (!andGroups.isEmpty())
		{
			for (AndGroup a : andGroups)
			{
				a = this.fullAndGroupSearch(a);
			}
			orGroup.assignAndGroupList(andGroups);
		}
		return orGroup;
	}

	private List<AndGroup> searchAssociatedAndGroups(OrGroup orGroup)
	{
		return authDao.getAssociatedAndGroups(orGroup);
	}

	private List<OrGroup> searchAssociatedOrGroups(AndGroup andGroup)
	{
		return authDao.getAssociatedOrGroups(andGroup);
	}

}
