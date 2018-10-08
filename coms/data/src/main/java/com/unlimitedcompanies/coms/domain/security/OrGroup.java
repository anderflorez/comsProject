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
import javax.persistence.Table;

@Entity
@Table(name = "orGroup")
public class OrGroup
{
	@Id
	private String orGroupId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "andGroupId_FK")
	private AndGroup containerAndGroup;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "orGroup")
	private List<OrCondition> orConditions = new ArrayList<>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "containerOrGroup")
	private List<AndGroup> andGroups = new ArrayList<>();
	
	public OrGroup()
	{
		this.orGroupId = UUID.randomUUID().toString();
	}
	
	public String getOrGroupId()
	{
		return this.orGroupId;
	}
	
	public List<OrCondition> getConditions() 
	{
		return Collections.unmodifiableList(this.orConditions);
	}
	
	public List<AndGroup> getAndGroups()
	{
		return Collections.unmodifiableList(this.andGroups);
	}
	
	public void addOrCondition(OrCondition orCondition)
	{
		if (!this.orConditions.contains(orCondition))
		{
			this.orConditions.add(orCondition);
		}
	}
	
	public void addOrConditionBidirectional(OrCondition orCondition)
	{
		if (!this.orConditions.contains(orCondition))
		{
			this.orConditions.add(orCondition);
		}
		orCondition.assignToGroup(this);
	}
	
	public void setContainerAndGroup(AndGroup andGroup)
	{
		this.containerAndGroup = andGroup;
	}
	
	public void addAndGroup(AndGroup andGroup)
	{
		this.andGroups.add(andGroup);
	}
	
	public void assignAndGroupList(List<AndGroup> andGroups)
	{
		this.andGroups = andGroups;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((containerAndGroup == null) ? 0 : containerAndGroup.hashCode());
		result = prime * result + ((orConditions == null) ? 0 : orConditions.hashCode());
		result = prime * result + ((orGroupId == null) ? 0 : orGroupId.hashCode());
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
		OrGroup other = (OrGroup) obj;
		if (containerAndGroup == null)
		{
			if (other.containerAndGroup != null)
				return false;
		} else if (!containerAndGroup.equals(other.containerAndGroup))
			return false;
		if (orConditions == null)
		{
			if (other.orConditions != null)
				return false;
		} else if (!orConditions.equals(other.orConditions))
			return false;
		if (orGroupId == null)
		{
			if (other.orGroupId != null)
				return false;
		} else if (!orGroupId.equals(other.orGroupId))
			return false;
		return true;
	}
	
}
