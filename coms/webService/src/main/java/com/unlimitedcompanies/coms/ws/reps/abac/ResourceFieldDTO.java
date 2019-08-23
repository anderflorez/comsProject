package com.unlimitedcompanies.coms.ws.reps.abac;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.abac.ResourceField;

@XmlRootElement(name = "resourceField")
public class ResourceFieldDTO extends ResourceSupport
{
	private int resourceFieldId;
	private String resourceFieldName;
	private boolean association;
	private ResourceDTO resource;
//	private List<RoleDTO> restrictedForRoles;

	protected ResourceFieldDTO() 
	{
//		this.restrictedForRoles = new ArrayList<>();
	}
	
	public ResourceFieldDTO(ResourceField resourceField)
	{
		this.resourceFieldId = resourceField.getResourceFieldId();
		this.resourceFieldName = resourceField.getResourceFieldName();
		this.association = resourceField.getAssociation();
		this.resource = new ResourceDTO(resourceField.getResource());
		 
		// TODO: complete this method by adding the restrictedForRoles field
	}

	public int getResourceFieldId()
	{
		return resourceFieldId;
	}

	public void setResourceFieldId(int resourceFieldId)
	{
		this.resourceFieldId = resourceFieldId;
	}

	public String getResourceFieldName()
	{
		return resourceFieldName;
	}

	public void setResourceFieldName(String resourceFieldName)
	{
		this.resourceFieldName = resourceFieldName;
	}

	public boolean isAssociation()
	{
		return association;
	}

	public void setAssociation(boolean association)
	{
		this.association = association;
	}

	public ResourceDTO getResource()
	{
		return resource;
	}

	public void setResource(ResourceDTO resource)
	{
		this.resource = resource;
	}
}
