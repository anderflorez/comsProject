package com.unlimitedcompanies.coms.dao.security;

import java.util.List;

import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.domain.abac.ResourceField;
import com.unlimitedcompanies.coms.domain.security.Role;

public interface ABACDao
{
	public void savePolicy(AbacPolicy policy);
	public void registerResource(Resource resource);
	public void registerResourceField(ResourceField resourceField);
	
	public int getNumberOfPolicies();
	public int getNumberOfEntityConditions();
	public int getNumberOfAttributeConditions();
	public int getNumberOfRestrictedFields();
	public AbacPolicy searchPolicy(Resource resource, PolicyType policyType, String accessConditions);
	public List<String> searchAllResourceNames();
	public Resource searchResourceByName(String name);
	public Resource searchResourceByNameWithFields(String name);
	public Resource searchResourceByNameWithFieldsAndPolicy(String name);
	public ResourceField searchResourceFieldById(int fieldId);
	public List<ResourceField> searchAllResourceFieldsWithResources();
	
	public void checkResourceList();
	public void checkResourceFieldList();
}
