package com.unlimitedcompanies.coms.service.securityImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.securitySettings.SecuritySetupDao;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.ResourceField;
import com.unlimitedcompanies.coms.service.security.SecuritySetupService;

@Service
@Transactional
public class SecuritySetupServiceImpl implements SecuritySetupService
{
	@Autowired
	private SecuritySetupDao dao;

	@Override
	public void initialSetup()
	{
		dao.initialSetup();
	}
	
	@Override
	public void checkAllResources()
	{
		dao.checkAllResources();
	}

	@Override
	public List<String> findAllResourceNames()
	{
		return dao.findAllResourceNames();
	}

	@Override
	public List<ResourceField> findAllResourceFieldsWithResources()
	{
		return dao.findAllResourceFieldsWithResources();
	}

	@Override
	public Resource findResourceByName(String name)
	{
		return dao.findResourceByName(name);
	}

	@Override
	public Resource findResourceByNameWithFields(String name)
	{
		return dao.findResourceByNameWithFields(name);
	}

}
