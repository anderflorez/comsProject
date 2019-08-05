package com.unlimitedcompanies.coms.service.securityImpl;

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
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.ABACService;
import com.unlimitedcompanies.coms.service.system.SystemService;

@Service
@Transactional
public class ABACServiceImpl implements ABACService
{
	
	@Autowired
	private ABACDao abacDao;
	
	@Autowired
	private SystemService systemService;
	
	@Override
	public void savePolicy(AbacPolicy policy, String signedUsername) throws NoResourceAccessException
	{
		User signedUser = systemService.searchFullUserByUsername(signedUsername);

		Resource policyResource = this.searchResourceByNameWithFields("AbacPolicy");
		
		UserAttribs userAttribs = new UserAttribs(signedUsername);
		// TODO: Verify why the next line is there - it seems like it shouldn't be there
		userAttribs.setRoles(signedUser.getRoleNames());
		
		AbacPolicy abacPolicy = systemService.searchPolicy(policyResource, PolicyType.UPDATE);
		if (abacPolicy.getModifyPolicy(null, userAttribs, signedUser) && abacPolicy.getCdPolicy().isCreatePolicy())
		{
			abacDao.savePolicy(policy);
			systemService.clearEntityManager();
		}
		
	}
	
	@Override
	public void addFieldRestriction(int roleId, int fieldId, String loggedUser) throws NoResourceAccessException, RecordNotFoundException
	{
		Resource restrictedFieldResource = this.searchResourceByName("RestrictedField");
		User signedUser = systemService.searchFullUserByUsername(loggedUser);
		
		UserAttribs userAttribs = systemService.getUserAttribs(signedUser.getUserId());
		AbacPolicy restrictionPolicy = systemService.searchPolicy(restrictedFieldResource, PolicyType.UPDATE);
		
		if (restrictionPolicy.getModifyPolicy(null, userAttribs, signedUser) && restrictionPolicy.getCdPolicy().isCreatePolicy())
		{
			ResourceField resourceField;
			Role role;
			try
			{
				resourceField = systemService.searchResourceFieldById(fieldId);
				
				// Fields in the Role resource (roleId and roleName) are not allowed to be restricted
				if (resourceField.getResource().getResourceName().equals("Role"))
				{
					throw new NoResourceAccessException();
				}
				
				role = systemService.searchRoleById(roleId);
			}
			catch (NoResultException e)
			{
				throw new NoResourceAccessException();
			}
			
			resourceField.assignRestrictedRole(role);
			systemService.clearEntityManager();
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public Resource searchResourceByName(String name) throws RecordNotFoundException
	{
		Resource resource;
		try
		{
			resource = abacDao.getResourceByName(name);
		}
		catch (NoResultException e)
		{
			throw new RecordNotFoundException("The resource " + name + " could not be found");
		}
		systemService.clearEntityManager();
		return resource;
	}

	@Override
	public Resource searchResourceByNameWithFields(String name)
	{
		Resource resource = abacDao.getResourceByNameWithFields(name);
		systemService.clearEntityManager();
		return resource;
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
	public AbacPolicy searchPolicy(Resource requestedResource, PolicyType policyType, String username) 
			throws NoResourceAccessException, RecordNotFoundException
	{
		// Check if user has access to read policies
		User user = systemService.searchFullUserByUsername(username);
		Resource abacPolicyResource = this.searchResourceByName("AbacPolicy");
		AbacPolicy policy = systemService.searchPolicy(abacPolicyResource, PolicyType.READ);
		
		ResourceReadPolicy resourceReadPolicy = policy.getReadPolicy(AbacPolicy.class, user);
		
		// If user has access then read the requested policy and return it
		if (resourceReadPolicy.isReadGranted())
		{
			AbacPolicy requestedPolicy = abacDao.getPolicy(requestedResource, policyType, resourceReadPolicy.getReadConditions());
			systemService.clearEntityManager();
			return requestedPolicy;
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}

	@Override
	public AbacPolicy searchModifiablePolicy(Resource requestedResource, PolicyType policyType, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException
	{
		// Check if user has access to read policies
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		Resource abacPolicyResource = this.searchResourceByName("AbacPolicy");
		
		AbacPolicy policy = systemService.searchPolicy(abacPolicyResource, PolicyType.READ);
		ResourceReadPolicy resourceReadPolicy = policy.getReadPolicy(AbacPolicy.class, signedUser);

		UserAttribs userAttribs = new UserAttribs(signedUsername);
		AbacPolicy abacPolicy = systemService.searchPolicy(abacPolicyResource, PolicyType.UPDATE);
		
		// If user has access then read and update the requested policy and return it
		if (resourceReadPolicy.isReadGranted() && abacPolicy.getModifyPolicy(null, userAttribs, signedUser))
		{
			AbacPolicy requestedPolicy = abacDao.getPolicy(requestedResource, policyType, resourceReadPolicy.getReadConditions());
			return requestedPolicy;
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
}
