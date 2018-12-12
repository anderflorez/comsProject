package com.unlimitedcompanies.coms.service.security;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.AndCondition;
import com.unlimitedcompanies.coms.domain.security.AndGroup;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.OrCondition;
import com.unlimitedcompanies.coms.domain.security.OrGroup;
import com.unlimitedcompanies.coms.domain.security.ResourcePermissions;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotCreatedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotDeletedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;

public interface AuthService
{
	public User saveUser(User user) throws RecordNotFoundException, RecordNotCreatedException;
	public int searchNumberOfUsers();
	public boolean hasNextUser(int page, int elements);
	public List<User> searchAllUsers();
	public List<User> searchUsersByRange(int page, int elements);
	public User searchUserByUserId(Integer id) throws RecordNotFoundException;
	public User searchUserByUsername(String string) throws RecordNotFoundException;
	public User searchUserByContact(Contact contact) throws RecordNotFoundException;
	public User searchUserByUserIdWithContact(int userId) throws RecordNotFoundException;
	public User searchUserByUsernameWithContact(String username) throws RecordNotFoundException;
//	public User searchAUserByIdWithRoles(int userId);
	public User searchFullUserByUserId(int userId);
	public User searchFullUserByUsername(String username);
	public User updateUser(User user) throws RecordNotFoundException;
	public void deleteUser(int userId) throws RecordNotFoundException, RecordNotDeletedException;
	
	public int searchNumberOfRoles();
	public Role saveRole(Role role);
	public List<Role> searchAllRoles();
	public Role searchRoleById(String string);
	public Role searchRoleByIdWithMembers(String string);
	public Role searchRoleByRoleName(String roleName);
	public Role updateRole(String roleId, Role role);
	public void deleteRole(String roleId);

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
