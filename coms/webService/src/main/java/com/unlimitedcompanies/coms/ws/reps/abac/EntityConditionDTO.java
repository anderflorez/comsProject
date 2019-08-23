package com.unlimitedcompanies.coms.ws.reps.abac;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.abac.EntityCondition;

@XmlRootElement(name = "entityCondition")
public class EntityConditionDTO extends ResourceSupport
{
	private String entityConditionId;
	private String userAttribute;
	private String value;
	private String comparison;
	
	protected EntityConditionDTO() {}
	
	public EntityConditionDTO(EntityCondition entityCondition)
	{
		this.entityConditionId = entityCondition.getEntityConditionId();
		this.userAttribute = entityCondition.getUserAttribute().toString();
		this.value = entityCondition.getValue();
		this.comparison = entityCondition.getComparison().toString();
	}

	public String getEntityConditionId()
	{
		return entityConditionId;
	}

	public void setEntityConditionId(String entityConditionId)
	{
		this.entityConditionId = entityConditionId;
	}

	public String getUserAttribute()
	{
		return userAttribute;
	}

	public void setUserAttribute(String userAttribute)
	{
		this.userAttribute = userAttribute;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
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
