package com.unlimitedcompanies.coms.service.securityImpl;

import java.util.ArrayList;
import java.util.List;

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
import com.unlimitedcompanies.coms.service.security.SecuritySetupService;

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
	
	@Autowired
	private SecuritySetupService setupService;
	
	@Override
	public ABACPolicy savePolicy(ABACPolicy policy, String username) throws NoResourceAccessException
	{
		Resource policyResource = setupService.findResourceByNameWithFields("ABACPolicy");
		User user = authService.searchFullUserByUsername(username);
		UserAttribs userAttribs = new UserAttribs(username);		
		userAttribs.setRoles(user.getRoleNames());		
		
		ABACPolicy abacPolicy = systemAbacService.findPolicy(policyResource, PolicyType.UPDATE);
		if (abacPolicy.getModifyPolicy(null, userAttribs, user) && abacPolicy.getCdPolicy().isCreatePolicy())
		{
			abacDao.savePolicy(policy);
		}
		
		return this.findPolicyByName(policy.getPolicyName());
	}

	@Override
	public int getNumberOfPolicies()
	{
		return abacDao.getNumberOfPolicies();
	}

	@Override
	public int getNumberOfConditionGroups()
	{
		return abacDao.getNumberOfConditionGroups();
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
	public ABACPolicy findPolicy(Resource resource, PolicyType policyType, String username) throws NoResourceAccessException
	{
		User user = authService.searchFullUserByUsername(username);
		ABACPolicy policy = systemAbacService.findPolicy(resource, policyType);
		ResourceReadPolicy resourceReadPolicy = policy.getReadPolicy("abacPolicy", "project", "user", user);
		
		if (resourceReadPolicy.isReadGranted())
		{
			return abacDao.findPolicy(resource, policyType, resourceReadPolicy.getReadConditions());
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}

	@Override
	public ABACPolicy findPolicyByName(String policyName)
	{
		return abacDao.findPolicyByName(policyName);
	}

	
}
