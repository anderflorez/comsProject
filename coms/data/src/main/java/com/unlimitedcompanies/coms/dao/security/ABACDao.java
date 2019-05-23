package com.unlimitedcompanies.coms.dao.security;

import com.unlimitedcompanies.coms.domain.abac.ABACPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.User;

public interface ABACDao
{
	public int getNumberOfPolicies();
	public int getNumberOfConditionGroups();
	public int getNumberOfEntityConditions();
	public int getNumberOfAttributeConditions();

	public void savePolicy(ABACPolicy policy);
	public ABACPolicy findPolicy(Resource resource, PolicyType policyType, String accessConditions);
	
	public User getFullUserWithAttribs(int userId);

}
