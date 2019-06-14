package com.unlimitedcompanies.coms.dao.securityImpl;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.AuthDao;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class AuthDaoImpl implements AuthDao
{
	@PersistenceContext
	private EntityManager em;

	@Override
	public void createUser(User user)
	{
		try
		{
			Contact contact = em.merge(user.getContact());
			user.setContact(contact);
			em.persist(user);
		} 
		catch (PersistenceException e)
		{
			if (e.getCause() instanceof ConstraintViolationException)
			{
				throw (ConstraintViolationException)e.getCause();
			}
			else
			{
				throw e;
			}
		}
	}
	
	@Override
	public int getNumberOfUsers()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(userId) FROM users").getSingleResult();
		return bigInt.intValue();
	}
//	
//	@Override
//	public boolean existingUser(int userId)
//	{
//		User user = em.find(User.class, userId);
//		return user == null ? false : true;
//	}
//	
//	@Override
//	public List<User> getAllUsers()
//	{
//		return em.createQuery("select user from User as user", User.class).getResultList();
//	}
//	
//	@Override
//	public List<User> getUsersByRange(int page, int elements)
//	{
//		return em.createQuery("select user from User user order by user.username", User.class)
//				  .setFirstResult(page * elements)
//				  .setMaxResults(elements)
//				  .getResultList();
//	}
	
	@Override
	public User getUserByUserId(int id, String accessConditions)
	{
		if (accessConditions != null && !accessConditions.isEmpty())
		{
			return em.createQuery("select user from User user where user.userId = :userId and " + accessConditions, User.class)
						.setParameter("userId", id)
						.getSingleResult();
		}
		else
		{
			User user = em.find(User.class, id);
			if (user == null)
			{
				throw new NoResultException();
			}
			
			return user;
		}
	}

	@Override
	public User getUserByUsername(String username, String accessConditions)
	{
		String queryString = "select user from User user where user.username = :username";
		if (accessConditions != null && !accessConditions.isEmpty())
		{
			queryString += " and " + accessConditions;
		}
		
		return em.createQuery(queryString, User.class).setParameter("username", username).getSingleResult();
	}
//
//	@Override
//	public User getUserByContact(Contact contact)
//	{
//		return em.createQuery("select user from User user where user.contact = :contact", User.class)
//							  .setParameter("contact", contact)
//							  .getSingleResult();
//	}
//
//	@Override
//	public User getUserByUserIdWithContact(int userId)
//	{
//		return em.createQuery("select user from User as user left join fetch user.contact where user.userId = :userId", User.class)
//				  .setParameter("userId", userId)
//				  .getSingleResult();
//	}
//	
//	@Override
//	public User getUserByUsernameWithContact(String username)
//	{
//		return em.createQuery("select user from User as user left join fetch user.contact where user.username = :username", User.class)
//							  .setParameter("username", username)
//							  .getSingleResult();
//	}
	
	@Override
	public User getFullUserByUsername(String username, String accessConditions)
	{
		String queryString = "select user from User user left join fetch user.contact left join fetch user.roles where user.username = :username";
		if (accessConditions != null && !accessConditions.isEmpty())
		{
			queryString += " and " + accessConditions;
		}
		
		return em.createQuery(queryString, User.class).setParameter("username", username).getSingleResult();
	}
	
	@Override
	public User getFullUserWithAttribs(int userId)
	{
		return em.createQuery("select user from User user "
				+ "left join fetch user.roles roles "
				+ "left join fetch user.contact contact "
				+ "left join fetch contact.employee employee "
				+ "left join fetch employee.pmProjects pmProjects "
				+ "left join fetch employee.superintendentProjects superintendentProjects "
				+ "left join fetch employee.foremanProjects foremanProjects "
				+ "where user.userId = :userId", User.class)
				.setParameter("userId", userId)
				.getSingleResult();
	}
	
	@Override
	public Role getRoleWithRestrictedFields(Integer roleId)
	{
		Role role = em.find(Role.class, roleId);
		role.getRestrictedFields();
		return role;
	}
//	
//	@Override
//	public User getFullUserByUserId(int userId)
//	{
//		return em.createQuery("select user from User user left join fetch user.contact left join fetch user.roles where user.userId = :id", User.class)
//							  .setParameter("id", userId)
//							  .getSingleResult();
//	}
//	
//	@Override
//	public void updateUser(User user)
//	{
//		User foundUser = this.getUserByUserId(user.getUserId());
//		foundUser.setUsername(user.getUsername());
//		foundUser.setEnabled(user.isEnabled());
//	}
//	
//	@Override
//	public void changeUserPassword(int userId, char[] newPassword)
//	{
//		User foundUser = this.getUserByUserId(userId);
//		foundUser.setPassword(newPassword);
//	}
//
//	@Override
//	public void deleteUser(int userId)
//	{
//		// TODO: Find a way to test this operation at running time to show a success or error message to the user
//		User user = this.getUserByUserId(userId);
//		em.remove(user);
//	}

	@Override
	public void createRole(Role role)
	{
		try
		{
			em.persist(role);
		}
		catch (PersistenceException e)
		{
			if (e.getCause() instanceof ConstraintViolationException)
			{
				throw (ConstraintViolationException)e.getCause();
			}
			else
			{
				throw e;
			}
		}
	}
	
	@Override
	public int getNumberOfRoles()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(roleId) FROM roles").getSingleResult();
		return bigInt.intValue();
	}
