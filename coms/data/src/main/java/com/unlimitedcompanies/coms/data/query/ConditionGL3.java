package com.unlimitedcompanies.coms.data.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.unlimitedcompanies.coms.data.exceptions.ConditionMaxLevelException;
import com.unlimitedcompanies.coms.data.exceptions.NoLogicalOperatorException;

@Entity
@Table(name = "conditionGroupL3")
public class ConditionGL3 implements ConditionGroup
{
	@Id
	@Column(name = "conditionGroupL3Id")
	private String group3Id;
	private String lOperator;
	
	@OneToOne
	@JoinColumn(name = "conditionGroupL2_FK")
	private ConditionGL2 parentGroup;

	@OneToMany(mappedBy = "containerGroup", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<ConditionL3> conditions;
	
	public ConditionGL3()
	{
		this.group3Id = UUID.randomUUID().toString();
		this.conditions = new ArrayList<>();
	}

	public String getGroup3Id()
	{
		return group3Id;
	}

	private void setGroup3Id(String group3Id)
	{
		this.group3Id = group3Id;
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
		if (this.lOperator.equals("AND"))
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

	protected ConditionGL2 getParentGroup()
	{
		return parentGroup;
	}

	protected void setParentGroup(ConditionGL2 parentGroup)
	{
		if (parentGroup.getConditionGroup().equals(this) && this.getParentGroup() == null)
		{
			this.parentGroup = parentGroup;
		}
		else
		{
			// Throw an exception as the condition Group was not added from the parent or it
			// already belongs to another search
		}
	}

	public List<ConditionL3> getConditions()
	{
		return Collections.unmodifiableList(conditions);
	}

	private void setConditions(List<ConditionL3> conditions)
	{
		this.conditions = conditions;
	}
	
	protected ConditionGL3 addCondition(String field, COperator condOperator, String value) throws NoLogicalOperatorException
	{
		if (this.getConditions().size() > 0 && this.getOperator() == null)
		{
			throw new NoLogicalOperatorException();
		}
		
		ConditionL3 condition = new ConditionL3(this, field, condOperator, value);
		this.conditions.add(condition);
		if (condition.getContainerGroup() == null)
		{
			condition.setContainerGroup(this);
		}		
		
		return this;
	}
	
	protected ConditionGL3 addCondition(String field, COperator condOperator, SearchQuery value) throws NoLogicalOperatorException
	{
		if (this.getConditions().size() > 0 && this.getOperator() == null)
		{
			throw new NoLogicalOperatorException();
		}
		
		ConditionL3 condition = new ConditionL3(this, field, condOperator, value);
		this.conditions.add(condition);
		if (condition.getContainerGroup() == null)
		{
			condition.setContainerGroup(this);
		}		
		
		return this;
	}
	
	@Override
	public ConditionGroup and(String field, COperator cOperator, String value) 
			throws ConditionMaxLevelException, NoLogicalOperatorException
	{
		// TODO: create a test for all cases in this method
		
		if (this.getlOperator() == null)
		{
			this.setOperator(LOperator.AND);
			this.addCondition(field, cOperator, value);
			return this;
		}
		
		if (this.getOperator().equals(LOperator.AND))
		{
			this.addCondition(field, cOperator, value);
			return this;
		}
		else
		{
			throw new ConditionMaxLevelException();
		}
	}
	
	@Override
	public ConditionGroup and(String field, COperator cOperator, SearchQuery value) 
			throws ConditionMaxLevelException, NoLogicalOperatorException
	{
		// TODO: create a test for all cases in this method
		
		if (this.getlOperator() == null)
		{
			this.setOperator(LOperator.AND);
			this.addCondition(field, cOperator, value);
			return this;
		}
		
		if (this.getOperator().equals(LOperator.AND))
		{
			this.addCondition(field, cOperator, value);
			return this;
		}
		else
		{
			throw new ConditionMaxLevelException();
		}
	}
	
	@Override
	public ConditionGroup or(String field, COperator cOperator, String value) 
			throws ConditionMaxLevelException, NoLogicalOperatorException
	{
		// TODO: create a test for all cases in this method
		
		if (this.getlOperator() == null)
		{
			this.setOperator(LOperator.OR);
			this.addCondition(field, cOperator, value);
			return this;
		}
		
		if (this.getOperator().equals(LOperator.OR))
		{
			this.addCondition(field, cOperator, value);
			return this;
		}
		else
		{
			throw new ConditionMaxLevelException();
		}
	}
	
	@Override
	public ConditionGroup or(String field, COperator cOperator, SearchQuery value) 
			throws ConditionMaxLevelException, NoLogicalOperatorException
	{
		// TODO: create a test for all cases in this method
		
		if (this.getlOperator() == null)
		{
			this.setOperator(LOperator.OR);
			this.addCondition(field, cOperator, value);
			return this;
		}
		
		if (this.getOperator().equals(LOperator.OR))
		{
			this.addCondition(field, cOperator, value);
			return this;
		}
		else
		{
			throw new ConditionMaxLevelException();
		}
	}
	
	protected StringBuilder conditionalGroupQuery()
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.conditions.size(); i++)
		{
			if (i == 0)
			{
				sb.append(this.conditions.get(i).conditionalQuery());
			}
			else
			{
				sb.append(" " + this.getlOperator().toLowerCase() + " " + this.conditions.get(i).conditionalQuery());
			}
		}
		
		return sb;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group3Id == null) ? 0 : group3Id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ConditionGL3 other = (ConditionGL3) obj;
		if (group3Id == null)
		{
			if (other.group3Id != null) return false;
		}
		else if (!group3Id.equals(other.group3Id)) return false;
		return true;
	}
	
}
