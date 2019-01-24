package com.unlimitedcompanies.coms.data.query;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "conditionL2")
public class ConditionL2
{
	@Id
	private String conditionL2Id;
	private String field;
	private String cOperator;
	private String value;
	private char valueType;
	
	@OneToOne
	@JoinColumn(name = "conditionGroupL2_FK")
	private ConditionGL2 group;
	
	@OneToOne
	@JoinColumn(name = "searchId_FK")
	private SearchQuery sqValue;

	public ConditionL2()
	{
		this.conditionL2Id = UUID.randomUUID().toString();
	}

	public ConditionL2(String field, String cOperator, String value, char valueType, ConditionGL2 group)
	{
		this.conditionL2Id = UUID.randomUUID().toString();
		this.field = field;
		this.cOperator = cOperator;
		this.value = value;
		this.valueType = valueType;
		this.group = group;
	}

	public String getConditionL2Id()
	{
		return conditionL2Id;
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

	public ConditionGL2 getGroup()
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
