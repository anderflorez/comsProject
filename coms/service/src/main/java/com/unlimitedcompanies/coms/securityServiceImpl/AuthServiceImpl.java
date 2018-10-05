package com.unlimitedcompanies.coms.securityServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.AuthDao;
import com.unlimitedcompanies.coms.dao.security.ContactDao;
import com.unlimitedcompanies.coms.domain.security.AndCondition;
import com.unlimitedcompanies.coms.domain.security.AndGroup;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.OrCondition;
import com.unlimitedcompanies.coms.domain.security.OrGroup;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.ResourcePermissions;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.securityService.AuthService;
import com.unlimitedcompanies.coms.securityServiceExceptions.NonExistingContactException;

@Service
@Transactional
public class AuthServiceImpl implements AuthService
{
	@Autowired
	private AuthDao authDao;

	@Autowired
	private ContactDao contactDao;

	@Override
	public int findNumberOfUsers()
	{
		return authDao.getNumberOfUsers();
	}

	@Override
	public User saveUser(User user) throws NonExistingContactException
	{
		if (user.getContact() == null)
		{
			throw new NonExistingContactException();
		}

		Contact contact = contactDao.getContactById(user.getContact().getContactId());
		user.setContact(contact);

		authDao.createUser(user);
		return this.searchUserByUsername(user.getUsername());
	}

//	@Override
//	public User updateUser(Integer userId, User user)
//	{
//		authDao.updateUser(userId, user);
//		return this.findUserByUserId(userId);
//	}
//
//	@Override
//	public List<User> findAllUsers()
//	{
//		return authDao.getAllUsers();
//	}

	@Override
	public User findUserByUserId(Integer id)
	{
		return authDao.getUserByUserId(id);
	}

	@Override
	public User searchUserByUsername(String username)
	{
		return authDao.getUserByUsername(username);
	}

	@Override
	public User searchUserByUsernameWithContact(String username)
	{
		return authDao.getUserByUsernameWithContact(username);
	}

	@Override
	public User searchFullUserByUsername(String username)
	{
		return authDao.getFullUserByUsername(username);
	}

	@Override
	public int findNumberOfRoles()
	{
		return authDao.getNumberOfRoles();
	}

	@Override
	public Role saveRole(Role role)
	{
		authDao.createRole(role);
		return this.findRoleByRoleName(role.getRoleName());
	}

//	@Override
//	public Role updateRole(Integer roleId, Role role)
//	{
//		authDao.updateRole(roleId, role);
//		return this.findRoleById(roleId);
//	}
//
//	@Override
//	public List<Role> findAllRoles()
//	{
//		return authDao.getAllRoles();
//	}
//
//	@Override
//	public Role findRoleById(int id)
//	{
//		return authDao.searchRoleById(id);
//	}
//
//	@Override
//	public Role findRoleByIdWithMembers(Integer id)
//	{
//		return authDao.getRoleByIdWithMembers(id);
//	}

	@Override
	public Role findRoleByRoleName(String roleName)
	{
		return authDao.getRoleByRoleName(roleName);
	}

	@Override
	public void assignUserToRole(User user, Role role)
	{
		authDao.assignUserToRole(user, role);
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

}
