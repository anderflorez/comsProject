package com.unlimitedcompanies.coms.service.security;

import com.unlimitedcompanies.coms.data.abac.ABACPolicy;

public interface ABACService
{
	public int getNumberOfPolicies();
	public int getNumberOfConditionGroups();
	public int getNumberOfEntityConditions();
	public int getNumberOfRecordConditions();

	public ABACPolicy savePolicy(ABACPolicy policy);
	public ABACPolicy findPolicyByName(String policyName);
}
