package com.unlimitedcompanies.coms.domain.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "andGroup")
public class AndGroup
{
	@Id
	private String andGroupId;

	@OneToOne(mappedBy = "viewCondtitions")
	private ResourcePermissions resourceViewPermission;
	
	@OneToOne(mappedBy = "editConditions")
	private ResourcePermissions resourceEditPermission;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orGroupId_FK")
	private OrGroup containerOrGroup;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "andGroup")
	private List<AndCondition> andConditions = new ArrayList<>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "containerAndGroup")
	private List<OrGroup> orGroups = new ArrayList<>();

	public AndGroup()
	{
		this.andGroupId = UUID.randomUUID().toString();
	}

	public String getAndGroupId()
	{
		return this.andGroupId;
	}
	
	public OrGroup getContainerOrGroup()
	{
		return this.containerOrGroup;
	}
	
	public List<OrGroup> getOrGroups()
	{
		return Collections.unmodifiableList(this.orGroups);
	}

	public List<AndCondition> getConditions()
	{
		return Collections.unmodifiableList(this.andConditions);
	}

	public void assignToViewPermission(ResourcePermissions resourcePermission)
	{
		if (!this.resourceViewPermission.equals(resourcePermission))
		{
			this.resourceViewPermission = resourcePermission;
			resourcePermission.assignViewConditionGroup(this);
		}
	}
	
	// TODO: Add method to assignToEditPermission

	public void addAndCondition(AndCondition andCondition)
	{
		if (!this.andConditions.contains(andCondition))
		{
			this.andConditions.add(andCondition);
		}
	}
	
	public void addAndConditionBidirectional(AndCondition andCondition)
	{
		if (!this.andConditions.contains(andCondition))
		{
			this.andConditions.add(andCondition);
		}
		andCondition.assignToGroup(this);
	}
	
	public void setContainerOrGroup(OrGroup orGroup)
	{
		this.containerOrGroup = orGroup;
	}
	
	public void addOrGroup(OrGroup orGroup)
	{
		this.orGroups.add(orGroup);
	}
	
	public void assignOrGroupList(List<OrGroup> orGroups)
	{
		this.orGroups = orGroups;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((andConditions == null) ? 0 : andConditions.hashCode());
		result = prime * result + ((andGroupId == null) ? 0 : andGroupId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AndGroup other = (AndGroup) obj;
		if (andConditions == null)
		{
			if (other.andConditions != null)
				return false;
		} else if (!andConditions.equals(other.andConditions))
			return false;
		if (andGroupId == null)
		{
			if (other.andGroupId != null)
				return false;
		} else if (!andGroupId.equals(other.andGroupId))
			return false;
		return true;
	}
	
}
