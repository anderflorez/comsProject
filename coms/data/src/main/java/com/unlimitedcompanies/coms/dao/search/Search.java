package com.unlimitedcompanies.coms.dao.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Search
{
	private String resource; // Resource as resource
	private Map<String, String> fields; // resource(alias), fieldName
	private Map<String, String> joins; // resource(alias), fieldName as new-resource
	private ConditionGroup conditionGroup;
	private Map<String, Object> variables;

	public Search(String resource)
	{
		this.resource = resource;
		conditionGroup = null;
		variables = new HashMap<>();
	}
	
	public void addField(String resource, String fieldName)
	{
		if (resource.equals(this.resource) || this.joins.containsKey(resource))
		{
			this.fields.put(resource, fieldName);			
		}
		else
		{
			// TODO: Send back an exception as there is no such resource
		}
	}
	
	public void join(String resource, String relationshipFieldName)
	{
		this.joins.put(resource, relationshipFieldName);
	}
	
	public void where(String resource, String fieldName, Object value, Operator operator)
	{
		SearchCondition sc = new SearchCondition(resource, fieldName, operator, value);
		ConditionGroup cg = new ConditionGroup(Method.AND);
		cg.addCondition(sc);
		this.conditionGroup = cg;
		this.variables.put(":" + fieldName, value);
	}
	
	public void and(SearchCondition...searchConditions)
	{
		ConditionGroup cg = new ConditionGroup(Method.AND);
		for (SearchCondition sc : searchConditions)
		{
			cg.addCondition(sc);
		}
		this.conditionGroup.addConditionGroupToSet(cg);
	}
	
	
	
	
	
	
	
	
	
	public void setConditionGroup(ConditionGroup conditionGroup)
	{
		this.conditionGroup = conditionGroup;
	}
	
	public void addVariable(String valueName, Object value)
	{
		variables.put(valueName, value);
	}

}
