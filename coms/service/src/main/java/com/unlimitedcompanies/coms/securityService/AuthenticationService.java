package com.unlimitedcompanies.coms.securityService;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.securityServiceExceptions.NonExistingContactException;

public interface AuthenticationService
{
	public int findNumberOfUsers();
	public User saveUser(User user) throws NonExistingContactException;
	public List<User> findAllUsers();
	public User findUserByUsername(String string);
	public int findNumberOfRoles();
	public Role saveRole(Role role);
	public Role findRoleByRoleName(String roleName);
	
	public int findNumberOfAssignments();
	public void assignUserToRole(Role role, User user);
	public void removeUserFromRole(Role role, User user);
}
