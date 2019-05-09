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

import com.unlimitedcompanies.coms.domain.security.User;

@Entity
@Table(name = "attributeConditions")
public class AttributeCondition
{
	@Id
	private String attributeConditionId;
	
	@Column(unique=false, nullable=false)
	private String userAttribute;
	
	@Column(unique=false, nullable=false)
	private String resourceAttribute;
	
	@Column(unique=false, nullable=false)
	private ComparisonOperator comparison;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinColumn(name="conditionGroupId_FK")
	private ConditionGroup parentConditionGroup;
	
	protected AttributeCondition() 
	{
		this.attributeConditionId = UUID.randomUUID().toString();
	}
	
	protected AttributeCondition(ConditionGroup conditionGroup, 
							     UserAttribute userAttribute, 
							     ComparisonOperator comparisonOperator, 
							     ResourceAttribute resourceAttribute)
	{
		this.attributeConditionId = UUID.randomUUID().toString();
		this.userAttribute = userAttribute.toString();
		this.comparison = comparisonOperator;
		this.resourceAttribute = resourceAttribute.toString();
		this.parentConditionGroup = conditionGroup;
		if (!conditionGroup.getAttributeConditions().contains(this))
		{
			conditionGroup.addAttributeCondition(this);
		}
	}

	public String getAttributeConditionId()
	{
		return attributeConditionId;
	}

	public UserAttribute getUserAttribute()
	{
		return UserAttribute.valueOf(this.userAttribute.toUpperCase());
	}

	public ResourceAttribute getResourceAttribute()
	{
		return ResourceAttribute.valueOf(this.resourceAttribute.toUpperCase());
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
		if (!parentConditionGroup.getAttributeConditions().contains(this))
		{
			parentConditionGroup.addAttributeCondition(this);
		}
	}
	
	protected String getReadPolicy(String projectAlias, String userAlias, User user)
	{
		String attributePart = this.getResourceAttribute().getField(projectAlias, userAlias) + 
							   " " + this.getComparison().getOperator() + " ";
		
		List<String> userAttributes = this.getUserAttribute().getUserField(user);
		
		String result = "";
		for (int i = 0; i < userAttributes.size(); i++)
		{
			if (i > 0) result += " OR ";
			result += attributePart + userAttributes.get(i);
		}
		
		return result;
	}
	
	protected boolean getModifyPolicy(ResourceAttribs resourceAttribs, UserAttribs userAttribs)
	{
		if (this.getComparison() == ComparisonOperator.EQUALS)
		{
			if (this.getResourceAttribute() == ResourceAttribute.PROJECT_NAME)
			{
				for (String resourceProject : resourceAttribs.getProjectNames())
				{
					for (String userProject : userAttribs.getProjects())
					{
						if (resourceProject.equals(userProject))
						{
							return true;
						}
					}
				}
				return false;
			}
			else if (this.getResourceAttribute() == ResourceAttribute.P_MANAGERS)
			{
				for (String pm : resourceAttribs.getProjectManagers())
				{
					if (pm.equals(userAttribs.getUsername()))
					{
						return true;
					}
				}
				return false;
			}
			else if (this.getResourceAttribute() == ResourceAttribute.P_SUPERINTENDENTS)
			{
				for (String superintendent : resourceAttribs.getProjectSuperintendents())
				{
					if (superintendent.equals(userAttribs.getUsername()))
					{
						return true;
					}
				}
				return false;
			}
			else if (this.getResourceAttribute() == ResourceAttribute.P_FOREMEN)
			{
				for (String foreman : resourceAttribs.getProjectForemen())
				{
					if (foreman.equals(userAttribs.getUsername()))
					{
						return true;
					}
				}
				return false;
			}
			else if (this.getResourceAttribute() == ResourceAttribute.USERNAME)
			{
				if (resourceAttribs.getUsername().equals(userAttribs.getUsername()))
				{
					return true;
				}
				return false;
			}
			else if (this.getResourceAttribute() == ResourceAttribute.ROLE)
			{
				for (String resourceRole : resourceAttribs.getRoles())
				{
					for (String userRole : userAttribs.getRoles())
					{
						if (resourceRole.equals(userRole))
						{
							return true;
						}
					}
				}
				return false;
			}
			else
			{
				return false;
			}
		}
		else // If comparison is not equals
		{
			if (this.getResourceAttribute() == ResourceAttribute.PROJECT_NAME)
			{
				for (String resourceProject : resourceAttribs.getProjectNames())
				{
					for (String userProject : userAttribs.getProjects())
					{
						if (resourceProject.equals(userProject))
						{
							return false;
						}
					}
				}
				return true;
			}
			else if (this.getResourceAttribute() == ResourceAttribute.P_MANAGERS)
			{
				for (String pm : resourceAttribs.getProjectManagers())
				{
					if (pm.equals(userAttribs.getUsername()))
					{
						return false;
					}
				}
				return true;
			}
			else if (this.getResourceAttribute() == ResourceAttribute.P_SUPERINTENDENTS)
			{
				for (String superintendent : resourceAttribs.getProjectSuperintendents())
				{
					if (superintendent.equals(userAttribs.getUsername()))
					{
						return false;
					}
				}
				return true;
			}
			else if (this.getResourceAttribute() == ResourceAttribute.P_FOREMEN)
			{
				for (String foreman : resourceAttribs.getProjectForemen())
				{
					if (foreman.equals(userAttribs.getUsername()))
					{
						return false;
					}
				}
				return true;
			}
			else if (this.getResourceAttribute() == ResourceAttribute.USERNAME)
			{
				if (resourceAttribs.getUsername().equals(userAttribs.getUsername()))
				{
					return false;
				}
				return true;
			}
			else if (this.getResourceAttribute() == ResourceAttribute.ROLE)
			{
				for (String resourceRole : resourceAttribs.getRoles())
				{
					for (String userRole : userAttribs.getRoles())
					{
						if (resourceRole.equals(userRole))
						{
							return false;
						}
					}
				}
				return true;
			}
			else
			{
				return false;
			}
		}
	}

}
