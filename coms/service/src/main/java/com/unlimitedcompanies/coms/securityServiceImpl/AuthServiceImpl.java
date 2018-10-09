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

	@Override
	public User updateUser(int userId, User user)
	{
		authDao.updateUser(userId, user);
		return this.searchUserByUserId(userId);
	}

	@Override
	public List<User> searchAllUsers()
	{
		return authDao.getAllUsers();
	}

	@Override
	public User searchUserByUserId(Integer id)
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
		if (permission.getAndGroup() != null && permission.getAndGroup().getOrGroups().size() > 0)
		{
			for (OrGroup o : permission.getAndGroup().getOrGroups())
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
