package com.unlimitedcompanies.coms.ws.reps.abac;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.data.exceptions.InvalidPolicyException;
import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.ComparisonOperator;
import com.unlimitedcompanies.coms.domain.abac.LogicOperator;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.domain.abac.ResourceAttribute;
import com.unlimitedcompanies.coms.domain.abac.UserAttribute;
import com.unlimitedcompanies.coms.ws.controllers.abac.PolicyRestController;

@XmlRootElement(name = "policy")
public class PolicyDTO extends ResourceSupport
{
	private String abacPolicyId;
	private String policyName;
	private String policyType;
	private String logicOperator;
	private CdPolicyDTO cdPolicy;
	private ResourceDTO resource;
	private PolicyCollectionResponse subPolicies;
	private EntityConditionCollectionResponse entityConditions;
	private AttributeConditionCollectionResponse attributeConditions;
	private FieldConditionCollectionResponse fieldConditions;
	
	protected PolicyDTO() {}
	
	public PolicyDTO(AbacPolicy policy)
	{		
		this.abacPolicyId = policy.getAbacPolicyId();
		if (this.abacPolicyId != null)
		{
			Link selfLink = null;
			try
			{
				selfLink = linkTo(methodOn(PolicyRestController.class).getPolicyById(this.abacPolicyId)).withSelfRel();
			}
			catch (Exception e) 
			{
				// No exception will ever be thrown as the method won't be called - this is just for heateos reference to the end point
			}
			this.add(selfLink);
		}
		
		this.policyName = policy.getPolicyName();
		this.policyType = policy.getPolicyType().toString();
		this.logicOperator = policy.getLogicOperator().toString();
		
		if (policy.getCdPolicy() != null)
		{
			this.cdPolicy = new CdPolicyDTO(policy.getCdPolicy());
		}
		else
		{
			this.cdPolicy = null;
		}
		
		if (policy.getResource() != null)
		{
			this.resource = new ResourceDTO(policy.getResource().getResourceId(), policy.getResource().getResourceName());
		}
		else
		{
			this.resource = null;
		}
		
		this.subPolicies = new PolicyCollectionResponse(policy.getSubPolicies());
		this.entityConditions = new EntityConditionCollectionResponse(policy.getEntityConditions());
		this.attributeConditions = new AttributeConditionCollectionResponse(policy.getAttributeConditions());
		this.fieldConditions = new FieldConditionCollectionResponse(policy.getFieldConditions());
	}

	public String getAbacPolicyId()
	{
		return abacPolicyId;
	}

	public void setAbacPolicyId(String abacPolicyId)
	{
		this.abacPolicyId = abacPolicyId;
		if (this.abacPolicyId != null)
		{
			Link selfLink = null;
			try
			{
				selfLink = linkTo(methodOn(PolicyRestController.class).getPolicyById(this.abacPolicyId)).withSelfRel();
			}
			catch (Exception e) 
			{
				// No exception will ever be thrown as the method won't be called - this is just for heateos reference to the end point
			}
			this.removeLinks();
			this.add(selfLink);
		}
	}

	public String getPolicyName()
	{
		return policyName;
	}

	public void setPolicyName(String policyName)
	{
		this.policyName = policyName;
	}

	public String getPolicyType()
	{
		return policyType;
	}

	public void setPolicyType(String policyType)
	{
		this.policyType = policyType;
	}

	public String getLogicOperator()
	{
		return logicOperator;
	}

	public void setLogicOperator(String logicOperator)
	{
		this.logicOperator = logicOperator;
	}

	public CdPolicyDTO getCdPolicy()
	{
		return cdPolicy;
	}

	public void setCdPolicy(CdPolicyDTO cdPolicy)
	{
		this.cdPolicy = cdPolicy;
	}

	public ResourceDTO getResource()
	{
		return resource;
	}

	public void setResource(ResourceDTO resource)
	{
		this.resource = resource;
	}

	@XmlElement(name = "sub-policies")
	public PolicyCollectionResponse getSubPolicies()
	{
		return subPolicies;
	}

	public void setSubPolicies(PolicyCollectionResponse subPolicies)
	{
		this.subPolicies = subPolicies;
	}

	public EntityConditionCollectionResponse getEntityConditions()
	{
		return entityConditions;
	}

	public void setEntityConditions(EntityConditionCollectionResponse entityConditions)
	{
		this.entityConditions = entityConditions;
	}

	public AttributeConditionCollectionResponse getAttributeConditions()
	{
		return attributeConditions;
	}

