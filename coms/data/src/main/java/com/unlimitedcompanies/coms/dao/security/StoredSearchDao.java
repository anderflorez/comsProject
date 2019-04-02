package com.unlimitedcompanies.coms.dao.security;

import com.unlimitedcompanies.coms.data.query.SearchQuery;

public interface StoredSearchDao
{
	public void createSearchQuery(SearchQuery search);
	public int getSearchQueriesTotal();
	public int getSearchQueriesPathTotal();
	public int getStoredSearchCG1Num(String searchQueryId);
	public SearchQuery getSearchQueryById(String searchQueryId);
	public void deleteSearchQuery(SearchQuery searchQuery);
}
