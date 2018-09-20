package com.unlimitedcompanies.coms.domain.security;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "andGroup")
public class AndGroup
{
	@Id
	private Integer andGroupId;
	
	@OneToOne(mappedBy = "andGroup")
	private ResourcePermissions resourcePermission;
	
	@OneToMany
	@JoinColumn(name = "orGroupId_FK")
	private Set<OrGroup> orGroup = new HashSet<>();
	
	@OneToMany(mappedBy = "andGroup")
	private Set<AndCondition> andConditions = new HashSet<>();
		
	public AndGroup() {}

	public void assignToPermission(ResourcePermissions resourcePermission)
	{
		if (!this.resourcePermission.equals(resourcePermission))
		{
			this.resourcePermission = resourcePermission;
			resourcePermission.assignConditionGroup(this);
		}
	}
	
	public void addOrGroup(OrGroup orGroup)
	{
		this.orGroup.add(orGroup);
	}
	
	public void addAndCondition(AndCondition andCondition)
	{
		if (!this.andConditions.contains(andCondition))
		{
			this.andConditions.add(andCondition);
			andCondition.assignToGroup(this);
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((andConditions == null) ? 0 : andConditions.hashCode());
		result = prime * result + ((resourcePermission == null) ? 0 : resourcePermission.hashCode());
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
		if (resourcePermission == null)
		{
			if (other.resourcePermission != null)
				return false;
		} else if (!resourcePermission.equals(other.resourcePermission))
			return false;
		return true;
	}
	
	
}
