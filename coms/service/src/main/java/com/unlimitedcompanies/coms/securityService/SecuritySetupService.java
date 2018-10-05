package com.unlimitedcompanies.coms.securityService;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.ResourceField;

public interface SecuritySetupService
{
	public void initialSetup();
	public void checkAllResources();
	public List<String> findAllResourceNames();
	public List<ResourceField> findAllResourceFieldsWithResources();
	public Resource findResourceByName(String name);
}
