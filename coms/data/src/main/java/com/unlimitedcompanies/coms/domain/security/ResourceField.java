package com.unlimitedcompanies.coms.domain.security;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "resourceField")
public class ResourceField
{
	@Id
	private Integer resourceFieldId;
	private String resourceFieldName;
	private byte association;

	@ManyToOne
	@JoinColumn(name = "resourceId_FK")
	private Resource resource;

	protected ResourceField()
	{}

	public ResourceField(String resourceFieldName, byte association, Resource resource)
	{
		this.resourceFieldId = null;
		this.resourceFieldName = resourceFieldName;
		this.association = association;
		this.resource = resource;
	}

	public Integer getResourceFieldId()
	{
		return resourceFieldId;
	}

	public String getResourceFieldName()
	{
		return resourceFieldName;
	}

	public byte getAssociation()
	{
		return association;
	}

	public Resource getResource()
	{
		return resource;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + association;
		result = prime * result + ((resource == null) ? 0 : resource.hashCode());
		result = prime * result + ((resourceFieldName == null) ? 0 : resourceFieldName.hashCode());
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
		ResourceField other = (ResourceField) obj;
		if (association != other.association)
			return false;
		if (resource == null)
		{
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		if (resourceFieldName == null)
		{
			if (other.resourceFieldName != null)
				return false;
		} else if (!resourceFieldName.equals(other.resourceFieldName))
			return false;
		return true;
	}

}
