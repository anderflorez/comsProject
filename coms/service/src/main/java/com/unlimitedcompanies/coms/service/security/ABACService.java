package com.unlimitedcompanies.coms.service.security;

import java.util.List;

import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;

public interface ABACService
{
	public void savePolicy(AbacPolicy policy, String username) throws NoResourceAccessException;
	public void addFieldRestriction(int roleId, int fieldId, String loggedUser) throws NoResourceAccessException, RecordNotFoundException;
	
	public int getNumberOfPolicies();
	public int getNumberOfEntityConditions();
	public int getNumberOfAttributeConditions();
	public int getNumberOfRestrictedFields();

	public Resource searchResourceByName(String name) throws RecordNotFoundException;
	public Resource searchResourceByNameWithFields(String name);
	public AbacPolicy searchPolicy(Resource requestedResource, PolicyType policyType, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException;
	public AbacPolicy searchPolicyWithRestrictedFields(Resource requestedResource, PolicyType policyType, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException;
	public List<AbacPolicy> searchPoliciesByRange(int elements, int page, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException;
	//TODO: Make sure the next method is not available to the users. It could be available to testing only
//	public AbacPolicy searchModifiablePolicy(Resource requestedResource, PolicyType policyType, String signedUsername) 
//			throws NoResourceAccessException, RecordNotFoundException;
	
	// No update AbacPolicy should be created 
	public void updatePolicy(AbacPolicy policy, String signedUsername) throws NoResourceAccessException;
	public void deletePolicy(AbacPolicy policy, String signedUsername) throws NoResourceAccessException;
}
