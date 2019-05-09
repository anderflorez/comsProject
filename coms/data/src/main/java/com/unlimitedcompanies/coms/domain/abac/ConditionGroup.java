package com.unlimitedcompanies.coms.domain.abac;

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
@Table(name = "conditionGroups")
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
		if (this.getAbacPolicy().getPolicyType() == PolicyType.READ)
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
					return;
				}
			}
			// TODO: throw an exception as the fieldName is not part of the resource
			System.out.println("ERROR: The referenced field does not belong to the intended resource");			
		}
		else
		{
			// TODO: Throw an exception as the Field Conditions are to be used only for read policies
		}
		
	}
	
	public void addFieldConditions(String fieldName, ComparisonOperator comparison, String value)
	{
		if (this.getAbacPolicy().getPolicyType() == PolicyType.READ)
		{
			Set<ResourceField> resourceFields = this.getAbacPolicy().getResource().getResourceFields();
			for (ResourceField next : resourceFields)
			{
				if (!next.getAssociation() && next.getResourceFieldName().equals(fieldName))
				{
					new FieldCondition(fieldName, comparison, value, this);
					return;
				}
			}
			// TODO: throw an exception as the fieldName is not part of the resource
			System.out.println("ERROR: The referenced field does not belong to the intended resource");			
		}
		else
		{
			// TODO: Throw an exception as the Field Conditions are to be used only for read policies
		}
		
	}
	
	
	/*
	 * 	GROUP CONDITIONS ONLY
	 * =======================
	 */

	private boolean isEntityAccessGranted(User user)
	{
		if (this.entityConditions.isEmpty())
		{
			if (!this.attributeConditions.isEmpty() || !this.fieldConditions.isEmpty())
			{
				return true;				
			}
			else
			{
				return false;
			}
		}
		
		if (this.logicOperator.equals("AND"))
		{
			for (EntityCondition next : this.entityConditions)
			{
				if (!next.entityConditionAccessGranted(user)) return false;
			}
			return true;
		}
		else // if logicOperator equals OR
		{
			for (EntityCondition next : this.entityConditions)
			{
				if (next.entityConditionAccessGranted(user)) return true;
			}
			return false;
		}
	}

	private String readAttributePolicies(String projectAlias, String userAlias, User user)
	{
		String policy = "";
		for (int i = 0; i < this.attributeConditions.size(); i++)
		{
			if (i > 0) policy += " " + this.logicOperator + " ";
			policy += this.attributeConditions.get(i).getReadPolicy(projectAlias, userAlias, user);
		}
		return policy;
	}
	
	private String readFieldPolicies(String resourceAlias)
	{
		String policy = "";
		for (int i = 0; i < this.fieldConditions.size(); i++)
		{
			if (i > 0) policy += " " + this.logicOperator + " ";
			policy += this.fieldConditions.get(i).getReadPolicy(resourceAlias);
		}
		return policy;
	}
	
	private String readAttributeAndFieldPolicies(String resourceAlias, String projectAlias, String userAlias, User user)
	{
		String attribPolicy = this.readAttributePolicies(projectAlias, userAlias, user);
		String fieldPolicy = this.readFieldPolicies(resourceAlias);
		
		if ((attribPolicy == null || attribPolicy.isEmpty()) && (fieldPolicy == null || fieldPolicy.isEmpty()))
		{
			return null;
		}
		else if (attribPolicy == null || attribPolicy.isEmpty() || fieldPolicy == null || fieldPolicy.isEmpty())
		{
			return "(" + attribPolicy + fieldPolicy + ")";
		}
		else 
		{
			return "(" + attribPolicy + " " + this.logicOperator + " " + fieldPolicy + ")";
		}		
	}
	
	private ResourceReadPolicy readGroupPolicy(String resourceAlias, String projectAlias, String userAlias, User user)
	{
		ResourceReadPolicy resourceReadPolicy = new ResourceReadPolicy();
		if (this.isEntityAccessGranted(user))
		{
			resourceReadPolicy.setReadGranted(true);
			resourceReadPolicy.setReadConditions(this.readAttributeAndFieldPolicies(resourceAlias, projectAlias, userAlias, user));
		}
		
		return resourceReadPolicy;
	}
	
	
	/*
	 * CONDITION GROUP RETURNED POLICIES INCLUDING SUBGROUPS
	 * =======================================================
	 */
	
	protected ResourceReadPolicy getReadAccessPolicy(String resourceAlias, String projectAlias, String userAlias, User user)
	{
		if (this.getLogicOperator() == LogicOperator.AND)
		{
			ResourceReadPolicy readPolicy = new ResourceReadPolicy();

			if (this.isEntityAccessGranted(user))
			{
				readPolicy.setReadGranted(true);
				String conditions = this.readAttributeAndFieldPolicies(resourceAlias, projectAlias, userAlias, user);
				
				for (ConditionGroup group : this.getConditionGroups())
				{
					if (group.isEntityAccessGranted(user))
					{
						String groupConditions = group.readAttributeAndFieldPolicies(resourceAlias, projectAlias, userAlias, user);
						if (conditions.isEmpty())
						{
							conditions = groupConditions;
						}
						else if (!groupConditions.isEmpty())
						{
							conditions = conditions + " " + this.logicOperator + " " + groupConditions;
						}
					}
					else // If subgroup entity access is not granted
					{
						readPolicy.setReadGranted(false);
						readPolicy.setReadConditions(null);
						return readPolicy;
					}
				}
				
				readPolicy.setReadConditions(conditions);
				return readPolicy;
			}
			else // If group entity access is not granted
			{
				readPolicy.setReadGranted(false);
				return readPolicy;				
			}
		}
		else // If Logic Operator is OR 
		{
			ResourceReadPolicy resourceReadPolicy = new ResourceReadPolicy();
			
			if (this.isEntityAccessGranted(user))
			{
				resourceReadPolicy.setReadGranted(true);
				resourceReadPolicy.setReadConditions(this.readAttributeAndFieldPolicies(resourceAlias, projectAlias, userAlias, user));				
			}

			for (ConditionGroup group : this.getConditionGroups())
			{
				ResourceReadPolicy groupReadPolicy = group.readGroupPolicy(resourceAlias, projectAlias, userAlias, user);
				if (groupReadPolicy.isReadGranted())
				{
					resourceReadPolicy.setReadGranted(true);
					
					String conditions = resourceReadPolicy.getReadConditions();
					if (conditions.isEmpty())
					{
						conditions = groupReadPolicy.getReadConditions();
					}
					else if (!groupReadPolicy.getReadConditions().isEmpty())
					{
						conditions += " " + this.logicOperator + " " + groupReadPolicy.getReadConditions();
					}
					
					resourceReadPolicy.setReadConditions(conditions);
				}
			}

			return resourceReadPolicy;
		}
	}
	
	protected boolean getModifyAccessPolicy(ResourceAttribs resourceAttribs, UserAttribs userAttribs, User user)
	{
		if (this.getLogicOperator() == LogicOperator.AND)
		{
			if (this.isEntityAccessGranted(user))
			{
				for (AttributeCondition attributeCond : this.attributeConditions)
				{
					if (!attributeCond.getModifyPolicy(resourceAttribs, userAttribs))
					{
						return false;					
					}
				}
				
				for (ConditionGroup subGroup : this.getConditionGroups())
				{
					if (!subGroup.getModifyAccessPolicy(resourceAttribs, userAttribs, user))
					{
						return false;
					}
				}
				
				return true;
			}
			
			return false;
			
		}
		else  // if logicOperator is OR 
		{
			if (this.isEntityAccessGranted(user))
			{
				for (AttributeCondition attributeCond : this.attributeConditions)
				{
					if (attributeCond.getModifyPolicy(resourceAttribs, userAttribs))
					{
						return true;					
					}
				}
				
				for (ConditionGroup subGroup : this.getConditionGroups())
				{
					if (subGroup.getModifyAccessPolicy(resourceAttribs, userAttribs, user))
					{
						return true;
					}
				}
			}
			
			return false;
		}
	}
	
	
	
	
	
	
	
	// TODO: Method to be deleted
	protected ResourceReadPolicy getConditionGroupPolicies(String resourceAlias, String projectAlias, String userAlias, User user)
	{

		boolean entityPolicy = this.isEntityAccessGranted(user);
		ResourceReadPolicy subGroupPolicies = ABACPolicy.subGroupReadPolicies(resourceAlias, 
																		projectAlias, 
																		userAlias, 
																		user, 
																		this.conditionGroups, 
																		this.getLogicOperator());
			
		ResourceReadPolicy resourceReadPolicy = new ResourceReadPolicy();
		
		if (this.getLogicOperator() == LogicOperator.AND)
		{
			if (entityPolicy && (subGroupPolicies == null || subGroupPolicies.isReadGranted()))
			{
				resourceReadPolicy.setReadGranted(true);

				String queryPolicyConditions = this.unifyConditionQueries(resourceAlias, projectAlias, userAlias, user, subGroupPolicies);
				
				resourceReadPolicy.setReadConditions(queryPolicyConditions);
				
				return resourceReadPolicy;
			}
			else
			{
				ResourceReadPolicy resourcePolicy = new ResourceReadPolicy();
				resourcePolicy.setReadGranted(false);
				resourcePolicy.setReadConditions(null);
				return resourcePolicy;
			}
		}
		else // When logic operator is OR
		{
			if (entityPolicy || (subGroupPolicies != null && subGroupPolicies.isReadGranted()))
			{
				resourceReadPolicy.setReadGranted(true);

				String queryPolicyConditions = this.unifyConditionQueries(resourceAlias, projectAlias, userAlias, user, subGroupPolicies);
				
				resourceReadPolicy.setReadConditions(queryPolicyConditions);
				
				return resourceReadPolicy;
			}
			else 
			{
				ResourceReadPolicy resourcePolicy = new ResourceReadPolicy();
				resourcePolicy.setReadGranted(false);
				resourcePolicy.setReadConditions(null);
				return resourcePolicy;
			}
		}

	}
	
	// TODO: Method to be deleted
	private String unifyConditionQueries(String resourceAlias, 
										 String projectAlias, 
										 String userAlias, 
										 User user, 
										 ResourceReadPolicy subGroupPolicies)
	{
		String attributePolicyConditions = this.readAttributePolicies(projectAlias, userAlias, user);
		String fieldPolicyConditions = this.readFieldPolicies(resourceAlias);
		
		String queryPolicyConditions = "";
		if (!attributePolicyConditions.isEmpty() && !fieldPolicyConditions.isEmpty())
		{
			if (this.getLogicOperator() == LogicOperator.OR)
			{
				queryPolicyConditions = attributePolicyConditions + " " + 
										this.getLogicOperator() + " " + 
										fieldPolicyConditions;
			}
			else
			{
				queryPolicyConditions = "(" + attributePolicyConditions + ") " + 
										this.getLogicOperator() + 
										" (" + fieldPolicyConditions + ")";
			}
			
		}
		else if (!attributePolicyConditions.isEmpty() && fieldPolicyConditions.isEmpty())
		{
			queryPolicyConditions = attributePolicyConditions;
		}
		else if (attributePolicyConditions.isEmpty() && !fieldPolicyConditions.isEmpty())
		{
			queryPolicyConditions = fieldPolicyConditions;
		}
		
		if (subGroupPolicies != null && subGroupPolicies.isReadGranted())
		{
			queryPolicyConditions += " " + this.getLogicOperator() + " (" + subGroupPolicies.getReadConditions() + ")";
		}
		
		return queryPolicyConditions;
	}
	
}
