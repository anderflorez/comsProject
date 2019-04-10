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
@Table(name = "recordCondition")
public class RecordCondition
{
	@Id
	private String recordConditionId;
	
	@Column(unique=false, nullable=false)
	private String userAttribute;
	
	@Column(unique=false, nullable=false)
	private String resourceAttribute;
	
	@Column(unique=false, nullable=false)
	private ComparisonOperator comparison;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinColumn(name="conditionGroupId_FK")
	private ConditionGroup parentConditionGroup;
	
	protected RecordCondition() 
	{
		this.recordConditionId = UUID.randomUUID().toString();
	}
	
	protected RecordCondition(ConditionGroup conditionGroup, 
							  UserAttribute userAttribute, 
							  ComparisonOperator comparisonOperator, 
							  ResourceAttribute resourceAttribute)
	{
		this.recordConditionId = UUID.randomUUID().toString();
		this.userAttribute = userAttribute.toString();
		this.comparison = comparisonOperator;
		this.resourceAttribute = resourceAttribute.toString();
		this.parentConditionGroup = conditionGroup;
		if (!conditionGroup.getRecordConditions().contains(this))
		{
			conditionGroup.addRecordCondition(this);
		}
	}

	public String getRecordConditionId()
	{
		return recordConditionId;
	}

	public UserAttribute getUserAttribute()
	{
		return UserAttribute.valueOf(this.userAttribute.toUpperCase());
	}

	public ResourceAttribute getResourceAttribute()
	{
		return ResourceAttribute.valueOf(this.resourceAttribute.toUpperCase());
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
		if (!parentConditionGroup.getRecordConditions().contains(this))
		{
			parentConditionGroup.addRecordCondition(this);
		}
	}
	
}
