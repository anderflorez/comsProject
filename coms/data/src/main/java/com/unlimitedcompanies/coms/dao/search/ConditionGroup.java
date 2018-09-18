package com.unlimitedcompanies.coms.dao.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConditionGroup
{
	private Method method;
	private Set<SearchCondition> conditions;
	private Set<ConditionGroup> conditionGroupSet;

	public ConditionGroup(Method method)
	{
		this.method = method;
		this.conditions = new HashSet<>();
		this.conditionGroupSet = new HashSet<>();
	}
	
	public Method getMethod()
	{
		return this.method;
	}
	
	public Map<String, Object> getValues()
	{
		Map<String, Object> values = new HashMap<String, Object>();
		for (SearchCondition sc : conditions)
		{
			values.put(sc.getFieldName(), sc.getValue());
		}
		
		for (ConditionGroup cg : this.conditionGroupSet)
		{
			values.putAll(cg.getValues());
		}
		
		return values;
	}
	
	public void setMethod(Method method)
	{
		this.method = method;
	}
	
	public void addCondition(SearchCondition condition)
	{
		this.conditions.add(condition);
	}
	
	public void addCondition(String resource, String field, Object value, Operator operator)
	{
		SearchCondition sc = new SearchCondition(resource, field, operator, value);
		this.conditions.add(sc);
	}
	
	public void addConditionGroupToSet(ConditionGroup conditionGroup)
	{
		this.conditionGroupSet.add(conditionGroup);
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
					group.append(" " + this.method + " ");
					group.append(condition);
				}

				if (this.conditionGroupSet.size() > 0)
				{
					for (ConditionGroup cg : conditionGroupSet)
					{
						if (cg.conditions.size() > 0 || cg.conditionGroupSet.size() > 0)
						{
							group.append(" " + this.method + " ");
							group.append(cg.toString());							
						}
					}
				}
			}
			group.append(")");
			
		}
		else if (this.conditionGroupSet.size() > 0)
		{
			group.append(this.conditionGroupSet.toString());
		}
		
		return group.toString();
	}
}
