package com.unlimitedcompanies.coms.data.query;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
	
	@ManyToOne
	@JoinColumn(name = "conditionGroupL3_FK")
	private ConditionGL3 containerGroup;
	
	@OneToOne
	@JoinColumn(name = "searchId_FK")
	private SearchQuery sqValue;

	public ConditionL3()
	{
		this.conditionL3Id = UUID.randomUUID().toString();
	}

	public ConditionL3(String field, COperator cOperator, String value, char valueType)
	{
		this.conditionL3Id = UUID.randomUUID().toString();
		this.field = field;
		this.cOperator = cOperator.toString();
		this.value = value;
		this.valueType = valueType;
	}

	public String getConditionL3Id()
	{
		return conditionL3Id;
	}

	private void setConditionL3Id(String conditionL3Id)
	{
		this.conditionL3Id = conditionL3Id;
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

	private void setcOperator(String cOperator)
	{
		this.cOperator = cOperator;
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

	protected ConditionGL3 getContainerGroup()
	{
		return containerGroup;
	}

	protected void setContainerGroup(ConditionGL3 containerGroup)
	{
		if (containerGroup.getConditions().contains(this))
		{
			this.containerGroup = containerGroup;			
		}
		else
		{
			// TODO: Throw a new exception as the Group and Condition are not being updated in both sides of the relationship
		}
	}

	private SearchQuery getSqValue()
	{
		return sqValue;
	}

	private void setSqValue(SearchQuery sqValue)
	{
		this.sqValue = sqValue;
	}
	
	protected String conditionalQuery()
	{
		return this.field + " " + this.getOperator().symbolOperator() + " " + this.value;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conditionL3Id == null) ? 0 : conditionL3Id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ConditionL3 other = (ConditionL3) obj;
		if (conditionL3Id == null)
		{
			if (other.conditionL3Id != null) return false;
		}
		else if (!conditionL3Id.equals(other.conditionL3Id)) return false;
		return true;
	}

}
