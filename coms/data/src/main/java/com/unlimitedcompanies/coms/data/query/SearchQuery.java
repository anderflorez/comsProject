package com.unlimitedcompanies.coms.data.query;

import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.data.exceptions.FieldNotInSearchException;
import com.unlimitedcompanies.coms.data.exceptions.IncorrectFieldFormatException;
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

	protected void setSingleResultField(String singleResultField)
	{
		this.singleResultField = singleResultField;
	}

	public void assignSingleResultField(String alias, String field)
	{
		if (verifyField(alias, field, this.getQueryResource()))
		{
			this.singleResultField = alias + "." + field;
		}
		else
		{
			// TODO: Throw an exception or somehow provide an error as the search cannot return this single result
			// TODO: Test this
			System.out.println("ERROR: This field cannot be retrieved as it could not be found within the search resources");
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
		if (this.getConditionGL1() != null || (conditionGL1.getSearch() != null && conditionGL1.getSearch() != this))
		{
			// TODO: Throw an exception - There is already a ConditionGL1 in the search or the conditionGL1 already belongs to another search
		}
		
		this.conditionGL1 = conditionGL1;
		conditionGL1.setSearch(this);
	}
	
	private ConditionGL1 addConditionGroup()
	{
		if (this.getConditionGL1() != null)
		{
			// TODO: Throw an exception - There is already a ConditionGL1 in the search; only one should be created
		}
		
		ConditionGL1 conditionGL1 = new ConditionGL1();
		this.setConditionGL1(conditionGL1);
		
		return conditionGL1;
	}
	
	// TODO: Future upgrades: There could be methods here to verify if a condition field does belong to the search

	public Path leftJoinFetch(ResourceField field, String alias, Resource relationResource)
	{
		Path resultPath = this.queryResource.leftJoinFetch(field, alias, relationResource);
		return resultPath;
	}
	
	// The logic operator should be set from the "and" and "or" methods in the condition group classes
	// This method expects the condition the be the first and only; therefore, no logical operator will be used
	public ConditionGL1 where(String field, COperator condOperator, String value, char valueType) 
			throws FieldNotInSearchException, IncorrectFieldFormatException
	{
		ConditionGL1 conditionGL1 = this.addConditionGroup();
		
		// TODO: Check the next line does create a ConditionL1 and add it to the corresponding ConditionGL1
		conditionGL1.addCondition(field, condOperator, value, valueType);

		return conditionGL1;
	}
	
	public String generateFullQuery()
	{
		// TODO: Create some checking mechanism to make sure there are no repeated aliases in the same search query
		// TODO: Update this method to include the where clause of the query
		
		StringBuilder sb = new StringBuilder();
		if (this.getSingleResultField() == null)
		{
			sb.append("select root ");
		}
		else 
		{
			sb.append("select " + this.getSingleResultField() + " ");
		}
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
	
	public static boolean verifyField(String alias, String field, Path path)
	{
		// Verifies if a field belongs to a search based on a path and an alias
		
		if (path.getAlias().equals(alias))
		{
			Set<ResourceField> rfs = path.getResource().getResourceFields();
			for (ResourceField next : rfs)
			{
				if (next.getResourceFieldName().equals(field))
				{
					return true;
				}
			}
			return false;
		}
		else
		{
			if (!path.getBranches().isEmpty())
			{
				for (Path nextPath : path.getBranches())
				{
					if (verifyField(alias, field, nextPath)) return true;
				}
				return false;
			}
			else
			{
				return false;
			}
		}
	}
}
