package com.unlimitedcompanies.coms.data.abac;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "fieldCondition")
public class FieldCondition
{
	@Id
	private String fieldConditionId;
	
	@Column(unique=false, nullable=false)
	private String fieldName;
	
	@Column(unique=false, nullable=false)
	private String value;
	
	@Column(unique=false, nullable=false)
	private ComparisonOperator comparison;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinColumn(name="conditionGroupId_FK")
	private ConditionGroup parentConditionGroup;
	
	protected FieldCondition()
	{
		this.fieldConditionId = UUID.randomUUID().toString();
	}
	
	protected FieldCondition(String fieldName, ComparisonOperator comparison, 
							 String value, ConditionGroup parentConditionGroup)
	{
		this.fieldConditionId = UUID.randomUUID().toString();
		this.fieldName = fieldName;
		this.comparison = comparison;
		this.value = value;
		this.parentConditionGroup = parentConditionGroup;
		if (!parentConditionGroup.getFieldConditions().contains(this))
		{
			parentConditionGroup.addFieldConditions(this);
		}
	}

	public String getFieldConditionId()
	{
		return fieldConditionId;
	}

	public String getFieldName()
	{
		return fieldName;
	}

	public String getValue()
	{
		return value;
	}

	public ComparisonOperator getComparison()
	{
		return comparison;
	}
	
	public ConditionGroup getParentConditionGroup()
	{
		return parentConditionGroup;
	}
	
	protected void setParentConditionGroup(ConditionGroup parentConditionGroup)
	{
		this.parentConditionGroup = parentConditionGroup;
		if (!parentConditionGroup.getFieldConditions().contains(this))
		{
			parentConditionGroup.addFieldConditions(this);
		}
	}	
	
}
