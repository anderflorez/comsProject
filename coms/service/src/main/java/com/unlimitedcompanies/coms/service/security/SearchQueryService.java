package com.unlimitedcompanies.coms.service.security;

import com.unlimitedcompanies.coms.data.query.SearchQuery;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;

public interface SearchQueryService
{
	public void storeSearchQuery(SearchQuery search);
	public int storedSearchQueriesNum();
	public int storedSearchQueriesPathNum();
	public boolean storedSearchearchHasCGL1(String searchQueryId);
	public SearchQuery findQueryById(String searchQueryId) throws RecordNotFoundException;
	public void deleteSearchQuery(String searchQueryId) throws RecordNotFoundException;
}
