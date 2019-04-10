package com.unlimitedcompanies.coms.domain.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.data.abac.ABACPolicy;
import com.unlimitedcompanies.coms.data.abac.PolicyType;
import com.unlimitedcompanies.coms.data.exceptions.DuplicatedResourcePolicyException;

@Entity
@Table(name = "resource")
public class Resource
{
	@Id
	private Integer resourceId;
	private String resourceName;
	
	@OneToMany(mappedBy="resource")
	private Set<ResourceField> resourceFields;
	
	@OneToMany(mappedBy="resource")
	private Set<Permission> permissions;
	
	@OneToMany(mappedBy="resource")
	private List<ABACPolicy> policies;

	public Resource() 
	{
		this.resourceFields = new HashSet<>();
		this.permissions = new HashSet<>();
		this.policies = new ArrayList<>(); 
	}	

	public Resource(String resourceName)
	{
		this.resourceFields = new HashSet<>();
		this.permissions = new HashSet<>();
		this.policies = new ArrayList<>(); 
		this.resourceId = null;
		this.resourceName = resourceName;
	}

	public Integer getResourceId()
	{
		return resourceId;
	}

	public String getResourceName()
	{
		return resourceName;
	}
	
	public Set<ResourceField> getResourceFields()
	{
		return resourceFields;
	}
	
	public ResourceField getResourceFieldByName(String fieldName)
	{		
		if (this.resourceFields != null)
		{
			for (ResourceField next : this.resourceFields)
			{
				if (next.getResourceFieldName().equals(fieldName))
				{
					return next;
				}
			} 
		}
		return null;
	}

	public void addField(ResourceField field)
	{
		if (!this.resourceFields.contains(field))
		{
			this.resourceFields.add(field);
			field.assignResource(this);
		}
	}
	
	public void addPermission(Permission permission)
	{
		if (!this.permissions.contains(permission))
		{
			this.permissions.add(permission);
			permission.assignResource(this);
		}
	}

	public List<ABACPolicy> getPolicies()
	{
		return policies;
	}

	protected void setPolicies(List<ABACPolicy> policies)
	{
		this.policies = policies;
	}
	
	public void addPolicy(ABACPolicy policy) throws DuplicatedResourcePolicyException
	{
		if (!this.verifyExistingPolicy(policy))
		{
			this.policies.add(policy);
			if (policy.getResource() != this)
			{
				policy.setResource(this);
			}
		}
		else
		{
			throw new DuplicatedResourcePolicyException();
		}
	}
	
	public void addPolicy(String policyName, PolicyType policyType) throws DuplicatedResourcePolicyException
	{
		new ABACPolicy(policyName, policyType, this);
	}
	
	private boolean verifyExistingPolicy(ABACPolicy policy)
	{
		for (ABACPolicy next : this.policies)
		{
			if (next.equals(policy))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resourceName == null) ? 0 : resourceName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Resource other = (Resource) obj;
		if (resourceName == null)
		{
			if (other.resourceName != null)
				return false;
		} else if (!resourceName.equals(other.resourceName))
			return false;
		return true;
	}
}
