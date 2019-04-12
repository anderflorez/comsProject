package com.unlimitedcompanies.coms.data.abac;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;

@Entity
@Table(name = "entityCondition")
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
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinColumn(name="conditionGroupId_FK")
	private ConditionGroup parentConditionGroup;
	
	protected EntityCondition() 
	{
		this.entityConditionId = UUID.randomUUID().toString();
	}
	
	protected EntityCondition(ConditionGroup conditionGroup, 
							  UserAttribute userAttribute,
							  ComparisonOperator comparison, 
							  String value)
	{
		this.entityConditionId = UUID.randomUUID().toString();
		this.userAttribute = userAttribute.toString();
		this.value = value;
		this.comparison = comparison;
		this.parentConditionGroup = conditionGroup;
		if (!conditionGroup.getEntityConditions().contains(this))
		{
			conditionGroup.addEntityCondition(this);
		}
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

	public ConditionGroup getParentConditionGroup()
	{
		return parentConditionGroup;
	}

	protected void setParentConditionGroup(ConditionGroup parentConditionGroup)
	{
		this.parentConditionGroup = parentConditionGroup;
	}
	
	public boolean entityPolicyGrant(User user)
	{		
		if (this.getComparison() == ComparisonOperator.EQUALS)
		{
			
			if (this.userAttribute.equals(UserAttribute.FULL_NAME.toString()))
			{
				// TODO: Write the actual code when the attribute is available
				
				return false;
			}
			else if (this.userAttribute.equals(UserAttribute.PROJECTS.toString()))
			{
				// TODO: Write the actual code when the attribute is available
				
				return false;
			}
			else if (this.userAttribute.equals(UserAttribute.ROLES.toString()))
			{
				for (Role next : user.getRoles())
				{
					if (next.getRoleName().equals(value)) return true;
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
			if (this.userAttribute.equals(UserAttribute.FULL_NAME.toString()))
			{
				// TODO: Write the actual code when the attribute is available
				
				return false;
			}
			else if (this.userAttribute.equals(UserAttribute.PROJECTS.toString()))
			{
				// TODO: Write the actual code when the attribute is available
				
				return false;
			}
			else if (this.userAttribute.equals(UserAttribute.ROLES.toString()))
			{
				for (Role next : user.getRoles())
				{
					if (!next.getRoleName().equals(value)) return true;
				}
				return false;
			}
			else // USERNAME being the only option left here
			{
				if (!user.getUsername().equals(value)) return true;
				else return false;
			}
		}
		
	}
}
