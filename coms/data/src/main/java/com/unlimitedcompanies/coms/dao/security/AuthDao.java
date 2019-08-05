package com.unlimitedcompanies.coms.dao.security;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;

public interface AuthDao
{
	public void createUser(User user);
	public int getNumberOfUsers();
//	boolean existingUser(int userId);
	public List<User> getAllUsers(String accessConditions);
	public List<User> getAllUsers(int elements, int page, String accessConditions);
	public User getUserById(int id, String accessConditions);
	public User getUserByUsername(String username, String accessConditions);
	public User getUserByIdWithContact(int userId, String userAccessConditions, String contactAccessConditions);
	public User getUserByUsernameWithContact(String username, String userAccessConditions, String contactAccessConditions);
	public User getUserByContact(Contact contact, String accessConditions);
	public User getFullUserByUsername(String username, String userAccessConditions, String contactAccessConditions, String roleAccessConditions);
	public User getUserWithPathToProjects(int userId);
	public User getFullUserWithAttribs(int userId);
	public void updateUser(User user);
	public void deleteUser(int userId);
	
	public void createRole(Role role);
	public int getNumberOfRoles();
//	public boolean existingRole(int roleId);
	public List<Role> getAllRoles(String accessConditions);
	public List<Role> getAllRoles(int elements, int page, String accessConditions);
	public Role getRoleById(int id, String accessConditions);
	public Role getRoleByName(String roleName, String accessConditions);
	public Role getRoleByIdWithMembers(int roleId, String accessConditions);
	public Role getRoleByNameWithMembers(String roleName, String accessConditions);
	public Role getRoleWithPathToProjects(int roleId);
	public Role getRoleWithRestrictedFields(Integer roleId);
	public Role getRoleByNameWithRestrictedFields(String roleName, String accessConditions);
//	public List<User> getRoleNonMembersByCriteria(int roleId, String searchCriteria);
	public void updateRole(Role role);
	public void deleteRole(Role role);

	public void assignUserToRole(int userId, int roleId);
	public void removeUserFromRole(int userId, int roleId);

}
