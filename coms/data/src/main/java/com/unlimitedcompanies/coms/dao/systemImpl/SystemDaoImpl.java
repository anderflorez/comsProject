package com.unlimitedcompanies.coms.dao.systemImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.system.SystemDao;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class SystemDaoImpl implements SystemDao
{

	@PersistenceContext
	private EntityManager em;
	
	@Override
	public void clearEntityManager()
	{
		em.flush();
		em.clear();
	}

}
