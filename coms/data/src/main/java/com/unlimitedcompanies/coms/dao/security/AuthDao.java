package com.unlimitedcompanies.coms.dao.security;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.AndCondition;
import com.unlimitedcompanies.coms.domain.security.AndGroup;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.OrCondition;
import com.unlimitedcompanies.coms.domain.security.OrGroup;
import com.unlimitedcompanies.coms.domain.security.ResourcePermissions;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;

public interface AuthDao
{
	public void createUser(User user);
	public int getNumberOfUsers();
	boolean existingUser(int userId);
	public List<User> getAllUsers();
	public List<User> getUsersByRange(int page, int elements);
	public User getUserByUserId(int id);
	public User getUserByUsername(String username);
	public User getUserByContact(Contact contact);
	public User getUserByUserIdWithContact(int userId);
	public User getUserByUsernameWithContact(String username);
//	public User getAUserByIdWithRoles(int userId);
	public User getFullUserByUserId(int id);
	public User getFullUserByUsername(String username);
	public void updateUser(User user);
	public void changeUserPassword(int userId, char[] newPassword);
	public void deleteUser(int userId);
	
	public Role createAdminRole();
	public void createRole(String roleName);
	public int getNumberOfRoles();
	public boolean existingRole(int roleId);
	public List<Role> getAllRoles();
	public List<Role> getAllRolesByRange(int page, int elements);
	public Role getRoleById(int id);
	public Role getRoleByRoleName(String roleName);
	public Role getRoleByIdWithMembers(int id);
	public void updateRole(Role role);
	public void deleteRole(int roleId);

	public void assignUserToRole(int userId, int roleId);
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
