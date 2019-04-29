package com.unlimitedcompanies.coms.service.security;

import com.unlimitedcompanies.coms.data.abac.ABACPolicy;
import com.unlimitedcompanies.coms.data.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.security.Resource;

public interface ABACService
{
	public int getNumberOfPolicies();
	public int getNumberOfConditionGroups();
	public int getNumberOfEntityConditions();
	public int getNumberOfAttributeConditions();

	public ABACPolicy savePolicy(ABACPolicy policy);
	public ABACPolicy findPolicy(Resource resource, PolicyType policyType);
	public ABACPolicy findPolicyByName(String policyName);
}
