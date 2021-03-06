package com.unlimitedcompanies.coms.service.security;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
import com.unlimitedcompanies.coms.service.exceptions.IncorrectPasswordException;
import com.unlimitedcompanies.coms.service.exceptions.LastAdminRemovalException;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotDeletedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;

public interface AuthService
{
	public void saveUser(User user, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException, DuplicateRecordException;
	public int searchNumberOfUsers();
	public List<User> searchAllUsers(String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
//	public boolean hasNextUser(int page, int elements);
	public List<User> searchAllUsers(int elements, int page, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public User searchUserById(int id, String signedUsername) throws RecordNotFoundException, NoResourceAccessException;
	public User searchUserByUsername(String username, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException;
	public User searchUserByIdWithContact(int userId, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public User searchUserByUsernameWithContact(String username, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public User searchUserByContact(Contact contact, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public User searchFullUserByUsername(String username, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public void updateUser(User user, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public void changeUserPassword(User user, String currentPassword, String newPassword, String signedUsername) 
			throws IncorrectPasswordException, NoResourceAccessException, RecordNotFoundException;
	public void deleteUser(int userId, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException, RecordNotDeletedException, LastAdminRemovalException;
	
	public void saveRole(Role role, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public int searchNumberOfRoles();
//	public boolean hasNextRole(int page, int elements);
	public List<Role> searchAllRoles(String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public List<Role> searchAllRoles(int elements, int page, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public Role searchRoleById(int roleId, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public Role searchRoleByName(String roleName, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public Role searchRoleByNameWithRestrictedFields(String roleName, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException;
	public Role searchRoleByIdWithMembers(int roleId, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public Role searchRoleByNameWithMembers(String roleName, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
//	public List<User> searchRoleNonMembers(int roleId, String searchCriteria);
	public void updateRole(Role role, String signedUsername) throws NoResourceAccessException, RecordNotFoundException, LastAdminRemovalException;
	public void deleteRole(int roleId, String signedUsername) throws NoResourceAccessException, RecordNotFoundException, LastAdminRemovalException;

	public void assignUserToRole(int userId, int roleId, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public void removeRoleMember(int userId, int roleId, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException, LastAdminRemovalException;
	
}
