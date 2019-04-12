package com.unlimitedcompanies.coms.dao.security;

import com.unlimitedcompanies.coms.data.abac.ABACPolicy;

public interface ABACDao
{
	public int getNumberOfPolicies();
	public int getNumberOfConditionGroups();
	public int getNumberOfEntityConditions();
	public int getNumberOfAttributeConditions();

	public void savePolicy(ABACPolicy policy);
	public ABACPolicy findPolicyByName(String policyName);

}
