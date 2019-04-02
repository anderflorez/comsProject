package com.unlimitedcompanies.coms.data.query;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.unlimitedcompanies.coms.data.exceptions.NonExistingFieldException;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.ResourceField;

@Entity
@Table(name = "path")
public class Path
{
	@Id
	private String pathId;
	private String alias;
	
	// parentRelation is: parentAlias.fieldName
	private String parentRelation;
	
	@ManyToOne
	@JoinColumn(name = "resource_FK")
	private Resource resource;

	@ManyToOne
	@JoinColumn(name = "parent_FK")
	private Path parent;

	@OneToMany(mappedBy = "parent", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Path> branches;

	public Path()
	{
		this.pathId = UUID.randomUUID().toString();
		this.branches = new ArrayList<>();
	}

	public Path(Resource resource, String alias)
	{
		this.pathId = UUID.randomUUID().toString();
		this.resource = resource;
		this.alias = alias;
		if (alias.equals("root"))
		{
			this.parentRelation = "root";
		}
		this.branches = new ArrayList<>();
	}

	public String getPathId()
	{
		return pathId;
	}

	protected void setPathId(String pathId)
	{
		this.pathId = pathId;
	}
	
	public String getAlias()
	{
		return alias;
	}
	
	public void setAlias(String alias)
	{
		this.alias = alias;
	}

	public String getParentRelation()
	{
		return parentRelation;
	}

	private void setParentRelation(String parentRelation)
	{
		this.parentRelation = parentRelation;
	}

	public Resource getResource()
	{
		return resource;
	}

	protected void setResource(Resource resource)
	{
		this.resource = resource;
	}

	public List<Path> getBranches()
	{
		return branches;
	}

	protected void setBranches(List<Path> branches)
	{
		this.branches = branches;		
	}
	
	public Path leftJoinFetch(ResourceField field, String joinAlias, Resource relationResource) throws NonExistingFieldException
	{
		if (!field.getResource().equals(this.getResource()))
		{
			throw new NonExistingFieldException("The indicated field to join the referenced resource was not found in the parent resource");
		}
		
		Path branchPath = new Path(relationResource, joinAlias);
		branchPath.setParentRelation(this.alias + "." + field.getResourceFieldName());
		branchPath.setParent(this);
		this.branches.add(branchPath);
		
		return branchPath;
	}

	public Path getParent()
	{
		return parent;
	}

	public void setParent(Path parent)
	{
		this.parent = parent;
	}
	
	public String getFullQuery()
	{
		StringBuilder sb = new StringBuilder();
		if (this.parentRelation.equals("root"))
		{
			sb.append("from " + this.resource.getResourceName() + " as root");
		}
		else
		{
			sb.append(" left join fetch " + this.parentRelation + " as " + this.alias);
		}
		
		if (!this.branches.isEmpty())
		{
			for (Path next : this.branches)
			{
				sb.append(next.getFullQuery());
			}
		}
		
		return sb.toString();
	}

	public String getFullSQLQuery()
	{
		StringBuilder sb = new StringBuilder();
		if (this.parentRelation.equals("root"))
		{
			sb.append("FROM " + this.resource.getResourceName() + " AS root");
		}
		else
		{
			sb.append(" ");
		}
		
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pathId == null) ? 0 : pathId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Path other = (Path) obj;
		if (pathId == null)
		{
			if (other.pathId != null) return false;
		}
		else if (!pathId.equals(other.pathId)) return false;
		return true;
	}

	
}