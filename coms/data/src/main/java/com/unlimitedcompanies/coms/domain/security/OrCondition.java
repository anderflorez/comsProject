package com.unlimitedcompanies.coms.domain.security;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.domain.search.Operator;

@Entity
@Table(name = "orCondition")
public class OrCondition
{
	@Id
	private Integer orConditionId;
	private String fieldName;
	private String fieldValue;
	private Operator operator;
	
	@ManyToOne
	@JoinColumn(name = "orGroupId_FK")
	private OrGroup orGroup;

	public OrCondition() {}

	public OrCondition(String fieldName, String fieldValue, Operator operator, OrGroup orGroup)
	{
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.operator = operator;
		this.orGroup = orGroup;
	}

	public Integer getOrConditionId()
	{
		return orConditionId;
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
		return operator;
	}

	public OrGroup getOrGroup()
	{
		return orGroup;
	}
	
	public void assignToGroup(OrGroup orGroup)
	{
		if (!this.orGroup.equals(orGroup))
		{
			this.orGroup = orGroup;
			orGroup.addOrCondition(this);
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + ((fieldValue == null) ? 0 : fieldValue.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((orGroup == null) ? 0 : orGroup.hashCode());
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
		OrCondition other = (OrCondition) obj;
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
		if (orGroup == null)
		{
			if (other.orGroup != null)
				return false;
		} else if (!orGroup.equals(other.orGroup))
			return false;
		return true;
	}
}
