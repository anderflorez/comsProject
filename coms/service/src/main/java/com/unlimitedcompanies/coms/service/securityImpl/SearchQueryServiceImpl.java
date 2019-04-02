package com.unlimitedcompanies.coms.service.securityImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.StoredSearchDao;
import com.unlimitedcompanies.coms.data.query.SearchQuery;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.SearchQueryService;

@Service
@Transactional
public class SearchQueryServiceImpl implements SearchQueryService
{
	@Autowired
	private StoredSearchDao dao;
	
	@Override
	public void storeSearchQuery(SearchQuery search)
	{
		dao.createSearchQuery(search);
	}

	@Override
	public int storedSearchQueriesNum()
	{
		return dao.getSearchQueriesTotal();
	}
	
	@Override
	public int storedSearchQueriesPathNum()
	{
		return dao.getSearchQueriesPathTotal();
	}
	
	@Override
	public boolean storedSearchearchHasCGL1(String searchQueryId)
	{
		int cgl1Num = dao.getStoredSearchCG1Num(searchQueryId);
		return cgl1Num > 0 ? true : false;
	}

	@Override
	public SearchQuery findQueryById(String searchQueryId) throws RecordNotFoundException
	{
		SearchQuery sq = dao.getSearchQueryById(searchQueryId);
		if (sq ==  null)
		{
			throw new RecordNotFoundException("The requested stored search could not be found");
		}
		return sq;
	}

	@Override
	public void deleteSearchQuery(String searchQueryId) throws RecordNotFoundException
	{
		SearchQuery searchQuery = this.findQueryById(searchQueryId);
		dao.deleteSearchQuery(searchQuery);
	}
}
