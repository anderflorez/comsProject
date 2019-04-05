package com.unlimitedcompanies.coms.data.abac;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.data.exceptions.DuplicatedResourcePolicyException;
import com.unlimitedcompanies.coms.domain.security.Resource;

@Entity
@Table(name = "abacPolicy")
public class ABACPolicy
{
	@Id
	private Integer abacPolicyId;
	
	@Column(unique=true, nullable=false)
	private String policyName;
	
	@Column(unique=false, nullable=false)
	private PolicyType policyType;
	
	@Column(unique=false, nullable=false)
	private String logicOperator;
	
	@ManyToOne
	@JoinColumn(name="resourceId_FK")
	private Resource resource;
	
	@OneToMany(mappedBy="abacPolicy")
	private List<ConditionGroup> conditionGroups;
	
	protected ABACPolicy() 
	{
		this.conditionGroups = new ArrayList<>();
	}
	
	public ABACPolicy(String name, PolicyType policyType, Resource resource) throws DuplicatedResourcePolicyException
	{
		this.policyName = name;
		this.policyType = policyType;
//		this.logicOperator = LogicOperator.AND;
		this.logicOperator = "AND";
		this.resource = resource;
		resource.addPolicy(this);
		this.conditionGroups = new ArrayList<>();
	}

	public Integer getAbacPolicyId()
	{
		return abacPolicyId;
	}

	protected void setAbacPolicyId(Integer abacPolicyId)
	{
		this.abacPolicyId = abacPolicyId;
	}

	public String getPolicyName()
	{
		return policyName;
	}

	protected void setPolicyName(String policyName)
	{
		this.policyName = policyName;
	}

	private String getPolicyType()
	{
		return policyType.toString();
	}
	
	public PolicyType getType()
	{
		return policyType;
	}

	protected void setPolicyType(String policyType)
	{
		this.policyType = PolicyType.valueOf(policyType.toUpperCase());
	}
	
	public void setPolicyType(PolicyType policyType)
	{
		this.policyType = policyType;
	}

	protected String getLogicOperator()
	{
		return logicOperator.toString();
	}
	
	public LogicOperator getOperator()
	{
//		return logicOperator;
		return LogicOperator.valueOf(this.logicOperator.toUpperCase());
	}

	public void setLogicOperator(String logicOperator)
	{
//		this.logicOperator = LogicOperator.valueOf(logicOperator.toUpperCase());
		this.logicOperator = logicOperator.toUpperCase();
	}
	
	public void setLogicOperator(LogicOperator logicOperator)
	{
//		this.logicOperator = logicOperator;
		this.logicOperator = logicOperator.toString();
	}

	public Resource getResource()
	{
		return resource;
	}

	public void setResource(Resource resource) throws DuplicatedResourcePolicyException
	{
		this.resource = resource;
		if (!resource.getPolicies().contains(this))
		{
			resource.addPolicy(this);
		}
	}

	public List<ConditionGroup> getConditionGroups()
	{
		return this.conditionGroups;
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
}
