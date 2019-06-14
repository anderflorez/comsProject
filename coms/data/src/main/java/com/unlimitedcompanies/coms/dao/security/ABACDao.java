package com.unlimitedcompanies.coms.dao.security;

import java.util.List;

import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.domain.abac.ResourceField;
import com.unlimitedcompanies.coms.domain.security.User;

public interface ABACDao
{
	public void savePolicy(AbacPolicy policy);
	public void registerResource(Resource resource);
	public void registerResourceField(ResourceField resourceField);
	
	public int getNumberOfPolicies();
	public int getNumberOfEntityConditions();
	public int getNumberOfAttributeConditions();
	public int getNumberOfRestrictedFields();
	public AbacPolicy getPolicy(Resource resource, PolicyType policyType, String accessConditions);
	public List<String> getAllResourceNames();
	public Resource getResourceByName(String name);
	public Resource getResourceByNameWithFields(String name);
	public Resource getResourceByNameWithFieldsAndPolicy(String name);
	public ResourceField getResourceFieldById(int fieldId);
	public List<ResourceField> getAllResourceFieldsWithResources();
	public List<ResourceField> getRestrictedFields(int userId, int resourceId);
	
	public void checkResourceList();
	public void checkResourceFieldList();
}
