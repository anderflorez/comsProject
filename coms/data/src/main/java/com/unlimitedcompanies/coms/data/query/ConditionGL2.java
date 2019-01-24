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
@Table(name = "conditionGroupL2")
public class ConditionGL2
{
	@Id
	@Column(name = "conditionGroupL2Id")
	private String group2Id;
	private String lOperator;

	@OneToOne
	@JoinColumn(name = "conditionGroupL1_FK")
	private ConditionGL1 parentGroup;

	@OneToMany(mappedBy = "group")
	private List<ConditionL2> conditions;

	@OneToOne(mappedBy = "parentGroup")
	private ConditionGL3 conditionGroup;

	public ConditionGL2()
	{
		this.group2Id = UUID.randomUUID().toString();
		this.conditions = new ArrayList<>();
	}

	public ConditionGL2(LOperator lOperator)
	{
		this.group2Id = UUID.randomUUID().toString();
		this.conditions = new ArrayList<>();
		this.lOperator = lOperator.symbolOperator();
	}

	public String getGroup2Id()
	{
		return group2Id;
	}

	private void setGroup2Id(String group2Id)
	{
		this.group2Id = group2Id;
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

	public ConditionGL1 getParentGroup()
	{
		return parentGroup;
	}

	protected void setParentGroup(ConditionGL1 parentGroup)
	{
		if (parentGroup.getConditionGroup().equals(this) && !this.getParentGroup().equals(parentGroup))
		{
			this.parentGroup = parentGroup;
		}
		else
		{
			// Throw an exception as the condition Group was not added from the parent or it
			// already belongs to another search
		}
	}

	private List<ConditionL2> getConditions()
	{
		return conditions;
	}

	private void setConditions(List<ConditionL2> conditions)
	{
		this.conditions = conditions;
	}

	private void addCondition(ConditionL2 condition)
	{
		this.conditions.add(condition);
		if (!condition.getGroup().equals(this))
		{
			condition.setGroup(this);
		}
	}

	private ConditionGL3 getConditionGroup()
	{
		return conditionGroup;
	}

	private ConditionGL3 setConditionGroup(LOperator operator)
	{
		ConditionGL3 conditionGL3 = new ConditionGL3(LOperator operator);
		this.conditionGroup = conditionGL3;
		return conditionGL3;
	}
}
