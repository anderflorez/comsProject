package com.unlimitedcompanies.coms.service.securityImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unlimitedcompanies.coms.dao.security.StoredSearchDao;
import com.unlimitedcompanies.coms.data.query.SearchQuery;
import com.unlimitedcompanies.coms.service.security.SearchQueryService;

@Service
public class SearchQueryServiceImpl implements SearchQueryService
{
	@Autowired
	private StoredSearchDao dao;

	@Override
	public void storeSearchQuery(SearchQuery search)
	{
		// TODO: check that path has an existing resource assigned or verify and catch the possible exception
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
	public SearchQuery findQueryById(String searchQueryId)
	{
		SearchQuery sq = dao.getSearchQueryById(searchQueryId);
		
		return sq;
	}

}
