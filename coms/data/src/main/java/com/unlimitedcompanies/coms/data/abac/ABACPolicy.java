package com.unlimitedcompanies.coms.data.abac;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "abacPolicy")
public class ABACPolicy
{
	@Id
	private Integer abacPolicyId;
	
	@Column(unique=true, nullable=false)
	private String policyName;
	
	@Column(unique=false, nullable=false)
	private String logicOperator;
	
	@ManyToOne
	@JoinColumn(name = "parentABACPolicyId_FK")
	@Column(unique=false, nullable=true)
	private ABACPolicy parentABACPolicy;
	
	@OneToMany(mappedBy="parentABACPolicy")
	private List<ABACPolicy> subPolicies;
	
	protected ABACPolicy() {}
	
	public ABACPolicy(String name, String operator)
	{
		this.policyName = name;
		this.logicOperator = operator;
	}
	
	protected int getAbacPolicyId()
	{
		return abacPolicyId;
	}

	protected void setAbacPolicyId(int abacPolicyId)
	{
		this.abacPolicyId = abacPolicyId;
	}

	protected String getPolicyName()
	{
		return policyName;
	}

	protected void setPolicyName(String policyName)
	{
		this.policyName = policyName;
	}

	protected String getLogicOperator()
	{
		return logicOperator;
	}

	protected void setLogicOperator(String logicOperator)
	{
		this.logicOperator = logicOperator;
	}

	protected ABACPolicy getParentABACPolicy()
	{
		return parentABACPolicy;
	}

	protected void setParentABACPolicy(ABACPolicy parentABACPolicy)
	{
		this.parentABACPolicy = parentABACPolicy;
	}

	protected List<ABACPolicy> getSubPolicies()
	{
		return subPolicies;
	}

	protected void setSubPolicies(List<ABACPolicy> subPolicies)
	{
		this.subPolicies = subPolicies;
	}

	private void assignParentPolicy(ABACPolicy parentPolicy)
	{
		this.parentABACPolicy = parentPolicy;
	}
	
	public ABACPolicy addASubPolicy(String name, String operator)
	{
		ABACPolicy subpolicy = new ABACPolicy(name, operator);
		this.subPolicies.add(subpolicy);
		subpolicy.assignParentPolicy(this);
		return subpolicy;
	}
}
