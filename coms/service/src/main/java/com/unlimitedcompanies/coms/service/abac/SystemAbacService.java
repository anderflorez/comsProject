package com.unlimitedcompanies.coms.service.abac;

import java.util.List;

import com.unlimitedcompanies.coms.data.exceptions.DuplicatedResourcePolicyException;
import com.unlimitedcompanies.coms.domain.abac.ABACPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.UserAttribs;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.ResourceField;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;

public interface SystemAbacService
{
	/*
	 * Initial setup of resources for the security system
	 */
	public void initialSetup() throws DuplicatedResourcePolicyException;
	public void checkAllResources();
	public List<String> findAllResourceNames();
	public List<ResourceField> findAllResourceFieldsWithResources();
	
	
	// Get objects with system rights which is without checking any permissions to obtain them
	public ABACPolicy findPolicy(Resource resource, PolicyType policyType) throws NoResourceAccessException;
	public UserAttribs getUserAttribs(int userId);
}
