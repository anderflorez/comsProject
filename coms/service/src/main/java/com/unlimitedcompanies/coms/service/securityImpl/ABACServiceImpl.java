package com.unlimitedcompanies.coms.service.securityImpl;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.ABACDao;
import com.unlimitedcompanies.coms.domain.abac.ABACPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.ResourceReadPolicy;
import com.unlimitedcompanies.coms.domain.abac.UserAttribs;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.abac.SystemAbacService;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.security.ABACService;
import com.unlimitedcompanies.coms.service.security.AuthService;

@Service
@Transactional
public class ABACServiceImpl implements ABACService
{
	
	@Autowired
	private ABACDao abacDao;
	
	@Autowired
	private SystemAbacService systemAbacService;
	
	@Autowired
	private AuthService authService;
	
	@Override
	public Resource findResourceByName(String name)
	{
		return abacDao.findResourceByName(name);
	}

	@Override
	public Resource findResourceByNameWithFields(String name)
	{
		return abacDao.findResourceByNameWithFields(name);
	}

	@Override
	public Resource findResourceByNameWithFieldsAndPolicy(String name)
	{
		return abacDao.findResourceByNameWithFieldsAndPolicy(name);
	}
	
	@Override
	public void savePolicy(ABACPolicy policy, String username) throws NoResourceAccessException
	{
		Resource policyResource = this.findResourceByNameWithFields("ABACPolicy");
		User user = authService.searchFullUserByUsername(username);
		UserAttribs userAttribs = new UserAttribs(username);
		userAttribs.setRoles(user.getRoleNames());
		
		ABACPolicy abacPolicy = systemAbacService.findPolicy(policyResource, PolicyType.UPDATE);
		if (abacPolicy.getModifyPolicy(null, userAttribs, user) && abacPolicy.getCdPolicy().isCreatePolicy())
		{
			abacDao.savePolicy(policy);
		}

	}

	@Override
	public int getNumberOfPolicies()
	{
		return abacDao.getNumberOfPolicies();
	}
	
	@Override
	public int getNumberOfEntityConditions()
	{
		return abacDao.getNumberOfEntityConditions();
	}
	
	@Override
	public int getNumberOfAttributeConditions()
	{
		return abacDao.getNumberOfAttributeConditions();
	}
	
	@Override
	public ABACPolicy findPolicy(Resource requestedResource, PolicyType policyType, String username) throws NoResourceAccessException
	{
		// Check if user has access to read policies
		User user = authService.searchFullUserByUsername(username);
		Resource abacPolicyResource = this.findResourceByName("ABACPolicy");
		ABACPolicy policy;
		try
		{
			policy = systemAbacService.findPolicy(abacPolicyResource, PolicyType.READ);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceReadPolicy resourceReadPolicy = policy.getReadPolicy("abacPolicy", "project", user);
		
		// If user has access then read the requested policy and return it
		if (resourceReadPolicy.isReadGranted())
		{
			return abacDao.findPolicy(requestedResource, policyType, resourceReadPolicy.getReadConditions());
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}

	@Override
	public int getNumberOfRestrictedFields()
	{
		return abacDao.getNumberOfRestrictedFields();
	}
	
}
