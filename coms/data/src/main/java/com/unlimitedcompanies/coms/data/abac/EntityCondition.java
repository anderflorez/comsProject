package com.unlimitedcompanies.coms.data.abac;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "entityCondition")
public class EntityCondition
{
	@Id
	private Integer entityConditionId;
	
	@Column(unique=false, nullable=false)
	private UserAttribute userAttribute;
	
	@Column(unique=false, nullable=false)
	private String value;
	
	@Column(unique=false, nullable=false)
	private ComparisonOperator comparison;
	
	@ManyToOne
	@JoinColumn(name="conditionGroupId_FK")
	private ConditionGroup parentConditionGroup;
	
	protected EntityCondition() {}
	
	protected EntityCondition(ConditionGroup conditionGroup, 
							  UserAttribute userAttribute,
							  ComparisonOperator comparison, 
							  String value)
	{
		this.userAttribute = userAttribute;
		this.value = value;
		this.comparison = comparison;
		this.parentConditionGroup = conditionGroup;
		if (!conditionGroup.getEntityConditions().contains(this))
		{
			conditionGroup.addEntityCondition(this);
		}
	}

	public Integer getEntityConditionId()
	{
		return entityConditionId;
	}

	private void setEntityConditionId(Integer entityConditionId)
	{
		this.entityConditionId = entityConditionId;
	}

	private String getUserAttribute()
	{
		return userAttribute.toString();
	}
	
	public UserAttribute getUserEnumAttribute()
	{
		return userAttribute;
	}

	private void setUserAttribute(UserAttribute userAttribute)
	{
		this.userAttribute = userAttribute;
	}
	
	private void setUserAttribute(String userAttribute)
	{
		this.userAttribute = UserAttribute.valueOf(userAttribute.toUpperCase());
	}

	public String getValue()
	{
		return value;
	}

	private void setValue(String value)
	{
		this.value = value;
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
	}
	
}
