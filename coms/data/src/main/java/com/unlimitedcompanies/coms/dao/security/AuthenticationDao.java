package com.unlimitedcompanies.coms.dao.security;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;

public interface AuthenticationDao
{
	public int getNumberOfUsers();
	public void createUser(User user, String dateAdded, String lastAccessed);
	public void updateUser(int userId, User user);
	
	public List<User> getAllUsers();
	public User searchUserByUsername(String username);
	public User searchUserByUserId(int id);
	
	public int getNumberOfRoles();
	public void createRole(Role role);
	
	public Role searchRoleByRoleName(String roleName);
	
	public int findNumberOfUser_RoleAssignments();
	
	public void assignUserToRole(Role role, User user);
	public void removeUserFromRole(Role role, User user);
}
