package com.unlimitedcompanies.coms.ws.reps.abac;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.LazyInitializationException;
import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.domain.abac.ResourceField;

@XmlRootElement(name = "resource")
public class ResourceDTO extends ResourceSupport
{
	private Integer resourceId;
	private String resourceName;
	private Set<ResourceFieldDTO> resourceFields;
	private List<PolicyDTO> subpolicies;
	
	public ResourceDTO() 
	{
		this.resourceFields = new HashSet<>();
		this.subpolicies = new ArrayList<>();
	}
	
	public ResourceDTO(Resource resource)
	{
		this.resourceId = resource.getResourceId();
		this.resourceName = resource.getResourceName();
		this.resourceFields = new HashSet<>();
		this.subpolicies = new ArrayList<>();
		try
		{
			for (ResourceField field : resource.getResourceFields())
			{
				this.resourceFields.add(new ResourceFieldDTO(field));
			}
		}
		catch (LazyInitializationException e)
		{
			// The resource is not available
			this.resourceFields = null;
		}
		try
		{
			for (AbacPolicy policy : resource.getPolicies())
			{
				this.subpolicies.add(new PolicyDTO(policy));
			}
		}
		catch (LazyInitializationException e)
		{
			// The resource is not available
			this.subpolicies = null;
		}
	}
	
	public ResourceDTO(Integer resourceId, String resourceName)
	{
		this.resourceId = resourceId;
		this.resourceName = resourceName;
		this.resourceFields = null;
		this.subpolicies = null;
	}

	public Integer getResourceId()
	{
		return resourceId;
	}

	public void setResourceId(int resourceId)
	{
		this.resourceId = resourceId;
	}

	public String getResourceName()
	{
		return resourceName;
	}

	public void setResourceName(String resourceName)
	{
		this.resourceName = resourceName;
	}

	public Set<ResourceFieldDTO> getResourceFields()
	{
		return resourceFields;
	}

	public void setResourceFields(Set<ResourceFieldDTO> resourceFields)
	{
		this.resourceFields = resourceFields;
	}

	public List<PolicyDTO> getPolicies()
	{
		return subpolicies;
	}

	public void setPolicies(List<PolicyDTO> policies)
	{
		this.subpolicies = policies;
	}
}
