package com.unlimitedcompanies.coms.ws.reps.abac;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.abac.AttributeCondition;

@XmlRootElement(name = "attributeCondition")
public class AttributeConditionDTO extends ResourceSupport
{
	private String attributeConditionId;
	private String userAttribute;
	private String resourceAttribute;
	private String comparison;
	
	protected AttributeConditionDTO() {}
	
	public AttributeConditionDTO(AttributeCondition attributeCondition)
	{
		this.attributeConditionId = attributeCondition.getAttributeConditionId();
		this.userAttribute = attributeCondition.getUserAttribute().toString();
		this.resourceAttribute = attributeCondition.getResourceAttribute().toString();
		this.comparison = attributeCondition.getComparison().toString();
	}

	public String getAttributeConditionId()
	{
		return attributeConditionId;
	}

	public void setAttributeConditionId(String attributeConditionId)
	{
		this.attributeConditionId = attributeConditionId;
	}

	public String getUserAttribute()
	{
		return userAttribute;
	}

	public void setUserAttribute(String userAttribute)
	{
		this.userAttribute = userAttribute;
	}

	public String getResourceAttribute()
	{
		return resourceAttribute;
	}

	public void setResourceAttribute(String resourceAttribute)
	{
		this.resourceAttribute = resourceAttribute;
	}

	public String getComparison()
	{
		return comparison;
	}

	public void setComparison(String comparison)
	{
		this.comparison = comparison;
	}
}
