package com.unlimitedcompanies.coms.dao.security;

import java.util.List;

import com.unlimitedcompanies.coms.domain.abac.ABACPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.ResourceField;
import com.unlimitedcompanies.coms.domain.security.User;

public interface ABACDao
{
	public void savePolicy(ABACPolicy policy);

	public int getNumberOfPolicies();
	public int getNumberOfEntityConditions();
	public int getNumberOfAttributeConditions();
	public int getNumberOfRestrictedFields();

	public ABACPolicy findPolicy(Resource resource, PolicyType policyType, String accessConditions);
	public User getFullUserWithAttribs(int userId);
	
	public void checkResourceList();
	public void checkResourceFieldList();
	public List<String> findAllResourceNames();
	public List<ResourceField> findAllResourceFieldsWithResources();
	public Resource findResourceByName(String name);
	public Resource findResourceByNameWithFields(String name);
	public Resource findResourceByNameWithFieldsAndPolicy(String name);
	public void registerResource(Resource resource);
	public void registerResourceField(ResourceField resourceField);

}
