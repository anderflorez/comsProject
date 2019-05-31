package com.unlimitedcompanies.coms.service.security;

import java.util.List;

import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.domain.abac.ResourceField;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;

public interface ABACService
{
	public void savePolicy(AbacPolicy policy, String username) throws NoResourceAccessException;
	public void addFieldRestriction(int fieldId, int roleId, String loggedUser) throws NoResourceAccessException;
	
	public int getNumberOfPolicies();
	public int getNumberOfEntityConditions();
	public int getNumberOfAttributeConditions();
	public int getNumberOfRestrictedFields();

	public Resource searchResourceByName(String name);
	public Resource searchResourceByNameWithFields(String name);
	public Resource searchResourceByNameWithFieldsAndPolicy(String name);
	public AbacPolicy searchPolicy(Resource requestedResource, PolicyType policyType, String signedUsername) throws NoResourceAccessException;
}
