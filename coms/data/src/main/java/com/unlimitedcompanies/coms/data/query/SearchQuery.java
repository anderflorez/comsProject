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
		if (this.conditionGL1 == null || this.conditionGL1.equals(conditionGL1))
		{
			this.conditionGL1 = conditionGL1;
			// TODO: Create a unit test to make sure the search query equals method works as expected (based on id only)
			if (conditionGL1.getSearch() == null) conditionGL1.setSearch(this);
		}
		else
		{
			// TODO: Throw an exception as the search already has a condition group setup
		}
	}
	
	// TODO: Future upgrades: There could be methods here to verify a condition field does belong to the search

	public Path leftJoinFetch(ResourceField field, String alias, Resource relationResource)
	{
		return this.queryResource.leftJoinFetch(field, alias, relationResource);
	}
	
	// The logic operator should be set from the "and" and "or" methods in the condition group classes
	// This method expects the condition the be the first and only; therefore, no logical operator will be used
	public ConditionGL1 where(String field, COperator condOperator, String value, char valueType)
	{
		ConditionGL1 conditionGL1 = new ConditionGL1();
		// TODO: Test this is being added in both sides of the relationship
		this.setConditionGL1(conditionGL1);
		
		// TODO: Check the next line does create a ConditionL1 and add it to the corresponding ConditionGL1
		// TODO: Make sure the method called on the next line checks this is the first and only condition in the conditionGL1
		conditionGL1.addCondition(field, condOperator, value, valueType);
		
		return conditionGL1;
	}
	
	public String generateFullQuery()
	{
		// TODO: Create some checking mechanism to make sure there are no repeated aliases in the same search query
		// TODO: Update this method to include the where clause of the query		
		StringBuilder sb = new StringBuilder("select root ");
		sb.append(this.queryResource.getFullQuery());
		if (this.getConditionGL1() != null)
		{
			sb.append(this.getConditionGL1().conditionalGroupQuery());
		}
		sb.append(';');
		return sb.toString();
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
