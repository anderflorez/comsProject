package com.unlimitedcompanies.coms.dao.securityImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.GeneralSearchDao;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class GeneralSearchDaoImpl implements GeneralSearchDao
{
	@PersistenceContext
	private EntityManager em;
	
//	@Override
//	public Object search(Search search)
//	{
//		Query q = em.createQuery(search.toString());
//		Map<String, Object> values = search.getValues();
//		for (String key : values.keySet())
//		{
//			q.setParameter(key, values.get(key));
//		}
//		
//		return q.getSingleResult();
//	}

}
