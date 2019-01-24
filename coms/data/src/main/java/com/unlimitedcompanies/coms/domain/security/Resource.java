package com.unlimitedcompanies.coms.domain.security;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "resource")
public class Resource
{
	@Id
	private Integer resourceId;
	private String resourceName;
	
	@OneToMany(mappedBy = "resource")
	private Set<ResourceField> resourceFields = new HashSet<>();
	
	@OneToMany(mappedBy = "resource")
	private Set<ResourcePermissions> permissions = new HashSet<>();

	public Resource() {}

	public Resource(String resourceName)
	{
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
		// TODO: need to improve possibly using lambda
		
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
	
	public void addPermission(ResourcePermissions permission)
	{
		if (!this.permissions.contains(permission))
		{
			this.permissions.add(permission);
			permission.assignResource(this);
		}
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
