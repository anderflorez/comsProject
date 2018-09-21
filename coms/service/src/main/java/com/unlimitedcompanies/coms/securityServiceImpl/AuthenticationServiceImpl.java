package com.unlimitedcompanies.coms.securityServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.AuthDao;
import com.unlimitedcompanies.coms.dao.security.ContactDao;
import com.unlimitedcompanies.coms.securityService.AuthenticationService;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService
{
	@Autowired
	private AuthDao authDao;

	@Autowired
	private ContactDao contactDao;

//	@Override
//	public int findNumberOfUsers()
//	{
//		return authDao.getNumberOfUsers();
//	}
//
//	@Override
//	public User saveUser(User user) throws NonExistingContactException
//	{
//		if (user.getContact() == null)
//		{
//			throw new NonExistingContactException();
//		}
//
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd");
//		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
//
//		String dateCreated = dateFormat.format(user.getFullDateAdded());
//		String lastAccessed = dateTimeFormat.format(user.getFullLastAccessDate());
//
//		Contact contact = contactDao.searchContactById(user.getContact().getContactId());
//		user.setContact(contact);
//
//		authDao.createUser(user, dateCreated, lastAccessed);
//		return this.findUserByUsername(user.getUsername());
//	}
//
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
//
//	@Override
//	public User findUserByUserId(Integer id)
//	{
//		return authDao.searchUserByUserId(id);
//	}
//
//	@Override
//	public User findUserByUsername(String username)
//	{
//		return authDao.searchUserByUsername(username);
//	}
//
//	@Override
//	public User findUserByUsernameWithContact(String username)
//	{
//		return authDao.searchUserByUsernameWithContact(username);
//	}
//
//	@Override
//	public int findNumberOfRoles()
//	{
//		return authDao.getNumberOfRoles();
//	}
//
//	@Override
//	public Role saveRole(Role role)
//	{
//		authDao.createRole(role);
//		return this.findRoleByRoleName(role.getRoleName());
//	}
//
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
//
//	@Override
//	public Role findRoleByRoleName(String roleName)
//	{
//		return authDao.getRoleByRoleName(roleName);
//	}
//
//	@Override
//	public int findNumberOfAssignments()
//	{
//		return authDao.findNumberOfUser_RoleAssignments();
//	}
//
//	@Override
//	public void assignUserToRole(Role role, User user)
//	{
//		authDao.assignUserToRole(role, user);
//	}
//
//	@Override
//	public void removeUserFromRole(Role role, User user)
//	{
//		authDao.removeUserFromRole(role, user);
//	}

}
