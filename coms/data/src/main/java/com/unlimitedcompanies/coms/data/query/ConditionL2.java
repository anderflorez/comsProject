package com.unlimitedcompanies.coms.data.query;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.unlimitedcompanies.coms.data.exceptions.IncorrectFieldFormatException;
import com.unlimitedcompanies.coms.data.exceptions.NonExistingFieldException;

@Entity
@Table(name = "conditionL2")
public class ConditionL2
{
	@Id
	private String conditionL2Id;
	private String field;
	private String cOperator;
	private String value;
	// Expects t for text or v for view
	private char valueType;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinColumn(name = "conditionGroupL2_FK")
	@LazyCollection(LazyCollectionOption.FALSE)
	private ConditionGL2 containerGroup;
	
	@OneToOne
	@JoinColumn(name = "search_FK")
	private SearchQuery sqValue;

	protected ConditionL2()
	{
		this.conditionL2Id = UUID.randomUUID().toString();
	}

	protected ConditionL2(ConditionGL2 containerGroup, String field, COperator cOperator, String value) 
			throws NonExistingFieldException, IncorrectFieldFormatException
	{
		int i = field.indexOf('.');
		if (i > 0)
		{
			Path path = containerGroup.getParentGroup().getSearch().getQueryResource();
			if (SearchQuery.verifyField(field.substring(0, i), field.substring(i + 1), path))
			{
				this.conditionL2Id = UUID.randomUUID().toString();
				this.containerGroup = containerGroup;
				this.field = field;
				this.cOperator = cOperator.symbolOperator();
				this.value = value;
				this.valueType = 't';
				this.sqValue = null;
			}
			else
			{
				throw new NonExistingFieldException("The field referenced in the condition does not exist in the current search");
			}			
		}
		else
		{
			throw new IncorrectFieldFormatException();
		}
	}
	
	protected ConditionL2(ConditionGL2 containerGroup, String field, COperator cOperator, SearchQuery value) 
			throws NonExistingFieldException, IncorrectFieldFormatException
	{
		int i = field.indexOf('.');
		if (i > 0)
		{
			Path path = containerGroup.getParentGroup().getSearch().getQueryResource();
			if (SearchQuery.verifyField(field.substring(0, i), field.substring(i + 1), path))
			{
				this.conditionL2Id = UUID.randomUUID().toString();
				this.containerGroup = containerGroup;
				this.field = field;
				this.cOperator = cOperator.symbolOperator();
				this.value = null;
				this.valueType = 's';
				this.sqValue = value;
			}
			else
			{
				throw new NonExistingFieldException("The field referenced in the condition does not exist in the current search");
			}			
		}
		else
		{
			throw new IncorrectFieldFormatException();
		}
	}

	public String getConditionL2Id()
	{
		return conditionL2Id;
	}

	private void setConditionL2Id(String conditionL2Id)
	{
		this.conditionL2Id = conditionL2Id;
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

	protected ConditionGL2 getContainerGroup()
	{
		return containerGroup;
	}

	protected void setContainerGroup(ConditionGL2 containerGroup)
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
			return this.field + " " + this.getOperator().symbolOperator() + " (" + this.getSqValue().generateFullQuery() + ")";
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
		result = prime * result + ((conditionL2Id == null) ? 0 : conditionL2Id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ConditionL2 other = (ConditionL2) obj;
		if (conditionL2Id == null)
		{
			if (other.conditionL2Id != null) return false;
		}
		else if (!conditionL2Id.equals(other.conditionL2Id)) return false;
		return true;
	}
		
}
