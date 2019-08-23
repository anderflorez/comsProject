package com.unlimitedcompanies.coms.ws.reps.abac;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.abac.FieldCondition;

@XmlRootElement(name = "fieldCondition")
public class FieldConditionDTO extends ResourceSupport
{
	private String fieldConditionId;
	private String fieldName;
	private String value;
	private String comparison;

	protected FieldConditionDTO() {}
	
	public FieldConditionDTO(FieldCondition fieldCondition)
	{
		this.fieldConditionId = fieldCondition.getFieldConditionId();
		this.fieldName = fieldCondition.getFieldName();
		this.value = fieldCondition.getValue();
		this.comparison = fieldCondition.getComparison().toString();
	}

	public String getFieldConditionId()
	{
		return fieldConditionId;
	}

	public void setFieldConditionId(String fieldConditionId)
	{
		this.fieldConditionId = fieldConditionId;
	}

	public String getFieldName()
	{
		return fieldName;
	}

	public void setFieldName(String fieldName)
	{
		this.fieldName = fieldName;
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
