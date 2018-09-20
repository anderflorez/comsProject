package com.unlimitedcompanies.coms.dao.securitySettings;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.ResourceField;

public interface SecuritySetup
{

	void checkAllResources();
	void checkResourceList();
	void checkResourceFieldList();
	List<String> findAllResourceNames();
	List<ResourceField> findAllResourceFieldsWithResources();
	Resource findResourceByName(String name);
	void registerResource(String resourceName);
	void registerResourceField(ResourceField resourceField);

}
