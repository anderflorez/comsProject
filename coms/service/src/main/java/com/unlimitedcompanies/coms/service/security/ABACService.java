package com.unlimitedcompanies.coms.service.security;

import com.unlimitedcompanies.coms.data.abac.ABACPolicy;

public interface ABACService
{
	public int getNumberOfPolicies();
	public void savePolicy(ABACPolicy policy);
	public ABACPolicy findPolicyByName(String policyName);
}
