package com.unlimitedcompanies.coms.securityServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unlimitedcompanies.coms.dao.security.GeneralSearchDao;
import com.unlimitedcompanies.coms.domain.search.Search;
import com.unlimitedcompanies.coms.securityService.GeneralSearchService;

@Service
public class GeneralSearchServiceImpl implements GeneralSearchService
{
	@Autowired
	private GeneralSearchDao dao;
	
//	@Override
//	public Object search(Search search)
//	{
//		return dao.search(search);
//	}

}
