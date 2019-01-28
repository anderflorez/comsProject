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

@Entity
@Table(name = "conditionGroupL2")
public class ConditionGL2 implements ConditionGroup
{
	@Id
	@Column(name = "conditionGroupL2Id")
	private String group2Id;
	private String lOperator;

	@OneToOne
	@JoinColumn(name = "conditionGroupL1_FK")
	private ConditionGL1 parentGroup;

	@OneToMany(mappedBy = "containerGroup")
	private List<ConditionL2> conditions;

	@OneToOne(mappedBy = "parentGroup")
	private ConditionGL3 conditionGroup;

	public ConditionGL2()
	{
		this.group2Id = UUID.randomUUID().toString();
		this.conditions = new ArrayList<>();
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
	
	protected void setOperator(LOperator operator)
	{
		this.lOperator = operator.toString();
	}
	
	protected ConditionGL1 getParentGroup()
	{
		return parentGroup;
	}

	protected void setParentGroup(ConditionGL1 parentGroup)
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

	protected List<ConditionL2> getConditions()
	{
		return Collections.unmodifiableList(conditions);
	}

	private void setConditions(List<ConditionL2> conditions)
	{
		this.conditions = conditions;
	}

	private void addCondition(ConditionL2 condition)
	{
		this.conditions.add(condition);
		if (condition.getContainerGroup() == null)
		{
			condition.setContainerGroup(this);
		}
	}
	
	protected ConditionGL2 addCondition(String field, COperator condOperator, String value, char valueType)
	{
		if (this.conditions.isEmpty())
		{
			ConditionL2 condition = new ConditionL2(field, condOperator, value, valueType);
			// TODO: Make sure the next line adds the condition on both sides of the relationship
			this.addCondition(condition);
		}
		else
		{
			// TODO: Throw an exception as this method should be used to add the first condition only
		}
		return this;
	}
	
	protected ConditionGL3 getConditionGroup()
	{
		return conditionGroup;
	}

	private void setConditionGroup(ConditionGL3 conditionGL3)
	{
		this.conditionGroup = conditionGL3;
		if (conditionGL3.getParentGroup() == null)
		{
			conditionGL3.setParentGroup(this);
		}
	}
	
	private ConditionGL3 addConditionGroupL3()
	{
		ConditionGL3 conditionGL3 = new ConditionGL3();
		this.setConditionGroup(conditionGL3);
		return conditionGL3;
	}
	
	@Override
	public ConditionGroup and(String field, COperator cOperator, String value, char valueType)
	{
		// TODO: create a test for this
		// TODO: check if LOperator needs an equals method
		if (this.getlOperator() == null)
		{
			this.setOperator(LOperator.AND);
		}
		
		if (this.getOperator().equals(LOperator.AND))
		{
			this.addCondition(field, cOperator, value, valueType);
			return this;
		}
		else
		{
			// TODO: The next line should verify at some point that the groupL3 does not exist before creating it
			ConditionGL3 conditionGroupL3 = this.addConditionGroupL3();
			conditionGroupL3.setOperator(LOperator.AND);
			// TODO: Check the next line does create a ConditionL3 and add it to the corresponding ConditionGL3
			conditionGroupL3.addCondition(field, cOperator, value, valueType);
			return conditionGroupL3;
		}
	}
	
	@Override
	public ConditionGroup or(String field, COperator cOperator, String value, char valueType)
	{
		if (this.getlOperator() == null)
		{
			this.setOperator(LOperator.OR);
		}
		
		if (this.getOperator().equals(LOperator.OR))
		{
			this.addCondition(field, cOperator, value, valueType);
			return this;
		}
		else
		{
			// TODO: The next line should verify at some point that the groupL2 does not exist before creating it
			ConditionGL3 conditionGroupL3 = this.addConditionGroupL3();
			conditionGroupL3.setOperator(LOperator.OR);
			// TODO: Check the next line does create a ConditionL2 and add it to the corresponding ConditionGL2
			conditionGroupL3.addCondition(field, cOperator, value, valueType);
			return conditionGroupL3;
		}
	}
	
	protected StringBuilder conditionalGroupQuery()
	{
		StringBuilder sb = new StringBuilder();
		for (ConditionL2 condition : this.conditions)
		{
			sb.append(" " + condition.conditionalQuery());
		}
		if (this.getConditionGroup() != null)
		{
			sb.append(this.getConditionGroup().conditionalGroupQuery());			
		}
		return sb;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group2Id == null) ? 0 : group2Id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ConditionGL2 other = (ConditionGL2) obj;
		if (group2Id == null)
		{
			if (other.group2Id != null) return false;
		}
		else if (!group2Id.equals(other.group2Id)) return false;
		return true;
	}
}