//	
//	@Override
//	public boolean existingRole(int roleId)
//	{
//		Role role = em.find(Role.class, roleId);
//		return role == null ? false : true;
//	}
//	
//	@Override
//	public List<Role> getAllRoles()
//	{
//		return em.createQuery("from Role", Role.class).getResultList();
//	}
//	
//	@Override
//	public List<Role> getAllRolesByRange(int page, int elements)
//	{
//		return em.createQuery("select role from Role role order by role.roleName", Role.class)
//				  .setFirstResult(page * elements)
//				  .setMaxResults(elements)
//				  .getResultList();
//	}
	
	@Override
	public Role getRoleById(int roleId, String accessConditions)
	{
		if (accessConditions != null && !accessConditions.isEmpty())
		{
			return em.createQuery("select role from Role role where role.roleId = :roleId and " + accessConditions, Role.class)
						.setParameter("roleId", roleId)
						.getSingleResult();
		}
		else
		{
			Role role = em.find(Role.class, roleId);
			
			if (role == null)
			{
				throw new NoResultException();
			}
			return role;			
		}
	}

	@Override
	public Role getRoleByName(String roleName, String accessConditions)
	{
		String queryString = "select role from Role as role where role.roleName = :name";
		if (accessConditions != null && !accessConditions.isEmpty())
		{
			queryString += " and " + accessConditions;
		}

		return em.createQuery(queryString, Role.class).setParameter("name", roleName).getSingleResult();
	}
	
	@Override
	public Role getRoleByNameWithRestrictedFields(String roleName, String accessConditions)
	{
		String queryString = "select role from Role as role left join fetch role.restrictedFields where role.roleName = :name";
		if (accessConditions != null && !accessConditions.isEmpty())
		{
			queryString += " and " + accessConditions;
		}

		return em.createQuery(queryString, Role.class).setParameter("name", roleName).getSingleResult();
	}
//	
//	@Override
//	public Role getRoleByIdWithMembers(int id)
//	{
//		return em.createQuery("select role from Role role left join fetch role.users user left join fetch user.contact contact "
//				+ "where role.roleId = :roleId", Role.class)
//				.setParameter("roleId", id)
//				.getSingleResult();
//	}
//
	@Override
	public Role getRolePathWithFullEmployees(int roleId)
	{
		String queryString = "select role from Role role "
								+ "left join fetch role.users user "
								+ "left join fetch user.contact contact "
								+ "left join fetch contact.employee employee "
								+ "left join fetch employee.pmProjects pmProjects "
								+ "left join fetch employee.superintendentProjects superintendentProjects "
								+ "left join fetch employee.foremanProjects foremanProjects "
								+ "where role.roleId = :roleId";
		
		return em.createQuery(queryString, Role.class).setParameter("roleId", roleId).getSingleResult();
	}
	
//	@Override
//	public List<User> getRoleNonMembersByCriteria(int roleId, String searchCriteria)
//	{
//		return em.createQuery("select u from User u "
//							+ "left join u.roles r "
//							+ "join u.contact c "
//							+ "where (r.roleId != :roleId or r.roleId is null) "
//							+ "and (u.username like :criteria or c.firstName like :criteria or c.lastName like :criteria)", User.class)
//				 .setParameter("roleId", roleId)
//				 .setParameter("criteria", "%" + searchCriteria + "%")
//				 .getResultList();
//	}
	
	@Override
	public void updateRole(Role role)
	{
		em.merge(role);
	}
//
//	@Override
//	public void deleteRole(int roleId)
//	{
//		Role role = this.getRoleById(roleId);
//		em.remove(role);
//	}

	@Override
	public void assignUserToRole(int userId, int roleId)
	{
		Role role = this.getRoleById(roleId, null);
		User user = this.getUserByUserId(userId, null);
		
		role.addUser(user);
	}
//
//	@Override
//	public void removeUserFromRole(int userId, int roleId)
//	{
//		Role foundRole = this.getRoleById(roleId);
//		User foundUser = this.getUserByUserId(userId);
//
//		foundRole.removeUser(foundUser);
//	}

	@Override
	public void clearEntityManager()
	{
		em.flush();
		em.clear();		
	}
	
}
