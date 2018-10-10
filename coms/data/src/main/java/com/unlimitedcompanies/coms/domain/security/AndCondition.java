package com.unlimitedcompanies.coms.domain.security;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.domain.search.Operator;

@Entity
@Table(name = "andCondition")
public class AndCondition
{
	@Id
	private String andConditionId;
	private String fieldName;
	private String fieldValue;
	private String operator;

	@ManyToOne
	@JoinColumn(name = "andGroupId_FK")
	private AndGroup andGroup;

	public AndCondition() 
	{
		this.andConditionId = UUID.randomUUID().toString();
	}
	
	public AndCondition(String fieldName, String fieldValue, Operator operator)
	{
		this.andConditionId = UUID.randomUUID().toString();
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.operator = operator.toString();
		this.andGroup = null;
	}

	public String getAndConditionId()
	{
		return andConditionId;
	}

	public String getFieldName()
	{
		return fieldName;
	}

	public String getFieldValue()
	{
		return fieldValue;
	}

	public Operator getOperator()
	{
		return Operator.getNewOperator(this.operator);
	}

	public AndGroup getAndGroup()
	{
		return andGroup;
	}
	
	public void assignToGroup(AndGroup andGroup)
	{
		this.andGroup = andGroup;
	}
	
	public void assignToGroupBidirectional(AndGroup andGroup)
	{
		this.andGroup = andGroup;
		andGroup.addAndCondition(this);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((andGroup == null) ? 0 : andGroup.hashCode());
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + ((fieldValue == null) ? 0 : fieldValue.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AndCondition other = (AndCondition) obj;
		if (andGroup == null)
		{
			if (other.andGroup != null)
				return false;
		} else if (!andGroup.equals(other.andGroup))
			return false;
		if (fieldName == null)
		{
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		if (fieldValue == null)
		{
			if (other.fieldValue != null)
				return false;
		} else if (!fieldValue.equals(other.fieldValue))
			return false;
		if (operator != other.operator)
			return false;
		return true;
	}
}
