package com.unlimitedcompanies.coms.service.system;

import java.util.List;

import com.unlimitedcompanies.coms.data.exceptions.DuplicatedResourcePolicyException;
import com.unlimitedcompanies.coms.data.exceptions.NoParentPolicyOrResourceException;
import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.domain.abac.ResourceField;
import com.unlimitedcompanies.coms.domain.abac.UserAttribs;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;

public interface SystemService
{
	/*
	 * Initial setup of resources for the security system
	 */
	public void initialSetup() throws DuplicatedResourcePolicyException, NoParentPolicyOrResourceException;
	public void checkAllResources();
	public List<String> searchAllResourceNames();	
	
	// Get objects with system rights which is without checking any permissions to obtain them
	public AbacPolicy searchPolicy(Resource resource, PolicyType policyType) throws NoResourceAccessException;
	public Role searchRoleById(int roleId) throws NoResourceAccessException;
	public Role roleWithAllRestrictedFields(Integer roleId) throws RecordNotFoundException;
	public ResourceField searchResourceFieldById(int fieldId) throws NoResourceAccessException;
	public List<String> searchRestrictedFields(int userId, int resourceId);
	
	public User searchFullUserByUsername(String username);
	public UserAttribs getUserAttribs(int userId);
	public void clearEntityManager();
}
