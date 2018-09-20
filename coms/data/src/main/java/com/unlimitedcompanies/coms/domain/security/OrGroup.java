package com.unlimitedcompanies.coms.domain.security;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "orGroup")
public class OrGroup
{
	@Id
	private Integer OrGroupId;
	
	@OneToMany
	@JoinColumn(name = "andGroupId_FK")
	private Set<AndGroup> andGroup = new HashSet<>();	
	
	@OneToMany(mappedBy = "orGroup")
	private Set<OrCondition> orConditions = new HashSet<>();	
	
	public void addAndGroup(AndGroup andGroup)
	{
		this.andGroup.add(andGroup);
	}
	
	public void addOrCondition(OrCondition orCondition)
	{
		if (!this.orConditions.contains(orCondition))
		{
			this.orConditions.add(orCondition);
			orCondition.assignToGroup(this);
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orConditions == null) ? 0 : orConditions.hashCode());
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
		if (orConditions == null)
		{
			if (other.orConditions != null)
				return false;
		} else if (!orConditions.equals(other.orConditions))
			return false;
		return true;
	}
	
}
