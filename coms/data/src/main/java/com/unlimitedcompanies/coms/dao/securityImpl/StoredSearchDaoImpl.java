package com.unlimitedcompanies.coms.dao.securityImpl;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.StoredSearchDao;
import com.unlimitedcompanies.coms.data.query.SearchQuery;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class StoredSearchDaoImpl implements StoredSearchDao
{
	@PersistenceContext
	private EntityManager em;

	@Override
	public void createSearchQuery(SearchQuery search)
	{
		em.persist(search);
	}

	@Override
	public int getSearchQueriesTotal()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(searchQueryId) FROM searchQuery").getSingleResult();
		return bigInt.intValue();
	}

	@Override
	public int getSearchQueriesPathTotal()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(pathId) FROM path").getSingleResult();
		return bigInt.intValue();
	}
	
	@Override
	public int getStoredSearchCG1Num(String searchQueryId)
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(conditionGroupL1Id) FROM conditionGroupL1 WHERE searchId_FK = :id")
										   .setParameter("id", searchQueryId)
										   .getSingleResult();
		return bigInt.intValue();
	}

	@Override
	public SearchQuery getSearchQueryById(String searchQueryId)
	{
		SearchQuery sq = em.find(SearchQuery.class, searchQueryId);
		
//		StoredSearchDaoImpl.findAllPaths(sq.getQueryResource());
		
		
		
		return sq;
	}
	
//	private static void findAllPaths(Path path)
//	{
//		if (!path.getBranches().isEmpty())
//		{
//			for(Path next : path.getBranches())
//			{
//				findAllPaths(next);
//			}
//		}
//	}

	@Override
	public void deleteSearchQuery(SearchQuery searchQuery)
	{
		em.remove(searchQuery);
	}	
	
}
