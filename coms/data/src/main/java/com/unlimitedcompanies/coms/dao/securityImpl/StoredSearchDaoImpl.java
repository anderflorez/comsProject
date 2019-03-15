package com.unlimitedcompanies.coms.dao.securityImpl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.StoredSearchDao;
import com.unlimitedcompanies.coms.data.query.ConditionGL1;
import com.unlimitedcompanies.coms.data.query.ConditionGL2;
import com.unlimitedcompanies.coms.data.query.ConditionGL3;
import com.unlimitedcompanies.coms.data.query.ConditionL1;
import com.unlimitedcompanies.coms.data.query.ConditionL2;
import com.unlimitedcompanies.coms.data.query.ConditionL3;
import com.unlimitedcompanies.coms.data.query.Path;
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
		List<Path> sortedPathList = new ArrayList<>();
		findSearchQueryPath(sortedPathList, search.getQueryResource());
		for (int i = sortedPathList.size() - 1; i >= 0; i--)
		{
			em.persist(sortedPathList.get(i));
		}

		em.persist(search);

		// Save condition group level 1 and its conditions if there are any
		ConditionGL1 cgl1 = search.getConditionGL1();
		if (cgl1 != null)
		{
			em.persist(cgl1);
			for (ConditionL1 next : cgl1.getConditions())
			{
				em.persist(next);
			}
			
			// Save condition group level 2 and its conditions if there are any
			ConditionGL2 cgl2 = cgl1.getConditionGroup();
			if (cgl2 != null)
			{
				em.persist(cgl2);
				for (ConditionL2 next : cgl2.getConditions())
				{
					em.persist(next);
				}
				
				// Save condition group level 3 and its conditions if there are any
				ConditionGL3 cgl3 = cgl2.getConditionGroup();
				if (cgl3 != null)
				{
					em.persist(cgl3);
					for (ConditionL3 next : cgl3.getConditions())
					{
						em.persist(next);
					}
				}
			}
		}
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
	public SearchQuery getSearchQueryById(String searchQueryId)
	{
		SearchQuery sq = em.find(SearchQuery.class, searchQueryId);
		
		StoredSearchDaoImpl.findAllPaths(sq.getQueryResource());
		
		return sq;
	}
	
	private static void findSearchQueryPath(List<Path> sortedPath, Path path)
	{
		sortedPath.add(path);
		for (int i = 0; i < path.getBranches().size(); i++)
		{
			findSearchQueryPath(sortedPath, path.getBranches().get(i));
		}
	}
	
	private static void findAllPaths(Path path)
	{
		if (!path.getBranches().isEmpty())
		{
			for(Path next : path.getBranches())
			{
				findAllPaths(next);
			}
		}
	}
}
