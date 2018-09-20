package com.unlimitedcompanies.coms.securityService;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.securityServiceExceptions.NonExistingContactException;

public interface AuthenticationService
{
	public int findNumberOfUsers();
	public User saveUser(User user) throws NonExistingContactException;
	public User updateUser(Integer userId, User user);

	public List<User> findAllUsers();
	public User findUserByUserId(Integer id);
	public User findUserByUsername(String string);
	public User findUserByUsernameWithContact(String username);
	
	public int findNumberOfRoles();
	public Role saveRole(Role role);
	public Role updateRole(Integer roleId, Role role);
	public List<Role> findAllRoles();
	public Role findRoleById(int id);
	public Role findRoleByIdWithMembers(Integer id);
	public Role findRoleByRoleName(String roleName);
	
	public int findNumberOfAssignments();
	public void assignUserToRole(Role role, User user);
	public void removeUserFromRole(Role role, User user);
	
}
