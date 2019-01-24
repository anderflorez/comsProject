package com.unlimitedcompanies.coms.data.query;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "conditionL3")
public class ConditionL3
{
	@Id
	private String conditionL3Id;
	private String field;
	private String cOperator;
	private String value;
	private char valueType;
	
	@OneToOne
	@JoinColumn(name = "conditionGroupL3_FK")
	private ConditionGL3 group;
	
	@OneToOne
	@JoinColumn(name = "searchId_FK")
	private SearchQuery sqValue;

	public ConditionL3()
	{
		this.conditionL3Id = UUID.randomUUID().toString();
	}

	public ConditionL3(String field, String cOperator, String value, char valueType, ConditionGL3 group)
	{
		this.conditionL3Id = UUID.randomUUID().toString();
		this.field = field;
		this.cOperator = cOperator;
		this.value = value;
		this.valueType = valueType;
		this.group = group;
	}

	public String getConditionL3Id()
	{
		return conditionL3Id;
	}

	public String getField()
	{
		return field;
	}

	public String getcOperator()
	{
		return cOperator;
	}

	public String getValue()
	{
		return value;
	}

	public char getValueType()
	{
		return valueType;
	}

	public ConditionGL3 getGroup()
	{
		return group;
	}
	
	public SearchQuery getSqValue()
	{
		return sqValue;
	}
	
	public void setSqValue(SearchQuery sqValue)
	{
		this.sqValue = sqValue;
	}
	
}
