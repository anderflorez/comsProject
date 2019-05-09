package com.unlimitedcompanies.coms.dao.securitySettings;

import java.util.List;

import com.unlimitedcompanies.coms.data.exceptions.DuplicatedResourcePolicyException;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.ResourceField;

public interface SecuritySetupDao
{
	public void initialSetup() throws DuplicatedResourcePolicyException;
	public void checkAllResources();
	public void checkResourceList();
	public void checkResourceFieldList();
	public List<String> findAllResourceNames();
	public List<ResourceField> findAllResourceFieldsWithResources();
	public Resource findResourceByName(String name);
	public Resource findResourceByNameWithFields(String name);
	public Resource findResourceByNameWithFieldsAndPolicy(String name);
	public void registerResource(String resourceName);
	public void registerResourceField(ResourceField resourceField);

}
