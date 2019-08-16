package com.unlimitedcompanies.coms.domain.abac;

import java.util.List;
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
			if (this.userAttribute.equals(UserAttribute.ROLES.toString()))
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
			else if (this.userAttribute.equals(UserAttribute.PROJECTS.toString()))
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
			if (this.userAttribute.equals(UserAttribute.ROLES.toString()))
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
			else if (this.userAttribute.equals(UserAttribute.PROJECTS.toString()))
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

}
