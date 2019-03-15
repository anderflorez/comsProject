package com.unlimitedcompanies.coms.data.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.data.exceptions.NonExistingFieldException;
import com.unlimitedcompanies.coms.data.exceptions.ExistingConditionGroupException;
import com.unlimitedcompanies.coms.data.exceptions.IncorrectFieldFormatException;
import com.unlimitedcompanies.coms.data.exceptions.NoLogicalOperatorException;

@Entity
@Table(name = "conditionGroupL1")
public class ConditionGL1 implements ConditionGroup
{
	@Id
	@Column(name = "conditionGroupL1Id")
	private String group1Id;
	private String lOperator;
	
	@OneToOne
	@JoinColumn(name = "searchId_FK")
	private SearchQuery search;
	
	@OneToMany(mappedBy = "containerGroup")
	private List<ConditionL1> conditions;
	
	@OneToOne(mappedBy = "parentGroup")
	private ConditionGL2 conditionGroup;

	public ConditionGL1() 
	{
		this.group1Id = UUID.randomUUID().toString();
		this.conditions = new ArrayList<>();
	}

	public String getGroup1Id()
	{
		return group1Id;
	}

	private void setGroup1Id(String group1Id)
	{
		this.group1Id = group1Id;
	}

	private String getlOperator()
	{
		return lOperator;
	}
	
	private void setlOperator(String lOperator)
	{
		this.lOperator = lOperator;
	}
	
	protected LOperator getOperator()
	{
		if (this.lOperator == null)
		{
			return null;
		}
		else if (this.lOperator.equals("AND"))
		{
			return LOperator.AND;
		}
		else if (this.lOperator.equals("OR"))
		{
			return LOperator.OR;
		}
		else
		{
			return null;
		}
	}
	
	protected void setOperator(LOperator operator)
	{
		this.lOperator = operator.toString();
	}

	protected SearchQuery getSearch()
	{
		return search;
	}
	
	protected void setSearch(SearchQuery search)
	{
		if (search.getConditionGL1().equals(this) && this.search == null)
		{
			this.search = search;
		}
	}

	public List<ConditionL1> getConditions()
	{
		return Collections.unmodifiableList(conditions);
	}

	private void setConditions(List<ConditionL1> conditions)
	{
		this.conditions.clear();
		this.conditions = conditions;
	}
	
	protected ConditionGL1 addCondition(String field, COperator condOperator, String value) 
			throws NonExistingFieldException, IncorrectFieldFormatException, NoLogicalOperatorException
	{
		// Make sure the ConditionGL1 has no other conditions without a Logical Operator
		if (this.getConditions().size() > 0 && this.getOperator() == null)
		{
			throw new NoLogicalOperatorException();
		}
		
		ConditionL1 condition = new ConditionL1(this, field, condOperator, value);
		
		this.conditions.add(condition);
		if (condition.getContainerGroup() == null)
		{
			condition.setContainerGroup(this);
		}

		return this;
	}
	
	protected ConditionGL1 addCondition(String field, COperator condOperator, SearchQuery value) 
			throws NonExistingFieldException, IncorrectFieldFormatException, NoLogicalOperatorException
	{
		// Make sure the ConditionGL1 has no other conditions without a Logical Operator
		if (this.getConditions().size() > 0 && this.getOperator() == null)
		{
			throw new NoLogicalOperatorException();
		}
		
		ConditionL1 condition = new ConditionL1(this, field, condOperator, value);
		
		this.conditions.add(condition);
		if (condition.getContainerGroup() == null)
		{
			condition.setContainerGroup(this);
		}

		return this;
	}
	
	public ConditionGL2 getConditionGroup()
	{
		return conditionGroup;
	}

	private void setConditionGroup(ConditionGL2 conditionGL2)
	{
		this.conditionGroup = conditionGL2;
		if (conditionGL2.getParentGroup() == null)
		{
			conditionGL2.setParentGroup(this);
		}
	}
	
	private ConditionGL2 addConditionGroupL2() throws ExistingConditionGroupException
	{
		// Verify there is no existing ConditionGL2 already
		if (this.getConditionGroup() != null)
		{
			throw new ExistingConditionGroupException("There is already a ConditionGL2 referenced by the ConditionGL1");
		}
		ConditionGL2 conditionGL2 = new ConditionGL2();
		this.setConditionGroup(conditionGL2);
		return conditionGroup;
	}

	@Override
	public ConditionGroup and(String field, COperator cOperator, String value) 
			throws NonExistingFieldException, IncorrectFieldFormatException, 
				   NoLogicalOperatorException, ExistingConditionGroupException
	{
		// TODO: create a test for all cases in this method
		
		if (this.getOperator() == null)
		{
			this.setOperator(LOperator.AND);
			this.addCondition(field, cOperator, value);
			return this;
		}
		
		else if (this.getOperator().equals(LOperator.AND))
		{
			this.addCondition(field, cOperator, value);
			return this;
		}
		
		else if (this.getOperator().equals(LOperator.OR))
		{
			if (this.getConditionGroup() == null)
			{
				ConditionGL2 conditionGroupL2 = this.addConditionGroupL2();
				conditionGroupL2.setOperator(LOperator.AND);
				conditionGroupL2.addCondition(field, cOperator, value);
				return conditionGroupL2;
			}
			else 
			{
				ConditionGL2 conditionGroupL2 = this.getConditionGroup();
				// Assuming conditionGroupL2 has operator AND
				conditionGroupL2.addCondition(field, cOperator, value);
				return conditionGroupL2;
			}
		}
		
		else 
		{
			return null;
		}
	}
	
