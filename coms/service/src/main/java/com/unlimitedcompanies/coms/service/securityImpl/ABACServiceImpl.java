package com.unlimitedcompanies.coms.service.securityImpl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.ABACDao;
import com.unlimitedcompanies.coms.data.exceptions.InvalidPolicyException;
import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.domain.abac.ResourceField;
import com.unlimitedcompanies.coms.domain.abac.ResourceReadPolicy;
import com.unlimitedcompanies.coms.domain.abac.UserAttribs;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotCreatedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.AbacService;
import com.unlimitedcompanies.coms.service.system.SystemService;

@Service
@Transactional
public class ABACServiceImpl implements AbacService
{
	
	@Autowired
	private ABACDao abacDao;
	
	@Autowired
	private SystemService systemService;
	
	@Override
	public void savePolicy(AbacPolicy policy, String signedUsername) throws NoResourceAccessException, InvalidPolicyException
	{
		User signedUser = systemService.searchFullUserByUsername(signedUsername);

		Resource policyResource = this.searchResourceByNameWithFields("AbacPolicy");
		
		UserAttribs userAttribs = systemService.getUserAttribs(signedUser.getUserId());
		
		AbacPolicy abacPolicy = systemService.searchPolicy(policyResource, PolicyType.UPDATE);
		if (abacPolicy.getModifyPolicy(null, userAttribs, signedUser) && abacPolicy.getCdPolicy().isCreatePolicy())
		{
			try
			{
				abacDao.savePolicy(policy);
				systemService.clearEntityManager();
			}
			catch (PersistenceException e)
			{
				if (e.getCause() != null && e.getCause().getCause() != null)
				{
					String message = e.getCause().getCause().getMessage();
					if (message.contains("for key 'Resource_PolicyType'"))
					{
						throw new InvalidPolicyException("The referenced resouce already contains a policy of the same type");
					}
					else if (message.contains("for key 'policyName_UNIQUE'"))
					{
						throw new InvalidPolicyException("The policy already exists");
					}
				}
			}
		}
		else
		{
			throw new NoResourceAccessException();
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
	public List<String> allawedResources(String username)
	{
		User user = systemService.searchFullUserByUsername(username);
		UserAttribs userAttribs = systemService.getUserAttribs(user.getUserId());

		List<Resource> qualifiedResources = new ArrayList<>();
		List<Resource> allResources = systemService.searchAllResources(); 
		
		allResources.forEach((resource) -> {
			if (resource.getResourceName().equals("AbacPolicy") ||
				resource.getResourceName().equals("Contact") ||
				resource.getResourceName().equals("User") ||
				resource.getResourceName().equals("Role") ||
				resource.getResourceName().equals("Employee") ||
				resource.getResourceName().equals("Project"))
			{
				qualifiedResources.add(resource);
			}
		});
		
		List<String> allowedResourceNames = new ArrayList<>();
		for (Resource resource : qualifiedResources)
		{
			AbacPolicy policy;
			try
			{
				policy = systemService.searchPolicy(resource, PolicyType.READ);
				if (policy.isEntityAccessGranted(user) || policy.getModifyPolicy(null, userAttribs, user))
				{
					allowedResourceNames.add(resource.getResourceName());
				}
			}
			catch (NoResourceAccessException e) {}
		}
		
		return allowedResourceNames;
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
	public int getNumberOfMainPolicies(String signedUsername) throws RecordNotFoundException, NoResourceAccessException
	{
		// Check if user has access to read policies
		User user = systemService.searchFullUserByUsername(signedUsername);
		Resource abacPolicyResource = this.searchResourceByName("AbacPolicy");
		AbacPolicy policy = systemService.searchPolicy(abacPolicyResource, PolicyType.READ);
		
		ResourceReadPolicy resourceReadPolicy = policy.getReadPolicy(AbacPolicy.class, user);
		
		// If user has access then read the requested policy and return it
		// TODO: Return number of policies with any kind of access, not just read
		if (resourceReadPolicy.isReadGranted())
		{
			return abacDao.getNumberOfMainPolicies();
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public AbacPolicy searchPolicy(Resource requestedResource, PolicyType policyType, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException
	{
		// Check if user has access to read policies
		User user = systemService.searchFullUserByUsername(signedUsername);
		Resource abacPolicyResource = this.searchResourceByName("AbacPolicy");
		AbacPolicy policy = systemService.searchPolicy(abacPolicyResource, PolicyType.READ);
		
		ResourceReadPolicy resourceReadPolicy = policy.getReadPolicy(AbacPolicy.class, user);
		
		// If user has access then read the requested policy and return it
		if (resourceReadPolicy.isReadGranted())
		{
			AbacPolicy requestedPolicy;
			try
			{
				requestedPolicy = abacDao.getPolicy(requestedResource, policyType, resourceReadPolicy.getReadConditions());
				systemService.clearEntityManager();
				return requestedPolicy;
			}
			catch (NoResultException e)
			{
				throw new NoResourceAccessException();
			}
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public AbacPolicy searchPolicyWithRestrictedFields(Resource requestedResource, PolicyType policyType, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException
	{
		// Check if user has access to read policies
		User user = systemService.searchFullUserByUsername(signedUsername);
		Resource abacPolicyResource = this.searchResourceByName("AbacPolicy");
		AbacPolicy policy = systemService.searchPolicy(abacPolicyResource, PolicyType.READ);
		
		ResourceReadPolicy resourceReadPolicy = policy.getReadPolicy(AbacPolicy.class, user);
		
		// If user has access then read the requested policy and return it
		if (resourceReadPolicy.isReadGranted())
		{
			AbacPolicy requestedPolicy;
			try
			{
				requestedPolicy = abacDao.getPolicyWithRestrictedFields(requestedResource, 
																		policyType, 
																		resourceReadPolicy.getReadConditions());
				systemService.clearEntityManager();
				return requestedPolicy;
			}
			catch (NoResultException e)
			{
				throw new NoResourceAccessException();
			}
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public AbacPolicy searchPolicyById(String abacPolicyId, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		// Check if user has access to read policies
		User user = systemService.searchFullUserByUsername(signedUsername);
		Resource abacPolicyResource = this.searchResourceByName("AbacPolicy");
		AbacPolicy policy = systemService.searchPolicy(abacPolicyResource, PolicyType.READ);
		
		ResourceReadPolicy resourceReadPolicy = policy.getReadPolicy(AbacPolicy.class, user);
		
		// If user has access then read the requested policy and return it
		if (resourceReadPolicy.isReadGranted())
		{
			AbacPolicy requestedPolicy;
			try
			{
				requestedPolicy = abacDao.getPolicyById(abacPolicyId, resourceReadPolicy.getReadConditions());		
				systemService.clearEntityManager();
				return requestedPolicy;
			}
			catch (NoResultException e)
			{
				throw new NoResourceAccessException();
			}
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public List<AbacPolicy> searchPoliciesByRange(int elements, int page, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		// Check if user has access to read policies
		User user = systemService.searchFullUserByUsername(signedUsername);
		Resource abacPolicyResource = this.searchResourceByName("AbacPolicy");
		AbacPolicy policy = systemService.searchPolicy(abacPolicyResource, PolicyType.READ);
		
		ResourceReadPolicy resourceReadPolicy = policy.getReadPolicy(AbacPolicy.class, user);
		
		// If user has access then read the requested policy and return it
		if (resourceReadPolicy.isReadGranted())
		{
			List<AbacPolicy> policies = abacDao.getPoliciesByRange(elements, page - 1, resourceReadPolicy.getReadConditions());
			systemService.clearEntityManager();
			return policies;
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}

	@Override
	public void updatePolicy(String existingPolicyId, AbacPolicy updatedPolicy, String signedUsername) 
			throws NoResourceAccessException, InvalidPolicyException, RecordNotCreatedException
	{
		User signedUser = systemService.searchFullUserByUsername(signedUsername);

		Resource policyResource = this.searchResourceByNameWithFields("AbacPolicy");
		
		UserAttribs userAttribs = systemService.getUserAttribs(signedUser.getUserId());
		
		AbacPolicy abacPolicy = systemService.searchPolicy(policyResource, PolicyType.UPDATE);
		if (abacPolicy.getModifyPolicy(null, userAttribs, signedUser) && abacPolicy.getCdPolicy().isCreatePolicy())
		{
			try
			{
				AbacPolicy existingPolicy = abacDao.getPolicyById(existingPolicyId, null);
								
				abacDao.deletePolicy(existingPolicyId);
				systemService.clearEntityManager();
				
				System.out.println("Verifying the backup existing policy: " + existingPolicy.getPolicyName());
				
				try
				{
					abacDao.savePolicy(updatedPolicy);
				}
				catch (Exception e1)
				{
					abacDao.savePolicy(existingPolicy);
					throw new RecordNotCreatedException();
				}
				finally
				{
					systemService.clearEntityManager();
				}
			}
			catch (PersistenceException e)
			{
				if (e.getCause() != null && e.getCause().getCause() != null)
				{
					String message = e.getCause().getCause().getMessage();
					if (message.contains("for key 'Resource_PolicyType'"))
					{
						throw new InvalidPolicyException("The referenced resouce already contains a policy of the same type");
					}
				}
			}
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public void deletePolicy(String policyId, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		User signedUser = systemService.searchFullUserByUsername(signedUsername);

		Resource policyResource = this.searchResourceByNameWithFields("AbacPolicy");
		
		UserAttribs userAttribs = systemService.getUserAttribs(signedUser.getUserId());
		
		AbacPolicy abacPolicy = systemService.searchPolicy(policyResource, PolicyType.UPDATE);
		if (abacPolicy.getModifyPolicy(null, userAttribs, signedUser) && abacPolicy.getCdPolicy().isDeletePolicy())
		{
			try
			{
				abacDao.deletePolicy(policyId);
			}
			catch (NoResultException e)
			{
				throw new RecordNotFoundException("The referenced policy could not be found");
			}
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
}
