package com.unlimitedcompanies.coms.domain.abac;

import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;

@Entity
@Table(name = "entityConditions")
public class EntityCondition
{
	@Id
	private String entityConditionId;
	
	@Column(unique=false, nullable=false)
	private String userAttribute;
	
	@Column(unique=false, nullable=false)
	private String value;
	
	@Column(unique=false, nullable=false)
	private ComparisonOperator comparison;
	
	@ManyToOne//(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinColumn(name="abacPolicyId_FK")
	private AbacPolicy abacPolicy;
	
	protected EntityCondition() 
	{
		this.entityConditionId = UUID.randomUUID().toString();
	}
	
	protected EntityCondition(AbacPolicy abacPolicy, 
							  UserAttribute userAttribute,
							  ComparisonOperator comparison, 
							  String value)
	{
		this.entityConditionId = UUID.randomUUID().toString();
		this.userAttribute = userAttribute.toString();
		this.value = value;
		this.comparison = comparison;
		this.abacPolicy = abacPolicy;
	}

	public String getEntityConditionId()
	{
		return entityConditionId;
	}

	public UserAttribute getUserAttribute()
	{
		return UserAttribute.valueOf(this.userAttribute.toUpperCase());
	}

	public String getValue()
	{
		return value;
	}

	public ComparisonOperator getComparison()
	{
		return comparison;
	}
	
	public AbacPolicy getAbacPolicy()
	{
		return abacPolicy;
	}

	protected void setAbacPolicy(AbacPolicy abacPolicy)
	{
		this.abacPolicy = abacPolicy;
	}

	protected boolean entityConditionAccessGranted(User user)
	{
		// TODO: Test all possible situations in this method after the code to save employees and projects has been completed
		
		if (this.comparison == ComparisonOperator.EQUALS)
		{			
			if (this.userAttribute.equals(UserAttribute.ROLE.toString()))
			{
				if (this.value.equals("ANY"))
				{
					return true;
				}
				
				for (Role next : user.getRoles())
				{
					if (next.getRoleName().equals(this.value))
					{
						return true;
					}
				}

				return false;
			}
			else if (this.userAttribute.equals(UserAttribute.PROJECT.toString()))
			{
				if (this.value.equals("ANY"))
				{
					return true;
				}

				List<String> projectNames = user.getContact().getEmployee().getAssociatedProjectNames();
				for (String project : projectNames)
				{
					if (project.equals(this.value))
					{
						return true;
					}
				}
				
				return false;
			} 
			else // USERNAME being the only option left here
			{
				if (user.getUsername().equals(value)) return true;
				else return false;
			}
		}
		else // In the case it is not EQUALS
		{
			if (this.userAttribute.equals(UserAttribute.ROLE.toString()))
			{
				for (Role next : user.getRoles())
				{
					if (next.getRoleName().equals(value))
					{
						return false;
					}
				}
				return true;
			}
			else if (this.userAttribute.equals(UserAttribute.PROJECT.toString()))
			{
				List<String> projectNames = user.getContact().getEmployee().getAssociatedProjectNames();
				for (String project : projectNames)
				{
					if (project.equals(this.value))
					{
						return false;
					}
				}
				
				return true;
			} 
			else // USERNAME being the only option left here
			{
				if (!user.getUsername().equals(value)) return true;
				else return false;
			}
		}
		
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comparison == null) ? 0 : comparison.hashCode());
		result = prime * result + ((userAttribute == null) ? 0 : userAttribute.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		EntityCondition other = (EntityCondition) obj;
		if (comparison != other.comparison) return false;
		if (userAttribute == null)
		{
			if (other.userAttribute != null) return false;
		}
		else if (!userAttribute.equals(other.userAttribute)) return false;
		if (value == null)
		{
			if (other.value != null) return false;
		}
		else if (!value.equals(other.value)) return false;
		return true;
	}

}
