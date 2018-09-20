package com.unlimitedcompanies.coms.dao.securityImpl;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.unlimitedcompanies.coms.dao.security.GeneralSearchDao;
import com.unlimitedcompanies.coms.domain.search.Search;

public class GeneralSearchDaoImpl implements GeneralSearchDao
{
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public Object search(Search search)
	{
		Query q = em.createQuery(search.toString());
		Map<String, Object> values = search.getValues();
		for (String key : values.keySet())
		{
			q.setParameter(key, values.get(key));
		}
		
		return q.getSingleResult();
	}

}
