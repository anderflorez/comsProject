package com.unlimitedcompanies.coms.ws.reps.abac;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.AttributeCondition;
import com.unlimitedcompanies.coms.domain.abac.CdPolicy;
import com.unlimitedcompanies.coms.domain.abac.EntityCondition;
import com.unlimitedcompanies.coms.domain.abac.FieldCondition;
import com.unlimitedcompanies.coms.domain.abac.Resource;

@XmlRootElement(name = "policy")
public class AbacPolicyDTO extends ResourceSupport
{
	private String abacPolicyId;
	private String policyName;
	private String policyType;
	private String logicOperator;
//	private CdPolicy cdPolicy;
//	private Resource resource;
//	private Set<AbacPolicy> subPolicies;
//	private AbacPolicy parentPolicy;
//	private Set<EntityCondition> entityConditions;
//	private Set<AttributeCondition> attributeConditions;
//	private Set<FieldCondition> fieldConditions;
	
	public AbacPolicyDTO(AbacPolicy policy)
	{
		this.abacPolicyId = policy.getAbacPolicyId();
		this.policyName = policy.getPolicyName();
		this.policyType = policy.getPolicyType().toString();
		this.logicOperator = policy.getLogicOperator().toString();
	}

	public String getAbacPolicyId()
	{
		return abacPolicyId;
	}

	public void setAbacPolicyId(String abacPolicyId)
	{
		this.abacPolicyId = abacPolicyId;
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
}
