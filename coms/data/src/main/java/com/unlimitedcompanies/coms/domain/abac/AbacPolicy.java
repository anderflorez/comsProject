package com.unlimitedcompanies.coms.domain.abac;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.data.exceptions.InvalidPolicyException;
import com.unlimitedcompanies.coms.domain.security.User;

@Entity
@Table(name = "abacPolicies")
public class AbacPolicy
{
	@Id
	@Column(unique=true, nullable=false)
	private String abacPolicyId;
	
	@Column(unique=true, nullable=true)
	private String policyName;
	
	@Column(unique=false, nullable=true)
	private String policyType;
	
	@Column(unique=false, nullable=false)
	private String logicOperator;
	
	@OneToOne(mappedBy="policy", fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
	private CdPolicy cdPolicy;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="resourceId_FK")
	private Resource resource;
	
	@OneToMany(mappedBy = "parentPolicy", cascade = {CascadeType.ALL})
	private Set<AbacPolicy> subPolicies;
	
	@ManyToOne//(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "abacPolicyId_FK")
	private AbacPolicy parentPolicy;
	
	@OneToMany(mappedBy = "abacPolicy", fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
//	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<EntityCondition> entityConditions;
	
	@OneToMany(mappedBy = "abacPolicy", fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
	//@LazyCollection(LazyCollectionOption.FALSE)
	private Set<AttributeCondition> attributeConditions;
	
	@OneToMany(mappedBy = "abacPolicy", fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
	//@LazyCollection(LazyCollectionOption.FALSE)
	private Set<FieldCondition> fieldConditions;
	
	protected AbacPolicy() 
	{
		this.abacPolicyId = UUID.randomUUID().toString();
		this.subPolicies = new HashSet<>();
		this.entityConditions = new HashSet<>();
		this.attributeConditions = new HashSet<>();
		this.fieldConditions = new HashSet<>();
	}
	
	public AbacPolicy(String name, PolicyType policyType, Resource resource) 
			throws InvalidPolicyException 
	{
		this.abacPolicyId = UUID.randomUUID().toString();
		this.policyName = name;
		if (policyType != null)
		{
			this.policyType = policyType.toString();
		}
		else
		{
			throw new InvalidPolicyException("No policy type was provided for the resource main policy");
		}
		if (policyType == PolicyType.UPDATE)
		{
			CdPolicy cdPolicy = new CdPolicy(false, false, this);
			this.cdPolicy = cdPolicy;
		}
		this.logicOperator = "AND";
		this.resource = resource;
		if (this.resource == null)
		{
			throw new InvalidPolicyException("No related resource found in main resource policy");
		}
		else 
		{
			resource.addPolicy(this);
		}
		this.parentPolicy = null;
		this.subPolicies = new HashSet<>();
		this.entityConditions = new HashSet<>();
		this.attributeConditions = new HashSet<>();
		this.fieldConditions = new HashSet<>();
	}
	
	private AbacPolicy(AbacPolicy parentPolicy) throws InvalidPolicyException
	{
		if (parentPolicy == null)
		{
			throw new InvalidPolicyException("No parent policy found in sub-policy");
		}
		this.abacPolicyId = UUID.randomUUID().toString();
		this.policyName = null;
		this.policyType = parentPolicy.policyType;
		parentPolicy.getSubPolicies().add(this);
		if (PolicyType.valueOf(this.policyType) == PolicyType.UPDATE)
		{
			CdPolicy cdPolicy = new CdPolicy(false, false, this);
			this.cdPolicy = cdPolicy;
		}
		this.logicOperator = "AND";
		this.resource = null;
		this.parentPolicy = parentPolicy;
		this.subPolicies = new HashSet<>();
		this.entityConditions = new HashSet<>();
		this.attributeConditions = new HashSet<>();
		this.fieldConditions = new HashSet<>();
	}

	public String getAbacPolicyId()
	{
		return abacPolicyId;
	}

	public String getPolicyName()
	{
		return policyName;
	}

	protected void setPolicyName(String policyName)
	{
		this.policyName = policyName;
	}
	
	public PolicyType getPolicyType()
	{
		return PolicyType.valueOf(this.policyType.toUpperCase());
	}
	
	public void setPolicyType(PolicyType policyType)
	{
		this.policyType = policyType.toString();
		if (policyType == PolicyType.UPDATE)
		{
			this.setCdPolicy(false, false);
		}
	}

	public LogicOperator getLogicOperator()
	{
		return LogicOperator.valueOf(this.logicOperator.toUpperCase());
	}
	
	public void setLogicOperator(LogicOperator logicOperator)
	{
		this.logicOperator = logicOperator.toString();
	}

	public CdPolicy getCdPolicy()
	{
		return cdPolicy;
	}

	public void setCdPolicy(boolean create, boolean delete)
	{
		CdPolicy cdPolicy = new CdPolicy(create, delete, this);
		this.cdPolicy = cdPolicy;
	}

	public Resource getResource()
	{
		return resource;
	}
	
	public Resource getRootResource()
	{
		if (this.resource != null)
		{
			return resource;			
		}
		else
		{
			if (this.parentPolicy != null)
			{
				return this.parentPolicy.getResource();
			}
			else
			{
				return null;
			}
		}		
	}

	public void setResource(Resource resource) throws InvalidPolicyException
	{
		this.resource = resource;
		if (!resource.getPolicies().contains(this))
		{
			resource.addPolicy(this);
		}
	}
	
	public Set<AbacPolicy> getSubPolicies()
	{
		return subPolicies;
	}
	
	public AbacPolicy addSubPolicy() throws InvalidPolicyException
	{
		AbacPolicy subPolicy = new AbacPolicy(this);
		this.subPolicies.add(subPolicy);
		
		return subPolicy;
	}
	
	public AbacPolicy addSubPolicy(LogicOperator logicOperator) throws InvalidPolicyException
	{
		AbacPolicy subPolicy = new AbacPolicy(this);
		subPolicy.setLogicOperator(logicOperator);

		return subPolicy;
	}
	
	public AbacPolicy getParentPolicy()
	{
		return parentPolicy;
	}
	
	public void setParentPolicy(AbacPolicy parentPolicy)
	{
		this.parentPolicy = parentPolicy;
	}
	
	public Set<EntityCondition> getEntityConditions()
	{
		return entityConditions;
	}
	
	public void addEntityCondition(UserAttribute userAttribute, ComparisonOperator comparison, String value)
	{
		EntityCondition entityCondition = new EntityCondition(this, userAttribute, comparison, value);
		if (!this.entityConditions.contains(entityCondition))
		{
			this.entityConditions.add(entityCondition);
		}
	}
	
	public Set<AttributeCondition> getAttributeConditions()
	{
		return attributeConditions;
	}
	
	public void addAttributeCondition(ResourceAttribute resourceAttribute, ComparisonOperator comparison, UserAttribute userAttribute)
	{
		AttributeCondition attributeCondition = new AttributeCondition(this, resourceAttribute, comparison, userAttribute);
		if (!this.attributeConditions.contains(attributeCondition))
		{
			this.attributeConditions.add(attributeCondition);
		}
	}
	
	public Set<FieldCondition> getFieldConditions()
	{
		return fieldConditions;
	}
	
	public void addFieldConditions(String fieldName, ComparisonOperator comparison, String value) throws InvalidPolicyException
	{
		if (this.getPolicyType() == PolicyType.READ)
		{
			Set<ResourceField> resourceFields = this.getRootResource().getResourceFields();
			for (ResourceField next : resourceFields)
			{
				if (!next.getAssociation() && next.getResourceFieldName().equals(fieldName))
				{
					FieldCondition fieldCondition = new FieldCondition(fieldName, comparison, value, this);
					this.fieldConditions.add(fieldCondition);
					return;
				}
			}
			throw new InvalidPolicyException("The indicated field does not belong to the referenced resource");		
		}
		else
		{
			throw new InvalidPolicyException("Field Conditions only apply to read policies");
		}
		
	}
	
	
	/*
	 * 	CONDITION GROUPS ONLY
	 * =======================
	 */
	
	public boolean isEntityAccessGranted(User user)
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
	
	private String readAttributePolicies(String projectAlias, User user)
	{
		String policy = "";
		boolean notFirstAttribute = false;
		for (AttributeCondition condition : this.attributeConditions)
		{
			if (notFirstAttribute) 
			{
				policy += " " + this.logicOperator + " ";
			}
			else
			{
				notFirstAttribute = true;
			}
			policy += condition.getReadPolicy(projectAlias, user);
			
		}
		
		return policy;
	}
	
	private String readFieldPolicies(String resourceAlias)
	{
		String policy = "";
		boolean notFirstAttribute = false;
		for (FieldCondition condition : this.fieldConditions)
		{
			if (notFirstAttribute) 
			{
				policy += " " + this.logicOperator + " ";
			}
			else
			{
				notFirstAttribute = true;
			}
			policy += condition.getReadPolicy(resourceAlias);
			
		}
		
		return policy;
	}
	
	private String readAttributeAndFieldPolicies(String resourceAlias, String projectAlias, User user)
	{
		String attribPolicy = this.readAttributePolicies(projectAlias, user);
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
	
	private ResourceReadPolicy readGroupPolicy(String resourceAlias, String projectAlias, User user)
	{
		ResourceReadPolicy resourceReadPolicy = new ResourceReadPolicy();
		if (this.isEntityAccessGranted(user))
		{
			resourceReadPolicy.setReadGranted(true);
			resourceReadPolicy.setReadConditions(this.readAttributeAndFieldPolicies(resourceAlias, projectAlias, user));
		}
		
		return resourceReadPolicy;
	}
	
	/*
	 * ALL POLICIES RETURN INCLUDING SUBGROUPS
	 * =======================================================
	 */
	
	public ResourceReadPolicy getReadPolicy(Class<?> resourceClass, User user)
	{
		String resourceAlias = resourceClass.getSimpleName();
		resourceAlias = resourceAlias.substring(0, 1).toLowerCase() + resourceAlias.substring(1);
		String projectAlias = "project";
		
		if (this.getLogicOperator() == LogicOperator.AND)
		{
			ResourceReadPolicy readPolicy = new ResourceReadPolicy();

			if (this.isEntityAccessGranted(user))
			{
				readPolicy.setReadGranted(true);
				String conditions = this.readAttributeAndFieldPolicies(resourceAlias, projectAlias, user);
				
				for (AbacPolicy subPolicy : this.getSubPolicies())
				{
					if (subPolicy.isEntityAccessGranted(user))
					{
						String groupConditions = subPolicy.readAttributeAndFieldPolicies(resourceAlias, projectAlias, user);
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
				resourceReadPolicy.setReadConditions(this.readAttributeAndFieldPolicies(resourceAlias, projectAlias, user));				
			}

			for (AbacPolicy subPolicy : this.getSubPolicies())
			{
				ResourceReadPolicy groupReadPolicy = subPolicy.readGroupPolicy(resourceAlias, projectAlias, user);
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
	
	public boolean getModifyPolicy(ResourceAttribs resourceAttribs, UserAttribs userAttribs, User user)
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
				
				for (AbacPolicy subPolicy : this.getSubPolicies())
				{
					if (!subPolicy.getModifyPolicy(resourceAttribs, userAttribs, user))
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
				
				for (AbacPolicy subPolicy : this.getSubPolicies())
				{
					if (subPolicy.getModifyPolicy(resourceAttribs, userAttribs, user))
					{
						return true;
					}
				}
			}
			
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((abacPolicyId == null) ? 0 : abacPolicyId.hashCode());
		result = prime * result + ((policyName == null) ? 0 : policyName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		AbacPolicy other = (AbacPolicy) obj;
		if (abacPolicyId == null)
		{
			if (other.abacPolicyId != null) return false;
		}
		else if (!abacPolicyId.equals(other.abacPolicyId)) return false;
		if (policyName == null)
		{
			if (other.policyName != null) return false;
		}
		else if (!policyName.equals(other.policyName)) return false;
		return true;
	}



//	@Override
//	public int hashCode()
//	{
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((attributeConditions == null) ? 0 : attributeConditions.hashCode());
//		result = prime * result + ((cdPolicy == null) ? 0 : cdPolicy.hashCode());
//		result = prime * result + ((entityConditions == null) ? 0 : entityConditions.hashCode());
//		result = prime * result + ((fieldConditions == null) ? 0 : fieldConditions.hashCode());
//		result = prime * result + ((logicOperator == null) ? 0 : logicOperator.hashCode());
//		result = prime * result + ((resource == null) ? 0 : resource.hashCode());
//		result = prime * result + ((subPolicies == null) ? 0 : subPolicies.hashCode());
//		return result;
//	}

//	@Override
//	public boolean equals(Object obj)
//	{
//		if (this == obj) return true;
//		if (obj == null) return false;
//		if (getClass() != obj.getClass()) return false;
//		AbacPolicy other = (AbacPolicy) obj;
//		if (attributeConditions == null)
//		{
//			if (other.attributeConditions != null) return false;
//		}
//		else if (!attributeConditions.equals(other.attributeConditions)) return false;
//		if (cdPolicy == null)
//		{
//			if (other.cdPolicy != null) return false;
//		}
//		else if (!cdPolicy.equals(other.cdPolicy)) return false;
//		if (entityConditions == null)
//		{
//			if (other.entityConditions != null) return false;
//		}
//		else if (!entityConditions.equals(other.entityConditions)) return false;
//		if (fieldConditions == null)
//		{
//			if (other.fieldConditions != null) return false;
//		}
//		else if (!fieldConditions.equals(other.fieldConditions)) return false;
//		if (logicOperator == null)
//		{
//			if (other.logicOperator != null) return false;
//		}
//		else if (!logicOperator.equals(other.logicOperator)) return false;
//		if (resource == null)
//		{
//			if (other.resource != null) return false;
//		}
//		else if (!resource.equals(other.resource)) return false;
//		if (subPolicies == null)
//		{
//			if (other.subPolicies != null) return false;
//		}
//		else if (!subPolicies.equals(other.subPolicies)) return false;
//		return true;
//	}
	
}

