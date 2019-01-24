package com.unlimitedcompanies.coms.data.query;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "conditionGroupL1")
public class ConditionGL1
{
	@Id
	@Column(name = "conditionGroupL1Id")
	private String group1Id;
	private String lOperator;
	
	@OneToOne
	@JoinColumn(name = "searchId_FK")
	private SearchQuery search;
	
	@OneToMany(mappedBy = "group")
	private List<ConditionL1> conditions;
	
	@OneToOne(mappedBy = "parentGroup")
	private ConditionGL2 conditionGroup;

	public ConditionGL1() 
	{
		this.group1Id = UUID.randomUUID().toString();
		this.conditions = new ArrayList<>();
	}

	public ConditionGL1(LOperator lOperator)
	{
		this.group1Id = UUID.randomUUID().toString();
		this.lOperator = lOperator.symbolOperator();
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

	public SearchQuery getSearch()
	{
		return search;
	}
	
	protected void setSearch(SearchQuery search)
	{
		if (search.getConditionGL1().equals(this) && !this.search.equals(search))
		{
			this.search = search;
		}
		else
		{
			// Throw an exception as the condition was not added from the search or it already belongs to another search 
		}
	}

	protected List<ConditionL1> getConditions()
	{
		return conditions;
	}

	protected void setConditions(List<ConditionL1> conditions)
	{
		this.conditions = conditions;
	}
	
	private void addCondition(ConditionL1 condition)
	{
		this.conditions.add(condition);
		if (!condition.getGroup().equals(this))
		{
			condition.setGroup(this);
		}
	}
	
	protected ConditionGL2 getConditionGroup()
	{
		return conditionGroup;
	}

	private ConditionGL2 setConditionGroup(LOperator operator)
	{
		ConditionGL2 conditionGL2 = new ConditionGL2(operator);
		this.conditionGroup = conditionGL2;
		return conditionGroup;
	}

	protected ConditionGL1 and(String field, COperator cOperator, String value, char valueType)
	{
		if (this.getOperator().equals(LOperator.AND))
		{
			ConditionL1 conditionL1 = new ConditionL1(field, cOperator, value, valueType);
			this.addCondition(conditionL1);
		}
		else
		{
			ConditionGL2 cg = this.setConditionGroup(LOperator.AND);
			
			cg.addCondition(conditionL2);
		}
		
		return this;
	}
	
	protected ConditionGL1 and(ConditionL1 condition)
	{
		if (this.getOperator().equals(LOperator.AND))
		{
			this.addCondition(condition);
		}
		return this;
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