	public void setAttributeConditions(AttributeConditionCollectionResponse attributeConditions)
	{
		this.attributeConditions = attributeConditions;
	}

	public FieldConditionCollectionResponse getFieldConditions()
	{
		return fieldConditions;
	}

	public void setFieldConditions(FieldConditionCollectionResponse fieldConditions)
	{
		this.fieldConditions = fieldConditions;
	}
	
	public AbacPolicy getAbacPolicy(Resource resource) throws InvalidPolicyException
	{
		AbacPolicy abacPolicy = new AbacPolicy(this.policyName, PolicyType.valueOf(this.policyType), resource);
		abacPolicy.setLogicOperator(LogicOperator.valueOf(this.logicOperator));
		
		if (entityConditions != null && entityConditions.getEntityConditions() != null)
		{
			for(EntityConditionDTO condition : entityConditions.getEntityConditions())
			{
				abacPolicy.addEntityCondition(UserAttribute.valueOf(condition.getUserAttribute()), ComparisonOperator.valueOf(condition.getComparison()), condition.getValue());
			}
		}
		
		if (attributeConditions != null && attributeConditions.getAttributeConditions() != null)
		{
			for(AttributeConditionDTO condition : attributeConditions.getAttributeConditions())
			{
				abacPolicy.addAttributeCondition(ResourceAttribute.valueOf(condition.getResourceAttribute()), ComparisonOperator.valueOf(condition.getComparison()), UserAttribute.valueOf(condition.getUserAttribute()));
			}
		}
		
		if (fieldConditions != null && fieldConditions.getFieldConditions() != null)
		{
			for(FieldConditionDTO condition : fieldConditions.getFieldConditions())
			{
				abacPolicy.addFieldConditions(condition.getFieldName(), ComparisonOperator.valueOf(condition.getComparison()), condition.getValue());
			}
		}
		
		if (cdPolicy != null)
		{
			abacPolicy.setCdPolicy(cdPolicy.isCreatePolicy(), cdPolicy.isDeletePolicy());
		}
		
		if (subPolicies != null && subPolicies.getPolicies() != null)
		{
			for(PolicyDTO subPolicyDTO : subPolicies.getPolicies())
			{
				subPolicyDTO.getAbacPolicy(abacPolicy);
			}
		}
		
		return abacPolicy;
	}
	
	private AbacPolicy getAbacPolicy(AbacPolicy parentPolicy) throws InvalidPolicyException
	{
		AbacPolicy abacPolicy = parentPolicy.addSubPolicy(LogicOperator.valueOf(this.logicOperator));
		
		if (parentPolicy.getPolicyType() == PolicyType.UPDATE && this.cdPolicy != null)
		{
			abacPolicy.setCdPolicy(this.cdPolicy.isCreatePolicy(), this.cdPolicy.isDeletePolicy());
		}
		
		if (entityConditions != null && entityConditions.getEntityConditions() != null)
		{
			for(EntityConditionDTO condition : entityConditions.getEntityConditions())
			{
				abacPolicy.addEntityCondition(UserAttribute.valueOf(condition.getUserAttribute()), ComparisonOperator.valueOf(condition.getComparison()), condition.getValue());
			}
		}
		
		if (attributeConditions != null && attributeConditions.getAttributeConditions() != null)
		{
			for(AttributeConditionDTO condition : attributeConditions.getAttributeConditions())
			{
				abacPolicy.addAttributeCondition(ResourceAttribute.valueOf(condition.getResourceAttribute()), ComparisonOperator.valueOf(condition.getComparison()), UserAttribute.valueOf(condition.getUserAttribute()));
			}
		}
		
		if (fieldConditions != null && fieldConditions.getFieldConditions() != null)
		{
			for(FieldConditionDTO condition : fieldConditions.getFieldConditions())
			{
				abacPolicy.addFieldConditions(condition.getFieldName(), ComparisonOperator.valueOf(condition.getComparison()), condition.getValue());
			}
		}
		
		if (cdPolicy != null)
		{
			abacPolicy.setCdPolicy(cdPolicy.isCreatePolicy(), cdPolicy.isDeletePolicy());
		}
		
		if (subPolicies != null && subPolicies.getPolicies() != null)
		{
			for(PolicyDTO subPolicyDTO : subPolicies.getPolicies())
			{
				subPolicyDTO.getAbacPolicy(abacPolicy);
			}
		}
		
		return abacPolicy;
	}
	
}
