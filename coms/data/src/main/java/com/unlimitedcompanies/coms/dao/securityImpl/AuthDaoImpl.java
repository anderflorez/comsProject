package com.unlimitedcompanies.coms.dao.securityImpl;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.AuthDao;
import com.unlimitedcompanies.coms.domain.security.AndCondition;
import com.unlimitedcompanies.coms.domain.security.AndGroup;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.OrCondition;
import com.unlimitedcompanies.coms.domain.security.OrGroup;
import com.unlimitedcompanies.coms.domain.security.ResourcePermissions;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class AuthDaoImpl implements AuthDao
{
	@PersistenceContext
	private EntityManager em;

	@Override
	public int getNumberOfUsers()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(userId) FROM user").getSingleResult();
		return bigInt.intValue();
	}

	@Override
	public void createUser(User user)
	{
		em.createNativeQuery(
				"INSERT INTO user (username, password, enabled, dateAdded, lastAccess, contact_FK) VALUES (:username, :password, :enabled, :dateAdded, :lastAccess, :contact)")
				.setParameter("username", user.getUsername())
				.setParameter("password", user.getPassword())
				.setParameter("enabled", user.getEnabled())
				.setParameter("dateAdded", user.getDbDateAdded())
				.setParameter("lastAccess", user.getDbLastAccess())
				.setParameter("contact", user.getContact())
				.executeUpdate();
	}
	
	@Override
	public List<User> getAllUsers()
	{
		return em.createQuery("select user from User as user", User.class).getResultList();
	}
	
	@Override
	public User getUserByUserId(int id)
	{
		User user = em.find(User.class, id);
		if (user == null)
		{
			throw new NoResultException();
		}
		
		return user;
	}

	@Override
	public User getUserByUsername(String username)
	{
		return em.createQuery("select user from User as user where user.username = :username", User.class)
							  .setParameter("username", username)
							  .getSingleResult();
	}

	@Override
	public User getUserByContact(Contact contact)
	{
		return em.createQuery("select user from User user where user.contact = :contact", User.class)
							  .setParameter("contact", contact)
							  .getSingleResult();
	}
	
	@Override
	public User getUserByUsernameWithContact(String username)
	{
		return em.createQuery("select user from User as user left join fetch user.contact where username = :username", User.class)
							  .setParameter("username", username)
							  .getSingleResult();
	}

	@Override
	public User getFullUserByUsername(String username)
	{
		return em.createQuery("select user from User user left join fetch user.contact left join fetch user.roles where user.username = :username", User.class)
							  .setParameter("username", username)
							  .getSingleResult();
	}
	
	@Override
	public void updateUser(int userId, User user) {
		User foundUser = em.find(User.class, userId);
		foundUser.setUsername(user.getUsername());
		foundUser.setEnabled(user.getEnabled());
	}
	

	@Override
	public void deleteUser(int userId)
	{
		User user = this.getUserByUserId(userId);
		em.remove(user);
	}
	
	@Override
	public int getNumberOfRoles()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(roleId) FROM role").getSingleResult();
		return bigInt.intValue();
	}

	@Override
	public void createRole(Role role)
	{
		em.createNativeQuery("INSERT INTO role (roleName) VALUES (:roleName)")
				.setParameter("roleName", role.getRoleName()).executeUpdate();
	}
	
	@Override
	public List<Role> getAllRoles()
	{
		return em.createQuery("from Role", Role.class).getResultList();
	}
	
	@Override
	public Role getRoleById(int id)
	{
		Role role = em.find(Role.class, id);
		if (role == null)
		{
			throw new NoResultException();
		}
		return role;
	}

	@Override
	public Role getRoleByRoleName(String roleName)
	{
		return em.createQuery("select role from Role as role where role.roleName = :name", Role.class)
				 .setParameter("name", roleName)
				 .getSingleResult();
	}
	
	@Override
	public Role getRoleByIdWithMembers(int id)
	{
		return em.createQuery("select role from Role role left join fetch role.users user left join fetch user.contact contact "
				+ "where role.roleId = :roleId", Role.class)
				.setParameter("roleId", id)
				.getSingleResult();
	}

	
	@Override
	public void updateRole(int roleId, Role role)
	{
		Role foundRole = em.find(Role.class, roleId);
		if (foundRole == null)
		{
			throw new NoResultException();
		}
		foundRole.setRoleName(role.getRoleName());
	}	

	@Override
	public void assignUserToRole(User user, Role role)
	{
		Role foundRole = em.createQuery("select role from Role as role where role.roleName = :name", Role.class)
				.setParameter("name", role.getRoleName()).getSingleResult();
		User foundUser = em.createQuery("select user from User as user where user.username = :username", User.class)
				.setParameter("username", user.getUsername()).getSingleResult();

		foundRole.addUser(foundUser);
		em.persist(foundRole);
	}

//	@Override
//	public void removeUserFromRole(Role role, User user)
//	{
//		Role foundRole = this.getRoleByRoleName(role.getRoleName());
//		User foundUser = this.searchUserByUsername(user.getUsername());
//
//		foundRole.removeUser(foundUser);
//	}
	
	@Override
	public int getNumberOfPermissions()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(roleResourceIdentifier) FROM role_resource").getSingleResult();
		return bigInt.intValue();
	}

	@Override
	public void createResourcePermission(ResourcePermissions newPermission)
	{
		em.persist(newPermission);
	}
	
	@Override
	public ResourcePermissions searchPermissionById(String id)
	{
		return em.find(ResourcePermissions.class, id);
	}
	
	@Override
	public List<ResourcePermissions> getAllRolePermissions(Role role)
	{
		return em.createQuery("select permissions from ResourcePermissions permissions where permissions.role = :role", ResourcePermissions.class)
							  .setParameter("role", role)
							  .getResultList();
	}

	@Override
	public void createAndGroup(AndGroup andGroup)
	{
		em.persist(andGroup);
	}

	@Override
	public AndGroup getAndGroupById(String andGroupId)
	{
		return em.find(AndGroup.class, andGroupId);
	}

	@Override
	public void createAndCondition(AndCondition andCondition)
	{
		em.persist(andCondition);
	}

	@Override
	public void createOrGroup(OrGroup orGroup)
	{
		em.persist(orGroup);		
	}

	@Override
	public OrGroup getOrGroupById(String orGroupId)
	{
		return em.find(OrGroup.class, orGroupId);
	}

	@Override
	public void createOrCondition(OrCondition orCondition)
	{
		em.persist(orCondition);
	}

	@Override
	public List<AndGroup> getAssociatedAndGroups(OrGroup orGroup)
	{
		return em.createQuery("select a from AndGroup a where a.orGroups = :orGroup", AndGroup.class)
							  .setParameter("orGroup", orGroup)
							  .getResultList();
	}

	@Override
	public List<OrGroup> getAssociatedOrGroups(AndGroup andGroup)
	{
		return em.createQuery("select o from OrGroup o where o.andGroups = :orGroup", OrGroup.class)
							  .setParameter("andGroup", andGroup)
							  .getResultList();
	}

}
