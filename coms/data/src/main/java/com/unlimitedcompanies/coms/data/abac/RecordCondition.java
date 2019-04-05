package com.unlimitedcompanies.coms.data.abac;

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
	private Integer recordConditionId;
	
	@Column(unique=false, nullable=false)
	private UserAttribute userAttribute;
	
	@Column(unique=false, nullable=false)
	private ResourceAttribute resourceAttribute;
	
	@Column(unique=false, nullable=false)
	private ComparisonOperator comparison;
	
	@ManyToOne
	@JoinColumn(name="conditionGroupId_FK")
	private ConditionGroup parentConditionGroup;
	
	protected RecordCondition() {}
	
	protected RecordCondition(ConditionGroup conditionGroup, 
							  UserAttribute userAttribute, 
							  ComparisonOperator comparisonOperator, 
							  ResourceAttribute resourceAttribute)
	{
		this.userAttribute = userAttribute;
		this.comparison = comparisonOperator;
		this.resourceAttribute = resourceAttribute;
		this.parentConditionGroup = conditionGroup;
		if (!conditionGroup.getRecordConditions().contains(this))
		{
			conditionGroup.addRecordCondition(this);
		}
	}

	public Integer getRecordConditionId()
	{
		return recordConditionId;
	}

	private void setRecordConditionId(Integer recordConditionId)
	{
		this.recordConditionId = recordConditionId;
	}

	public UserAttribute getUserAttribute()
	{
		return userAttribute;
	}

	private void setUserAttribute(UserAttribute userAttribute)
	{
		this.userAttribute = userAttribute;
	}

	public ResourceAttribute getResourceAttribute()
	{
		return resourceAttribute;
	}

	private void setResourceAttribute(ResourceAttribute resourceAttribute)
	{
		this.resourceAttribute = resourceAttribute;
	}

	public ComparisonOperator getComparison()
	{
		return comparison;
	}

	private void setComparison(ComparisonOperator comparison)
	{
		this.comparison = comparison;
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
