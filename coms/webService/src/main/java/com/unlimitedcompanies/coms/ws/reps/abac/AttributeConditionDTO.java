package com.unlimitedcompanies.coms.ws.reps.abac;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.abac.AttributeCondition;
import com.unlimitedcompanies.coms.domain.abac.ComparisonOperator;
import com.unlimitedcompanies.coms.domain.abac.ResourceAttribute;

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
		attributeConditionId = attributeCondition.getAttributeConditionId();
		
		userAttribute = attributeCondition.getUserAttribute().toString();
		userAttribute = userAttribute.substring(0, 1).toUpperCase() + userAttribute.substring(1).toLowerCase();
		
		switch (attributeCondition.getResourceAttribute())
		{
			case PROJECT_NAME:
				resourceAttribute = "Project Name";
				break;
			case P_MANAGERS:
				resourceAttribute = "Project Manager";
				break;
			case P_SUPERINTENDENTS:
				resourceAttribute = "Superintendent";
				break;
			case P_FOREMEN:
				resourceAttribute = "Foreman";
				break;
		}
		
		if (attributeCondition.getComparison().equals(ComparisonOperator.EQUALS))
		{
			this.comparison = "is equal to";
		}
		else if (attributeCondition.getComparison().equals(ComparisonOperator.NOT_EQUALS))
		{
			this.comparison = "is not equal to";
		}
		else
		{
			this.comparison = attributeCondition.getComparison().toString();
		}
	}

	public String getAttributeConditionId()
	{
		return attributeConditionId;
	}
//
//	public void setAttributeConditionId(String attributeConditionId)
//	{
//		this.attributeConditionId = attributeConditionId;
//	}

	public String getUserAttribute()
	{
		return userAttribute;
	}
//
//	public void setUserAttribute(String userAttribute)
//	{
//		this.userAttribute = userAttribute;
//	}

	public String getResourceAttribute()
	{
		return resourceAttribute;
	}
//
//	public void setResourceAttribute(String resourceAttribute)
//	{
//		this.resourceAttribute = resourceAttribute;
//	}

	public String getComparison()
	{
		return comparison;
	}
//
//	public void setComparison(String comparison)
//	{
//		this.comparison = comparison;
//	}
}
