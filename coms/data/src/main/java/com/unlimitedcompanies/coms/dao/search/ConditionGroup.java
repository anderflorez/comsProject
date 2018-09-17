package com.unlimitedcompanies.coms.dao.search;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ConditionGroup
{
	private Method method;
	private Set<SearchCondition> conditions;
	private ConditionGroup conditionGroup;

	public ConditionGroup(Method method)
	{
		this.method = method;
		this.conditions = new HashSet<>();
		this.conditionGroup = null;
	}

	public String getMethod()
	{
		return method.toString();
	}

	public Set<SearchCondition> getConditions()
	{
		return Collections.unmodifiableSet(conditions);
	}

	public ConditionGroup getConditionGroup()
	{
		return conditionGroup;
	}

	public void setMethod(Method method)
	{
		this.method = method;
	}
	
	public void addCondition(SearchCondition condition)
	{
		this.conditions.add(condition);
	}
	
	public void addConditionGroup(Method method)
	{
		this.conditionGroup = new ConditionGroup(method);
	}
	
	@Override
	public String toString()
	{
		StringBuilder group = new StringBuilder();
		if (conditions.size() > 0)
		{
			group.append("(");
			for (SearchCondition condition : conditions)
			{
				if (group.charAt(group.length() - 1) == '(')
				{
					group.append(condition);
				}
				else 
				{
					group.append(this.getMethod());
					group.append(condition);
				}
			}
			group.append(")");
			
			if (this.conditionGroup != null)
			{
				group.append(" " + this.getMethod() + " ");
				group.append(this.conditionGroup.toString());
			}
		}
		else if (this.conditionGroup != null)
		{
			group.append(this.conditionGroup.toString());
		}
		
		return group.toString();
	}
}
