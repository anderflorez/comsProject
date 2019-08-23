package com.unlimitedcompanies.coms.domain.abac;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "fieldConditions")
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
	
	@ManyToOne//(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinColumn(name="abacPolicyId_FK")
	private AbacPolicy abacPolicy;
	
	protected FieldCondition()
	{
		this.fieldConditionId = UUID.randomUUID().toString();
	}
	
	protected FieldCondition(String fieldName, ComparisonOperator comparison, 
							 String value, AbacPolicy abacPolicy)
	{
		this.fieldConditionId = UUID.randomUUID().toString();
		this.fieldName = fieldName;
		this.comparison = comparison;
		this.value = value;
		this.abacPolicy = abacPolicy;
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
	
	public AbacPolicy getAbacPolicy()
	{
		return abacPolicy;
	}

	protected void setAbacPolicy(AbacPolicy abacPolicy)
	{
		this.abacPolicy = abacPolicy;
	}

	protected String getReadPolicy(String resourceEntityName)
	{
		return resourceEntityName + "." + this.fieldName + " " + this.getComparison().getOperator() + " '" + this.value + "'";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comparison == null) ? 0 : comparison.hashCode());
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		FieldCondition other = (FieldCondition) obj;
		if (comparison != other.comparison) return false;
		if (fieldName == null)
		{
			if (other.fieldName != null) return false;
		}
		else if (!fieldName.equals(other.fieldName)) return false;
		if (value == null)
		{
			if (other.value != null) return false;
		}
		else if (!value.equals(other.value)) return false;
		return true;
	}
	
}