	@Override
	public ConditionGroup and(String field, COperator cOperator, SearchQuery value) 
			throws NonExistingFieldException, IncorrectFieldFormatException, 
				   NoLogicalOperatorException, ExistingConditionGroupException
	{
		// TODO: create a test for all cases in this method
		
		if (this.getOperator() == null)
		{
			this.setOperator(LOperator.AND);
			this.addCondition(field, cOperator, value);
			return this;
		}
		
		else if (this.getOperator().equals(LOperator.AND))
		{
			this.addCondition(field, cOperator, value);
			return this;
		}
		
		else if (this.getOperator().equals(LOperator.OR))
		{
			if (this.getConditionGroup() == null)
			{
				ConditionGL2 conditionGroupL2 = this.addConditionGroupL2();
				conditionGroupL2.setOperator(LOperator.AND);
				conditionGroupL2.addCondition(field, cOperator, value);
				return conditionGroupL2;
			}
			else 
			{
				ConditionGL2 conditionGroupL2 = this.getConditionGroup();
				// Assuming conditionGroupL2 has operator AND
				conditionGroupL2.addCondition(field, cOperator, value);
				return conditionGroupL2;
			}
		}
		
		else 
		{
			return null;
		}
	}

	@Override
	public ConditionGroup or(String field, COperator cOperator, String value) 
			throws NonExistingFieldException, IncorrectFieldFormatException, 
			NoLogicalOperatorException, ExistingConditionGroupException
	{
		// TODO: create a test for all cases in this method
		
		if (this.getOperator() == null)
		{
			this.setOperator(LOperator.OR);
			this.addCondition(field, cOperator, value);
			return this;
		}
		
		else if (this.getOperator().equals(LOperator.OR))
		{
			this.addCondition(field, cOperator, value);
			return this;
		}
		
		else if (this.getOperator().equals(LOperator.AND))
		{
			if (this.getConditionGroup() == null)
			{
				ConditionGL2 conditionGroupL2 = this.addConditionGroupL2();
				conditionGroupL2.setOperator(LOperator.OR);
				conditionGroupL2.addCondition(field, cOperator, value);
				return conditionGroupL2;
			}
			else 
			{
				ConditionGL2 conditionGroupL2 = this.getConditionGroup();
				// Assuming conditionGroupL2 has operator OR
				conditionGroupL2.addCondition(field, cOperator, value);
				return conditionGroupL2;
			}
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public ConditionGroup or(String field, COperator cOperator, SearchQuery value) 
			throws NonExistingFieldException, IncorrectFieldFormatException, 
			NoLogicalOperatorException, ExistingConditionGroupException
	{
		// TODO: create a test for all cases in this method
		
		if (this.getOperator() == null)
		{
			this.setOperator(LOperator.OR);
			this.addCondition(field, cOperator, value);
			return this;
		}
		
		else if (this.getOperator().equals(LOperator.OR))
		{
			this.addCondition(field, cOperator, value);
			return this;
		}
		
		else if (this.getOperator().equals(LOperator.AND))
		{
			if (this.getConditionGroup() == null)
			{
				ConditionGL2 conditionGroupL2 = this.addConditionGroupL2();
				conditionGroupL2.setOperator(LOperator.OR);
				conditionGroupL2.addCondition(field, cOperator, value);
				return conditionGroupL2;
			}
			else 
			{
				ConditionGL2 conditionGroupL2 = this.getConditionGroup();
				// Assuming conditionGroupL2 has operator OR
				conditionGroupL2.addCondition(field, cOperator, value);
				return conditionGroupL2;
			}
		}
		else
		{
			return null;
		}
	}
	
	protected StringBuilder conditionalGroupQuery()
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.conditions.size(); i++)
		{
			if (i == 0)
			{
				sb.append(" where " + this.conditions.get(i).conditionalQuery());
			}
			else
			{
				sb.append(" " + this.getlOperator().toLowerCase() + " " + this.conditions.get(i).conditionalQuery());
			}
		}
		
		if (!this.conditions.isEmpty() && this.getConditionGroup() != null)
		{
			sb.append(" " + this.getlOperator().toLowerCase() + " (" + this.getConditionGroup().conditionalGroupQuery() + ")");
		}

		return sb;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group1Id == null) ? 0 : group1Id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ConditionGL1 other = (ConditionGL1) obj;
		if (group1Id == null)
		{
			if (other.group1Id != null) return false;
		}
		else if (!group1Id.equals(other.group1Id)) return false;
		return true;
	}

}
