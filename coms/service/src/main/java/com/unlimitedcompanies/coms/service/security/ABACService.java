package com.unlimitedcompanies.coms.service.security;

import com.unlimitedcompanies.coms.domain.abac.ABACPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;

public interface ABACService
{
	public Resource findResourceByName(String name);
	public Resource findResourceByNameWithFields(String name);
	public Resource findResourceByNameWithFieldsAndPolicy(String name);
	
	public void savePolicy(ABACPolicy policy, String username) throws NoResourceAccessException;
	
	public int getNumberOfPolicies();
	public int getNumberOfEntityConditions();
	public int getNumberOfAttributeConditions();

	public ABACPolicy findPolicy(Resource requestedResource, PolicyType policyType, String username) throws NoResourceAccessException;

	public int getNumberOfRestrictedFields();
}
