package com.unlimitedcompanies.coms.data.query;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "conditionL1")
public class ConditionL1
{
	@Id
	private String conditionL1Id;
	private String field;
	private String cOperator;
	private String value;
	private char valueType;
	
	@ManyToOne
	@JoinColumn(name = "conditionGroupL1_FK")
	private ConditionGL1 group;
	
	@OneToOne
	@JoinColumn(name = "searchId_FK")
	private SearchQuery sqValue;

	public ConditionL1() 
	{
		this.conditionL1Id = UUID.randomUUID().toString();
	}

	public ConditionL1(String field, COperator cOperator, String value, char valueType)
	{
		this.conditionL1Id = UUID.randomUUID().toString();
		this.field = field;
		this.cOperator = cOperator.symbolOperator();
		this.value = value;
		this.valueType = valueType;
	}

	protected String getConditionL1Id()
	{
		return conditionL1Id;
	}

	private void setConditionL1Id(String conditionL1Id)
	{
		this.conditionL1Id = conditionL1Id;
	}

	protected String getField()
	{
		return field;
	}

	private void setField(String field)
	{
		this.field = field;
	}

	private String getcOperator()
	{
		return cOperator;
	}
	
	public COperator getOperator()
	{
		if (this.cOperator.equals("="))
		{
			return COperator.EQUALS;
		}
		else if(this.cOperator.equals("!="))
		{
			return COperator.NOT_EQUAL;
		}
		else if(this.cOperator.equals(">"))
		{
			return COperator.GRATER_THAN;
		}
		else if(this.cOperator.equals("<"))
		{
			return COperator.LESS_THAN;
		}
		else
		{
			return null;
		}
	}

	private void setcOperator(String cOperator)
	{
		this.cOperator = cOperator;
	}

	protected String getValue()
	{
		return value;
	}

	private void setValue(String value)
	{
		this.value = value;
	}

	protected char getValueType()
	{
		return valueType;
	}

	private void setValueType(char valueType)
	{
		this.valueType = valueType;
	}

	protected ConditionGL1 getGroup()
	{
		return group;
	}

	protected void setGroup(ConditionGL1 group)
	{
		this.group = group;
		if (!group.getConditions().contains(this))
		{
			if (group.getOperator().toString().equals("AND"))
			{
				group.and(this);
			}
			else if (group.getOperator().toString().equals("OR"))
			{
				group.or(this);
			}
		}
	}

	protected SearchQuery getSqValue()
	{
		return sqValue;
	}

	protected void setSqValue(SearchQuery sqValue)
	{
		this.sqValue = sqValue;
	}
}
