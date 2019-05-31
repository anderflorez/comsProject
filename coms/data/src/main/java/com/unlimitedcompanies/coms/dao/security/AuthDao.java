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
//	public List<User> getAllUsers();
//	public List<User> getUsersByRange(int page, int elements);
	public User getUserByUserId(int id, String accessConditions);
	public User getUserByUsername(String username, String accessConditions);
//	public User getUserByContact(Contact contact);
//	public User getUserByUserIdWithContact(int userId);
//	public User getUserByUsernameWithContact(String username);
////	public User getAUserByIdWithRoles(int userId);
//	public User getFullUserByUserId(int id);
	public User getFullUserByUsername(String username, String accessConditions);
	public User getFullUserWithAttribs(int userId);
//	public void updateUser(User user);
//	public void changeUserPassword(int userId, char[] newPassword);
//	public void deleteUser(int userId);
//	
	public void createRole(Role role);
	public int getNumberOfRoles();
//	public boolean existingRole(int roleId);
//	public List<Role> getAllRoles();
//	public List<Role> getAllRolesByRange(int page, int elements);
	public Role getRoleById(int id, String accessConditions);
	public Role getRoleByName(String roleName, String accessConditions);
//	public Role getRoleByIdWithMembers(int id);
	public Role getRolePathWithFullEmployees(int roleId);
	public Role getRoleWithRestrictedFields(Integer roleId);
//	public List<User> getRoleNonMembersByCriteria(int roleId, String searchCriteria);
	public void updateRole(Role role);
//	public void deleteRole(int roleId);
//
	public void assignUserToRole(int userId, int roleId);
//	public void removeUserFromRole(int userId, int roleId);
	
	
	public void clearEntityManager();

}
