package com.unlimitedcompanies.coms.dao.security;

import com.unlimitedcompanies.coms.data.abac.ABACPolicy;

public interface ABACDao
{
	public int getNumberOfPolicies();
	public void savePolicy(ABACPolicy policy);
	public ABACPolicy findPolicyByName(String policyName);

}
