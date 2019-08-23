package com.unlimitedcompanies.coms.service.security;

import java.util.List;

import com.unlimitedcompanies.coms.data.exceptions.InvalidPolicyException;
import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotCreatedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;

public interface AbacService
{
	public void savePolicy(AbacPolicy policy, String username) throws NoResourceAccessException, InvalidPolicyException;
	public void addFieldRestriction(int roleId, int fieldId, String loggedUser) throws NoResourceAccessException, RecordNotFoundException;
	
	public int getNumberOfPolicies();
	public int getNumberOfEntityConditions();
	public int getNumberOfAttributeConditions();
	public int getNumberOfRestrictedFields();

	public Resource searchResourceByName(String name) throws RecordNotFoundException;
	public Resource searchResourceByNameWithFields(String name);
	
	// TODO: Analyze if next two methods are actually needed only internally in the application or the end user needs access to it
	public AbacPolicy searchPolicy(Resource requestedResource, PolicyType policyType, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException;
	public AbacPolicy searchPolicyWithRestrictedFields(Resource requestedResource, PolicyType policyType, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException;
	
	public AbacPolicy searchPolicyById(String abacPolicyId, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public List<AbacPolicy> searchPoliciesByRange(int elements, int page, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException;
	//TODO: Make sure the next method is not available to the users. It could be available to testing only
//	public AbacPolicy searchModifiablePolicy(Resource requestedResource, PolicyType policyType, String signedUsername) 
//			throws NoResourceAccessException, RecordNotFoundException;
	
	// No update AbacPolicy should be created 
	public void updatePolicy(String existingPolicyId, AbacPolicy updatedPolicy, String signedUsername) 
			throws NoResourceAccessException, InvalidPolicyException, RecordNotCreatedException;
	public void deletePolicy(String policyId, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
}
