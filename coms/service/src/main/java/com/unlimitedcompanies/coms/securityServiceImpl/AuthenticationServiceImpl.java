package com.unlimitedcompanies.coms.securityServiceImpl;

import java.text.SimpleDateFormat;

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
		if (user.getContact().getContactId() == null)
		{
			throw new NonExistingContactException();
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd");
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
		
		String dateCreated = dateFormat.format(user.getDateAdded());
		String lastAccessed = dateTimeFormat.format(user.getLastAccess());
		
		Contact contact = contactDao.searchContactById(user.getContact().getContactId());
		user.setContact(contact);
		
		dao.createUser(user, dateCreated, lastAccessed);
		return this.findUserByUsername(user.getUsername());
	}

	@Override
	public User findUserByUsername(String username)
	{
		return dao.searchUserByUsername(username);
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
	public Role findRoleByRoleName(String roleName)
	{
		return dao.searchRoleByRoleName(roleName);
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
