package com.unlimitedcompanies.coms.service.security;

import com.unlimitedcompanies.coms.domain.abac.ABACPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;

public interface ABACService
{
	public ABACPolicy savePolicy(ABACPolicy policy, String username) throws NoResourceAccessException;
	
	public int getNumberOfPolicies();
	public int getNumberOfConditionGroups();
	public int getNumberOfEntityConditions();
	public int getNumberOfAttributeConditions();

	public ABACPolicy findPolicy(Resource resource, PolicyType policyType, String username) throws NoResourceAccessException;
	public ABACPolicy findPolicyByName(String policyName);
}
