package com.unlimitedcompanies.coms.dao.securityImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.ABACDao;
import com.unlimitedcompanies.coms.domain.security.User;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class ABACDaoImpl implements ABACDao
{
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public User getUserWithAttributes()
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		
		return em.createQuery("select user from User user left join fetch user.contact left join fetch user.roles where user.username = :username", User.class)
				 .setParameter("username", userDetails.getUsername())
				 .getSingleResult();
	}

}
