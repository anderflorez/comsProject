package com.unlimitedcompanies.coms.data.query;

import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.domain.security.ResourceField;

@Entity
@Table(name = "conditionL1")
public class ConditionL1
{
	@Id
	private String conditionL1Id;
	private String field;
	private String cOperator;
	private String value;
	// Expects t for text or v for view
	private char valueType;
	
	@ManyToOne
	@JoinColumn(name = "conditionGroupL1_FK")
	private ConditionGL1 containerGroup;
	
	@OneToOne
	@JoinColumn(name = "searchId_FK")
	private SearchQuery sqValue;

	protected ConditionL1() 
	{
		this.conditionL1Id = UUID.randomUUID().toString();
	}

//	protected ConditionL1(String field, COperator cOperator, String value, char valueType)
//	{
//		this.conditionL1Id = UUID.randomUUID().toString();
//		this.field = field;
//		this.cOperator = cOperator.symbolOperator();
//		this.value = value;
//		this.valueType = valueType;
//	}
	
	protected ConditionL1(ConditionGL1 containerGroup, String field, COperator cOperator, String value, char valueType)
	{
		int i = field.indexOf('.');
		if (i > 0)
		{
			Path path = containerGroup.getSearch().getQueryResource();
			if (SearchQuery.verifyField(field.substring(0, i), field.substring(i + 1), path))
			{
				this.conditionL1Id = UUID.randomUUID().toString();
				this.containerGroup = containerGroup;		
				this.field = field;
				this.cOperator = cOperator.symbolOperator();
				this.value = value;
				this.valueType = valueType;
			}
			else
			{
				// TODO: Throw an exception as the field does not exist in the search
				System.out.println("ERROR: The field does not exist in the current search");
			}			
		}
		else
		{
			// TODO: Throw an exception as the field format is incorrect
			System.out.println("ERROR: The field format entered is incorrect");
		}
	}

	public String getConditionL1Id()
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

	protected ConditionGL1 getContainerGroup()
	{
		return containerGroup;
	}

	protected void setContainerGroup(ConditionGL1 containerGroup)
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

	protected SearchQuery getSqValue()
	{
		return sqValue;
	}

	protected void setSqValue(SearchQuery sqValue)
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
		result = prime * result + ((conditionL1Id == null) ? 0 : conditionL1Id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ConditionL1 other = (ConditionL1) obj;
		if (conditionL1Id == null)
		{
			if (other.conditionL1Id != null) return false;
		}
		else if (!conditionL1Id.equals(other.conditionL1Id)) return false;
		return true;
	}
}
