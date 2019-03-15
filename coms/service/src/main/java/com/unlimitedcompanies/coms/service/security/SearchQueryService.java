package com.unlimitedcompanies.coms.service.security;

import com.unlimitedcompanies.coms.data.query.SearchQuery;

public interface SearchQueryService
{
	public void storeSearchQuery(SearchQuery search);
	public int storedSearchQueriesNum();
	public int storedSearchQueriesPathNum();
	public SearchQuery findQueryById(String searchQueryId);
	public void deleteSearchQuery(String searchQueryId);
}
