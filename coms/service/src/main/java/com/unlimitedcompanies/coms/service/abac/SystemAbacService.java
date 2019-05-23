package com.unlimitedcompanies.coms.service.abac;

import com.unlimitedcompanies.coms.domain.abac.ABACPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.UserAttribs;
import com.unlimitedcompanies.coms.domain.security.Resource;

public interface SystemAbacService
{
	public ABACPolicy findPolicy(Resource resource, PolicyType policyType);
	
	public UserAttribs getUserAttribs(int userId);
}
