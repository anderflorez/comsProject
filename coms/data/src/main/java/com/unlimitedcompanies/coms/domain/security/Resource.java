package com.unlimitedcompanies.coms.domain.security;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "resource")
public class Resource
{
	@Id
	private Integer resourceId;
	private String resourceName;

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
