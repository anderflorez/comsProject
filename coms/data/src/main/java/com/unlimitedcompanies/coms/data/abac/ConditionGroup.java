package com.unlimitedcompanies.coms.data.abac;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.unlimitedcompanies.coms.domain.security.ResourceField;
import com.unlimitedcompanies.coms.domain.security.User;

@Entity
@Table(name = "conditionGroup")
public class ConditionGroup
{
	@Id
	private String conditionGroupId;
	
	@Column(unique=false, nullable=false)
	private String logicOperator;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinColumn(name="abacPolicyId_FK")
	private ABACPolicy abacPolicy;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinColumn(name="parentConditionGroupId_FK")
	private ConditionGroup parentConditionGroup;
	
	@OneToMany(mappedBy="parentConditionGroup", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<ConditionGroup> conditionGroups;
	
	@OneToMany(mappedBy="parentConditionGroup", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<EntityCondition> entityConditions;
	
	@OneToMany(mappedBy="parentConditionGroup", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<AttributeCondition> attributeConditions;
	
	@OneToMany(mappedBy="parentConditionGroup", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<FieldCondition> fieldConditions;
	
	protected ConditionGroup() 
	{
		this.conditionGroupId = UUID.randomUUID().toString();
		this.conditionGroups = new ArrayList<>();
		this.entityConditions = new ArrayList<>();
		this.attributeConditions = new ArrayList<>();
		this.fieldConditions = new ArrayList<>();
	}
	
	protected ConditionGroup(ABACPolicy policy)
	{
		this.conditionGroupId = UUID.randomUUID().toString();
		this.abacPolicy = policy;
		this.parentConditionGroup = null;
		this.logicOperator = "AND";
		this.conditionGroups = new ArrayList<>();
		this.entityConditions = new ArrayList<>();
		this.attributeConditions = new ArrayList<>();
		this.fieldConditions = new ArrayList<>();
	}
	
	protected ConditionGroup(ABACPolicy policy, LogicOperator operator)
	{
		this.conditionGroupId = UUID.randomUUID().toString();
		this.abacPolicy = policy;
		this.parentConditionGroup = null;
		this.logicOperator = operator.toString();
		this.conditionGroups = new ArrayList<>();
		this.entityConditions = new ArrayList<>();
		this.attributeConditions = new ArrayList<>();
		this.fieldConditions = new ArrayList<>();
	}
	
	private ConditionGroup(ConditionGroup parentConditionGroup)
	{
		this.conditionGroupId = UUID.randomUUID().toString();
		this.abacPolicy = null;
		this.parentConditionGroup = parentConditionGroup;
		this.logicOperator = "AND";
		this.conditionGroups = new ArrayList<>();
		this.entityConditions = new ArrayList<>();
		this.attributeConditions = new ArrayList<>();
		this.fieldConditions = new ArrayList<>();
	}
	
	private ConditionGroup(ConditionGroup parentConditionGroup, LogicOperator operator)
	{
		this.conditionGroupId = UUID.randomUUID().toString();
		this.abacPolicy = null;
		this.parentConditionGroup = parentConditionGroup;
		this.logicOperator = operator.toString();
		this.conditionGroups = new ArrayList<>();
		this.entityConditions = new ArrayList<>();
		this.attributeConditions = new ArrayList<>();
		this.fieldConditions = new ArrayList<>();
	}

	public String getConditionGroupId()
	{
		return conditionGroupId;
	}

	public LogicOperator getLogicOperator()
	{
		return LogicOperator.valueOf(this.logicOperator.toUpperCase());
	}

	public void setLogicOperator(LogicOperator logicOperator)
	{
		this.logicOperator = logicOperator.toString();
	}

	public ABACPolicy getAbacPolicy()
	{
		return abacPolicy;
	}

	protected void setAbacPolicy(ABACPolicy abacPolicy)
	{
		this.abacPolicy = abacPolicy;
	}

	public ConditionGroup getParentConditionGroup()
	{
		return parentConditionGroup;
	}

	protected void setParentConditionGroup(ConditionGroup parentConditionGroup)
	{
		this.parentConditionGroup = parentConditionGroup;
		if (!parentConditionGroup.conditionGroups.contains(this))
		{
			parentConditionGroup.conditionGroups.add(this);
		}
	}

	public List<ConditionGroup> getConditionGroups()
	{
		return conditionGroups;
	}

	protected void setConditionGroups(List<ConditionGroup> conditionGroups)
	{
		this.conditionGroups = conditionGroups;
	}

	public ConditionGroup addConditionGroup()
	{
		ConditionGroup conditionGroup = new ConditionGroup(this);
		this.conditionGroups.add(conditionGroup);
		return conditionGroup;
	}
	
	public ConditionGroup addConditionGroup(LogicOperator logicOperator)
	{
		ConditionGroup conditionGroup = new ConditionGroup(this, logicOperator);
		this.conditionGroups.add(conditionGroup);
		return conditionGroup;
	}
	
	public List<EntityCondition> getEntityConditions()
	{
		return entityConditions;
	}
	
	protected void setEntityConditions(List<EntityCondition> entityConditions)
	{
		this.entityConditions = entityConditions;
	}
	
	protected void addEntityCondition(EntityCondition entityCondition)
	{
		this.entityConditions.add(entityCondition);
		if (!entityCondition.getParentConditionGroup().equals(this))
		{
			entityCondition.setParentConditionGroup(this);
		}
	}
	
	public void addEntityCondition(UserAttribute userAttribute, ComparisonOperator comparison, String value)
	{
		EntityCondition entityCondition = new EntityCondition(this, userAttribute, comparison, value);
		if (!this.entityConditions.contains(entityCondition))
		{
			this.entityConditions.add(entityCondition);			
		}
	}

	public List<AttributeCondition> getAttributeConditions()
	{
		return attributeConditions;
	}
	
	protected void addAttributeCondition(AttributeCondition attributeCondition)
	{
		this.attributeConditions.add(attributeCondition);
		if (!attributeCondition.getParentConditionGroup().equals(this))
		{
			attributeCondition.setParentConditionGroup(this);
		}
	}
	
	public void addAttributeCondition(UserAttribute userAttribute, ComparisonOperator comparison, ResourceAttribute resourceAttribute)
	{
		AttributeCondition attributeCondition = new AttributeCondition(this, userAttribute, comparison, resourceAttribute);
		if (!this.attributeConditions.contains(attributeCondition))
		{
			this.attributeConditions.add(attributeCondition);
		}
	}
	
	public List<FieldCondition> getFieldConditions()
	{
		return fieldConditions;
	}

	protected void addFieldConditions(FieldCondition fieldCondition)
	{
		Set<ResourceField> resourceFields = this.getAbacPolicy().getResource().getResourceFields();
		for (ResourceField next : resourceFields)
		{
			if (!next.getAssociation() && next.getResourceFieldName().equals(fieldCondition.getFieldName()))
			{
				this.fieldConditions.add(fieldCondition);
				if (!fieldCondition.getParentConditionGroup().equals(this))
				{
					fieldCondition.setParentConditionGroup(this);
				}				
			}
		}
		// TODO: throw an exception as the fieldName is not part of the resource
		System.out.println("ERROR: The referenced field does not belong to the intended resource");
		
	}
	
	public void addFieldConditions(String fieldName, ComparisonOperator comparison, String value)
	{
		Set<ResourceField> resourceFields = this.getAbacPolicy().getResource().getResourceFields();
		for (ResourceField next : resourceFields)
		{
			if (!next.getAssociation() && next.getResourceFieldName().equals(fieldName))
			{
				FieldCondition fieldCondition = new FieldCondition(fieldName, comparison, value, this);
				if (!this.fieldConditions.contains(fieldCondition))
				{
					this.fieldConditions.add(fieldCondition);
				}				
				return;
			}
		}
		// TODO: throw an exception as the fieldName is not part of the resource
		System.out.println("ERROR: The referenced field does not belong to the intended resource");
		
	}

	public boolean entityPoliciesGrant(User user)
	{
		if (this.logicOperator.equals("AND"))
		{
			for (EntityCondition next : this.entityConditions)
			{
				if (!next.entityPolicyGrant(user)) return false;
			}
			for (ConditionGroup next : this.conditionGroups)
			{
				if (!next.entityPoliciesGrant(user)) return false;
			}
			return true;
		}
		else // if logicOperator equals OR
		{
			for (EntityCondition next : this.entityConditions)
			{
				if (next.entityPolicyGrant(user)) return true;
			}
			for (ConditionGroup next : this.conditionGroups)
			{
				if (next.entityPoliciesGrant(user)) return true;
			}
			return false;
		}
	}
}
