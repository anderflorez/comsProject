package com.unlimitedcompanies.coms.dao.security;

import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;

public interface AuthDao
{
	public int getNumberOfUsers();
	public void createUser(User user);
//	public void updateUser(int userId, User user);
//	public List<User> getAllUsers();
//	public User searchUserByUserId(int id);
	public User getUserByUsername(String username);
//	public User searchUserByUsernameWithContact(String username);
//	
	public int getNumberOfRoles();
	public void createRole(Role role);
//	public List<Role> getAllRoles();
//	public Role searchRoleById(int id);
	public Role getRoleByRoleName(String roleName);
//	public Role getRoleByIdWithMembers(Integer id);
//	public void updateRole(Integer roleId, Role role);
//	
//	public int findNumberOfUser_RoleAssignments();
//	
	public void assignUserToRole(User user, Role role);
//	public void removeUserFromRole(Role role, User user);
	
	public int getNumberOfPermissions();
}
