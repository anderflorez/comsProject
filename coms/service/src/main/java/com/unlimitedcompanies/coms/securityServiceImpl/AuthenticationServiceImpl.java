package com.unlimitedcompanies.coms.securityServiceImpl;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.AuthenticationDao;
import com.unlimitedcompanies.coms.dao.security.ContactDao;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.securityService.AuthenticationService;
import com.unlimitedcompanies.coms.securityServiceExceptions.NonExistingContactException;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService
{
	@Autowired
	private AuthenticationDao dao;

	@Autowired
	private ContactDao contactDao;

	@Override
	public int findNumberOfUsers()
	{
		return dao.getNumberOfUsers();
	}

	@Override
	public User saveUser(User user) throws NonExistingContactException
	{
		if (user.getContact() == null)
		{
			throw new NonExistingContactException();
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd");
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");

		String dateCreated = dateFormat.format(user.getFullDateAdded());
		String lastAccessed = dateTimeFormat.format(user.getFullLastAccessDate());

		Contact contact = contactDao.searchContactById(user.getContact().getContactId());
		user.setContact(contact);

		dao.createUser(user, dateCreated, lastAccessed);
		return this.findUserByUsername(user.getUsername());
	}

	@Override
	public User updateUser(Integer userId, User user)
	{
		dao.updateUser(userId, user);
		return this.findUserByUserId(userId);
	}

	@Override
	public List<User> findAllUsers()
	{
		return dao.getAllUsers();
	}

	@Override
	public User findUserByUserId(Integer id)
	{
		return dao.searchUserByUserId(id);
	}

	@Override
	public User findUserByUsername(String username)
	{
		return dao.searchUserByUsername(username);
	}

	@Override
	public User findUserByUsernameWithContact(String username)
	{
		return dao.searchUserByUsernameWithContact(username);
	}

	@Override
	public int findNumberOfRoles()
	{
		return dao.getNumberOfRoles();
	}

	@Override
	public Role saveRole(Role role)
	{
		dao.createRole(role);
		return this.findRoleByRoleName(role.getRoleName());
	}

	@Override
	public Role updateRole(Integer roleId, Role role)
	{
		dao.updateRole(roleId, role);
		return this.findRoleById(roleId);
	}

	@Override
	public List<Role> findAllRoles()
	{
		return dao.getAllRoles();
	}

	@Override
	public Role findRoleById(int id)
	{
		return dao.searchRoleById(id);
	}

	@Override
	public Role findRoleByIdWithMembers(Integer id)
	{
		return dao.getRoleByIdWithMembers(id);
	}

	@Override
	public Role findRoleByRoleName(String roleName)
	{
		return dao.getRoleByRoleName(roleName);
	}

	@Override
	public int findNumberOfAssignments()
	{
		return dao.findNumberOfUser_RoleAssignments();
	}

	@Override
	public void assignUserToRole(Role role, User user)
	{
		dao.assignUserToRole(role, user);
	}

	@Override
	public void removeUserFromRole(Role role, User user)
	{
		dao.removeUserFromRole(role, user);
	}

}
