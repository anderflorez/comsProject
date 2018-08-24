package com.unlimitedcompanies.coms.dao.securityImpl;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.AuthenticationDao;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class AuthenticationDaoImpl implements AuthenticationDao
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
	public void createUser(User user, String dateAdded, String lastAccessed)
	{
		em.createNativeQuery(
				"INSERT INTO user (username, password, enabled, dateAdded, lastAccess, contact_FK) VALUES (:username, :password, :enabled, :dateAdded, :lastAccess, :contact)")
				.setParameter("username", user.getUsername())
				.setParameter("password", user.getPassword())
				.setParameter("enabled", user.getEnabled())
				.setParameter("dateAdded", dateAdded)
				.setParameter("lastAccess", lastAccessed)
				.setParameter("contact", user.getContact())
				.executeUpdate();
	}

	@Override
	public User searchUserByUsername(String username)
	{
		return em.createQuery("select user from User as user where user.username = :username", User.class)
				.setParameter("username", username).getSingleResult();
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
	public Role searchRoleByRoleName(String roleName)
	{
		return em.createQuery("select role from Role as role where role.roleName = :name", Role.class)
				.setParameter("name", roleName).getSingleResult();
	}

	@Override
	public int findNumberOfUser_RoleAssignments()
	{
		BigInteger bigInt =
				(BigInteger) em.createNativeQuery("SELECT COUNT(user_role_Id) FROM user_role").getSingleResult();
		return bigInt.intValue();
	}

	@Override
	public void assignUserToRole(Role role, User user)
	{
		Role foundRole = em.createQuery("select role from Role as role where role.roleName = :name", Role.class)
				.setParameter("name", role.getRoleName()).getSingleResult();
		User foundUser = em.createQuery("select user from User as user where user.username = :username", User.class)
				.setParameter("username", user.getUsername()).getSingleResult();

		foundRole.addUser(foundUser);
		em.persist(foundRole);
	}

	@Override
	public void removeUserFromRole(Role role, User user)
	{
		Role foundRole = this.searchRoleByRoleName(role.getRoleName());
		User foundUser = this.searchUserByUsername(user.getUsername());

		foundRole.removeUser(foundUser);
	}

}
