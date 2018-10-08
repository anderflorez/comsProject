package com.unlimitedcompanies.coms.securityService;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.AndCondition;
import com.unlimitedcompanies.coms.domain.security.AndGroup;
import com.unlimitedcompanies.coms.domain.security.OrCondition;
import com.unlimitedcompanies.coms.domain.security.OrGroup;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.ResourcePermissions;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.securityServiceExceptions.NonExistingContactException;

public interface AuthService
{
	public int findNumberOfUsers();
	public User saveUser(User user) throws NonExistingContactException;
//	public User updateUser(Integer userId, User user);
//
//	public List<User> findAllUsers();
	public User findUserByUserId(Integer id);
	public User searchUserByUsername(String string);
	public User searchUserByUsernameWithContact(String username);
	public User searchFullUserByUsername(String username);
	
	public int findNumberOfRoles();
	public Role saveRole(Role role);
//	public Role updateRole(Integer roleId, Role role);
//	public List<Role> findAllRoles();
//	public Role findRoleById(int id);
//	public Role findRoleByIdWithMembers(Integer id);
	public Role findRoleByRoleName(String roleName);
//	
	public void assignUserToRole(User user, Role role);
//	public void removeUserFromRole(Role role, User user);
	
	public ResourcePermissions savePermission(ResourcePermissions permission);
	public ResourcePermissions searchPermissionById(String id);
	public List<ResourcePermissions> searchAllRolePermissions(Role role);
	
	public AndGroup saveAndGroup(AndGroup andGroup);
	public AndGroup searchAndGroupById(String andGroupId);
	public void saveAndCondition(AndCondition andCondition);
	public OrGroup saveOrGroup(OrGroup orGroup);
	public OrGroup searchOrGroupById(String orGroupId);
	public void saveOrCondition(OrCondition orCondition);
	public AndGroup fullAndGroupSearch(AndGroup andGroup);
}
