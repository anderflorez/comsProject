package com.unlimitedcompanies.coms.securityServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unlimitedcompanies.coms.dao.securitySettings.SecuritySetupDao;
import com.unlimitedcompanies.coms.securityService.SecuritySetupService;

@Service
public class SecuritySetupServiceImpl implements SecuritySetupService
{
	@Autowired
	private SecuritySetupDao dao;

	@Override
	public void initialSetup()
	{
		dao.initialSetup();
	}

}
