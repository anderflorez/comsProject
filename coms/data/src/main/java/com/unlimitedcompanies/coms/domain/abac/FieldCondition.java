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
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinColumn(name="abacPolicyId_FK")
	private ABACPolicy abacPolicy;
	
	protected FieldCondition()
	{
		this.fieldConditionId = UUID.randomUUID().toString();
	}
	
	protected FieldCondition(String fieldName, ComparisonOperator comparison, 
							 String value, ABACPolicy abacPolicy)
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
	
	public ABACPolicy getAbacPolicy()
	{
		return abacPolicy;
	}

	protected void setAbacPolicy(ABACPolicy abacPolicy)
	{
		this.abacPolicy = abacPolicy;
	}

	protected String getReadPolicy(String resourceEntityName)
	{
		return resourceEntityName + "." + this.fieldName + " " + this.getComparison().getOperator() + " '" + this.value + "'";
	}
	
}
