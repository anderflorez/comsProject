package com.unlimitedcompanies.coms.service.securityImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.StoredSearchDao;
import com.unlimitedcompanies.coms.data.query.SearchQuery;
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
	public SearchQuery findQueryById(String searchQueryId)
	{
		SearchQuery sq = dao.getSearchQueryById(searchQueryId);
		return sq;
	}

	@Override
	public void deleteSearchQuery(String searchQueryId)
	{
		SearchQuery sq = this.findQueryById(searchQueryId);
		
	}
}
