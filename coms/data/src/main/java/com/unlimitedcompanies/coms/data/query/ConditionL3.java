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
	// Expects t for text or v for view
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

	public ConditionL3(ConditionGL3 containerGroup, String field, COperator cOperator, String value)
	{
		int i = field.indexOf('.');
		if (i > 0)
		{
			Path path = containerGroup.getParentGroup().getParentGroup().getSearch().getQueryResource();
			if (SearchQuery.verifyField(field.substring(0, i), field.substring(i + 1), path))
			{
				this.conditionL3Id = UUID.randomUUID().toString();
				this.containerGroup = containerGroup;
				this.field = field;
				this.cOperator = cOperator.symbolOperator();
				this.value = value;
				this.valueType = 't';
				this.sqValue = null;
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
	
	public ConditionL3(ConditionGL3 containerGroup, String field, COperator cOperator, SearchQuery value)
	{
		int i = field.indexOf('.');
		if (i > 0)
		{
			Path path = containerGroup.getParentGroup().getParentGroup().getSearch().getQueryResource();
			if (SearchQuery.verifyField(field.substring(0, i), field.substring(i + 1), path))
			{
				this.conditionL3Id = UUID.randomUUID().toString();
				this.containerGroup = containerGroup;
				this.field = field;
				this.cOperator = cOperator.symbolOperator();
				this.value = null;
				this.valueType = 's';
				this.sqValue = value;
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
	
	// TODO: All ConditionL1, ConditionL2, and ConditionL3 have this same method; create a superclass that has the method instead of having a copy
	protected String conditionalQuery()
	{
		if (this.valueType == 't')
		{
			return this.field + " " + this.getOperator().symbolOperator() + " " + this.value;			
		}
		else if (this.valueType == 's')
		{
			return this.field + " " + this.getOperator().symbolOperator() + " " + this.getSqValue().generateFullQuery() + ")";
		}
		else
		{
			return null;
		}
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
