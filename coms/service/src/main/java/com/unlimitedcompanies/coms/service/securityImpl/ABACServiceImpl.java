package com.unlimitedcompanies.coms.service.securityImpl;

import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.ABACDao;
import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.domain.abac.ResourceField;
import com.unlimitedcompanies.coms.domain.abac.ResourceReadPolicy;
import com.unlimitedcompanies.coms.domain.abac.UserAttribs;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.abac.SystemService;
import com.unlimitedcompanies.coms.service.abacImpl.SystemServiceImpl;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.security.ABACService;

@Service
@Transactional
public class ABACServiceImpl implements ABACService
{
	
	@Autowired
	private ABACDao abacDao;
	
	@Autowired
	private SystemService systemService;
	
	@Override
	public void savePolicy(AbacPolicy policy, String username) throws NoResourceAccessException
	{
		Resource policyResource = this.searchResourceByNameWithFields("AbacPolicy");
		User user = systemService.searchFullUserByUsername(username);
		UserAttribs userAttribs = new UserAttribs(username);
		userAttribs.setRoles(user.getRoleNames());
		
		AbacPolicy abacPolicy = systemService.searchPolicy(policyResource, PolicyType.UPDATE);
		if (abacPolicy.getModifyPolicy(null, userAttribs, user) && abacPolicy.getCdPolicy().isCreatePolicy())
		{
			abacDao.savePolicy(policy);
		}
		
	}
	
	@Override
	public void addFieldRestriction(int fieldId, int roleId, String loggedUser) throws NoResourceAccessException
	{
		Resource roleResource = this.searchResourceByName("Role");
		Resource fieldResource = this.searchResourceByName("ResourceField");
		User signedUser = systemService.searchFullUserByUsername(loggedUser);
		
		UserAttribs userAttribs = systemService.getUserAttribs(signedUser.getUserId());
		AbacPolicy fieldPolicy = systemService.searchPolicy(fieldResource, PolicyType.UPDATE);
		AbacPolicy rolePolicy = systemService.searchPolicy(roleResource, PolicyType.UPDATE);
		if (fieldPolicy.getModifyPolicy(null, userAttribs, signedUser) && rolePolicy.getModifyPolicy(null, userAttribs, signedUser))
		{
			ResourceField resourceField;
			Role role;
			try
			{
				resourceField = systemService.searchResourceFieldById(fieldId);
				role = systemService.searchRoleById(roleId);
			}
			catch (NoResultException e)
			{
				throw new NoResourceAccessException();
			}
			
			resourceField.assignRestrictedRole(role);
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public Resource searchResourceByName(String name)
	{
		return abacDao.searchResourceByName(name);
	}

	@Override
	public Resource searchResourceByNameWithFields(String name)
	{
		return abacDao.searchResourceByNameWithFields(name);
	}

	@Override
	public Resource searchResourceByNameWithFieldsAndPolicy(String name)
	{
		return abacDao.searchResourceByNameWithFieldsAndPolicy(name);
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
	public int getNumberOfRestrictedFields()
	{
		return abacDao.getNumberOfRestrictedFields();
	}
	
	@Override
	public AbacPolicy searchPolicy(Resource requestedResource, PolicyType policyType, String username) throws NoResourceAccessException
	{
		// Check if user has access to read policies
		User user = systemService.searchFullUserByUsername(username);
		Resource abacPolicyResource = this.searchResourceByName("AbacPolicy");
		AbacPolicy policy;
		try
		{
			policy = systemService.searchPolicy(abacPolicyResource, PolicyType.READ);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceReadPolicy resourceReadPolicy = policy.getReadPolicy("abacPolicy", "project", user);
		
		// If user has access then read the requested policy and return it
		if (resourceReadPolicy.isReadGranted())
		{
			return abacDao.searchPolicy(requestedResource, policyType, resourceReadPolicy.getReadConditions());
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
}
