package com.unlimitedcompanies.coms.dao.search;

import java.util.List;
import java.util.Map;

public class Search
{
	private String resource;
	private List<String> selectClause;
	private Map<String, Search> joinClause;
	private ConditionGroup whereClause;

	public Search(String resource)
	{
		this.resource = resource;
	}

	public Search(String resource, List<String> selectClause, Map<String, Search> joinClause)
	{
		this.resource = resource;
		this.selectClause = selectClause;
		this.joinClause = joinClause;
	}

	public String getResource()
	{
		return resource;
	}

	public List<String> getSelectClause()
	{
		return selectClause;
	}

	public Map<String, Search> getJoinClause()
	{
		return joinClause;
	}

	public ConditionGroup getWhereClause()
	{
		return whereClause;
	}

	public void setSelectClause(List<String> selectClause)
	{
		this.selectClause = selectClause;
	}
	
	public String createQuery()
	{
		StringBuilder query = new StringBuilder("select ");
		for (String field : selectClause)
		{
			query.append(this.getResource().toLowerCase() + "." + field + " ");
		}
		
		query.append("from " + this.getResource() + " " + this.getResource().toLowerCase() + " ");
		
		if (joinClause.size() > 0)
		{
			for (String relation : joinClause.keySet())
			{
				query.append("left join fetch " + 
							 this.getResource().toLowerCase() + "." + relation + " " 
							 + joinClause.get(relation).getResource().toLowerCase());
			}
		}
		
		
		
		return query.toString();
	}

}
