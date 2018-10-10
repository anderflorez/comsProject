package com.unlimitedcompanies.coms.dao.security;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.AndCondition;
import com.unlimitedcompanies.coms.domain.security.AndGroup;
import com.unlimitedcompanies.coms.domain.security.OrCondition;
import com.unlimitedcompanies.coms.domain.security.OrGroup;
import com.unlimitedcompanies.coms.domain.security.ResourcePermissions;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;

public interface AuthDao
{
	public int getNumberOfUsers();
	public void createUser(User user);
	public void updateUser(int userId, User user);
	public List<User> getAllUsers();
	public User getUserByUserId(int id);
	public User getUserByUsername(String username);
	public User getUserByUsernameWithContact(String username);
	public User getFullUserByUsername(String username);
	
	public int getNumberOfRoles();
	public void createRole(Role role);
//	public List<Role> getAllRoles();
//	public Role searchRoleById(int id);
	public Role getRoleByRoleName(String roleName);
//	public Role getRoleByIdWithMembers(Integer id);
//	public void updateRole(Integer roleId, Role role);
//	
	public void assignUserToRole(User user, Role role);
//	public void removeUserFromRole(Role role, User user);
	
	public int getNumberOfPermissions();
	public void createResourcePermission(ResourcePermissions newPermission);
	public ResourcePermissions searchPermissionById(String id);
	public List<ResourcePermissions> getAllRolePermissions(Role role);
	
	public void createAndGroup(AndGroup condition);
	public AndGroup getAndGroupById(String andGroupId);
	public void createAndCondition(AndCondition condition);	
	public void createOrGroup(OrGroup orGroup);
	public OrGroup getOrGroupById(String orGroupId);
//	public List<OrGroup> getOrGroups(int andGroupId);
	public void createOrCondition(OrCondition orCondition);
	public List<AndGroup> getAssociatedAndGroups(OrGroup orGroup);
	public List<OrGroup> getAssociatedOrGroups(AndGroup andGroup);
}
