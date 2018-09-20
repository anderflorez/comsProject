package com.unlimitedcompanies.coms.domain.search;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Search
{
	private String resource; // Resource as resource
	private Map<String, Set<String>> fields; // resource(alias), fieldName
	private Map<String, Map<String, String>> joins; // resource(alias), fieldName as new-resource
	private ConditionGroup conditionGroup;
	private Map<String, Object> variables;

	public Search(String resource)
	{
		this.resource = resource;
		this.fields = new HashMap<>();
		fields.put(resource.toLowerCase(), new HashSet<>());
		this.joins = new HashMap<>();
		joins.put(resource.toLowerCase(), new HashMap<>());
		conditionGroup = null;
		variables = new HashMap<>();
	}
	
	public Map<String, Object> getValues()
	{
		return Collections.unmodifiableMap(variables);
	}
	
	public void addField(String resource, String fieldName)
	{
		if (this.fields.containsKey(resource.toLowerCase()))
		{
			this.fields.get(resource.toLowerCase()).add(fieldName);
		}
		else
		{
			// TODO: provide an error as the resource for this field does not exist
		}
	}
	
	public void join(String resource, String relationshipFieldName, String relationshipResource)
	{
		if (this.joins.containsKey(resource.toLowerCase()))
		{
//			this.addField(resource, relationshipFieldName);
			this.joins.get(resource.toLowerCase()).put(relationshipFieldName, relationshipResource);
			this.addResource(relationshipResource.toLowerCase());
		}
		else
		{
			// TODO: provide an error as the resource for this join does not exist
		}
	}
	
	private void addResource(String newResource)
	{
		this.fields.put(newResource, new HashSet<>());
		this.joins.put(newResource, new HashMap<>());
	}
	
	public void where(String resource, String fieldName, Object value, Operator operator)
	{
		SearchCondition sc = new SearchCondition(resource, fieldName, operator, value);
		if (this.conditionGroup == null)
		{
			ConditionGroup cg = new ConditionGroup(Method.AND);
			cg.addCondition(sc);
			this.conditionGroup = cg;			
		}
		else if (this.conditionGroup.getMethod().equals(Method.AND))
		{
			this.conditionGroup.addCondition(sc);
		}
		else 
		{
			ConditionGroup tempCG = this.conditionGroup;
			ConditionGroup cg = new ConditionGroup(Method.AND);
			cg.addCondition(sc);
			this.conditionGroup = cg;
			this.conditionGroup.addConditionGroupToSet(tempCG);
		}
		this.variables.put(fieldName, value);
	}
	
	public void where(ConditionGroup conditionGroup)
	{
		if (this.conditionGroup == null)
		{
			this.conditionGroup = conditionGroup;
		}
		else if (this.conditionGroup.getMethod().equals(Method.AND))
		{
			this.conditionGroup.addConditionGroupToSet(conditionGroup);
		}
		else
		{
			ConditionGroup tempCG = this.conditionGroup;
			ConditionGroup cg = new ConditionGroup(Method.AND);
			cg.addConditionGroupToSet(conditionGroup);
			this.conditionGroup = cg;
			this.conditionGroup.addConditionGroupToSet(tempCG);
		}
		
		this.variables.putAll(conditionGroup.getValues());
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("select " + this.resource.toLowerCase());
//		for (String resource : this.fields.keySet())
//		{
//			for (String field : this.fields.get(resource))
//			{
//				if (sb.toString().endsWith("select "))
//				{
//					sb.append(resource + "." + field);					
//				}
//				else
//				{
//					sb.append(", " + resource + "." + field);
//				}
//			}
//		}
		
		sb.append(" from " + this.resource + " " + this.resource.toLowerCase() + " ");
		
		for (String resource : this.joins.keySet())
		{
			for (String joinField : this.joins.get(resource).keySet())
			{
				sb.append("left join fetch " + resource + "." + joinField + " " + this.joins.get(resource).get(joinField).toLowerCase() + " ");
			}
		}
		
		if (this.conditionGroup != null)
		{
			sb.append("where " + this.conditionGroup);
		}
		
		return sb.toString();
	}
}
