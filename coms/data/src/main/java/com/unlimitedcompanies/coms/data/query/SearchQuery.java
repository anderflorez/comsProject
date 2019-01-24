package com.unlimitedcompanies.coms.data.query;

import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.ResourceField;

@Entity
@Table(name = "searchQuery")
public class SearchQuery
{
	@Id
	private String searchQueryId;
	private String searchName;
	
	// TODO: complete the getters and setters for this field
	private String singleResultField;
	
	@OneToOne
	@JoinColumn(name = "path_FK")
	private Path queryResource;
	
	@OneToOne(mappedBy = "search")
	private ConditionGL1 conditionGL1;

	public SearchQuery() 
	{
		this.searchQueryId = UUID.randomUUID().toString();
	}
	
	public SearchQuery(Resource resource)
	{
		this.searchQueryId = UUID.randomUUID().toString();
		this.queryResource = new Path(resource, "root");
	}

	public SearchQuery(String searchName, Resource resource)
	{
		this.searchQueryId = UUID.randomUUID().toString();
		this.setSearchName(searchName);
		this.queryResource = new Path(resource, "root");
	}

	public String getSearchQueryId()
	{
		return searchQueryId;
	}

	protected void setSearchQueryId(String searchId)
	{
		this.searchQueryId = searchId;
	}
	
	public String getSearchName()
	{
		return searchName;
	}

	protected void setSearchName(String searchName)
	{
		this.searchName = searchName;
	}

	public String getSingleResultField()
	{
		return singleResultField;
	}

	public void setSingleResultField(String singleResultField)
	{		
		Predicate<String> fieldCheck = testField -> {
			Set<ResourceField> parentFields = this.getQueryResource().getResource().getResourceFields();
			boolean result = false;
			for(ResourceField next : parentFields)
			{
				if (next.getResourceFieldName().equals(testField))
				{
					result = true;
				}
			}
			return result;
		}; 
		
		// TODO: Use a lambda function to verify the singleResultField exists in the root resource fields; then add it
		if (fieldCheck.test(singleResultField))
		{
			this.singleResultField = singleResultField;
		}
		else
		{
			// TODO: Throw an exception or somehow provide an error as the search cannot return this single result
		}
	}	

	public Path getQueryResource()
	{
		return queryResource;
	}

	protected void setQueryResource(Path queryResource)
	{
		this.queryResource = queryResource;
	}
	
	public ConditionGL1 getConditionGL1()
	{
		return conditionGL1;
	}

	private void setConditionGL1(ConditionGL1 conditionGL1)
	{
		if (this.conditionGL1 == null)
		{
			this.conditionGL1 = conditionGL1;
			if (!conditionGL1.getSearch().equals(this)) conditionGL1.setSearch(this);
		}
		else
		{
			// TODO: Throw an exception as the search already has a condition group setup
		}
	}

	public Path leftJoinFetch(ResourceField field, String alias)
	{
		return this.queryResource.leftJoinFetch(field, alias);
	}
	
	public ConditionGL1 where(String field, String cOperator, String value, char valueType)
	{
		ConditionGL1 conditionGL1 = new ConditionGL1(LOperator.AND);
		this.setConditionGL1(conditionGL1);
		
		conditionGL1.and(field, cOperator, value, valueType);
		
		return conditionGL1;
	}
	
	public String generateFullQuery()
	{
		// TODO: Create some checking mechanism to make sure there are no repeated aliases in the same search query
		
		return "select root " + this.queryResource.getFullQuery();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((searchQueryId == null) ? 0 : searchQueryId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SearchQuery other = (SearchQuery) obj;
		if (searchQueryId == null)
		{
			if (other.searchQueryId != null) return false;
		}
		else if (!searchQueryId.equals(other.searchQueryId)) return false;
		return true;
	}
	
}
