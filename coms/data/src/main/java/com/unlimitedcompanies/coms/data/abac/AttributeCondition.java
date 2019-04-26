package com.unlimitedcompanies.coms.data.abac;

import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.domain.security.User;

@Entity
@Table(name = "attributeConditions")
public class AttributeCondition
{
	@Id
	private String attributeConditionId;
	
	@Column(unique=false, nullable=false)
	private String userAttribute;
	
	@Column(unique=false, nullable=false)
	private String resourceAttribute;
	
	@Column(unique=false, nullable=false)
	private ComparisonOperator comparison;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinColumn(name="conditionGroupId_FK")
	private ConditionGroup parentConditionGroup;
	
	protected AttributeCondition() 
	{
		this.attributeConditionId = UUID.randomUUID().toString();
	}
	
	protected AttributeCondition(ConditionGroup conditionGroup, 
							     UserAttribute userAttribute, 
							     ComparisonOperator comparisonOperator, 
							     ResourceAttribute resourceAttribute)
	{
		this.attributeConditionId = UUID.randomUUID().toString();
		this.userAttribute = userAttribute.toString();
		this.comparison = comparisonOperator;
		this.resourceAttribute = resourceAttribute.toString();
		this.parentConditionGroup = conditionGroup;
		if (!conditionGroup.getAttributeConditions().contains(this))
		{
			conditionGroup.addAttributeCondition(this);
		}
	}

	public String getAttributeConditionId()
	{
		return attributeConditionId;
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
		if (!parentConditionGroup.getAttributeConditions().contains(this))
		{
			parentConditionGroup.addAttributeCondition(this);
		}
	}
	
	protected String getReadPolicy(User user, String resourceEntityName)
	{
		String attributePart = "project." + this.getResourceAttribute().getProjectField() + 
							   " " + this.getComparison().getOperator() + " ";
		
		List<String> userAttributes = this.getUserAttribute().getUserField(user);
		
		String result = "";
		for (int i = 0; i < userAttributes.size(); i++)
		{
			if (i > 0) result += " OR ";
			result += attributePart + userAttributes.get(i);
		}
		
		return result;
	}
}
