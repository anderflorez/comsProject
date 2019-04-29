package com.unlimitedcompanies.coms.data.abac;

import java.util.ArrayList;
import java.util.List;
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

import com.unlimitedcompanies.coms.data.exceptions.DuplicatedResourcePolicyException;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.User;

@Entity
@Table(name = "abacPolicies")
public class ABACPolicy
{
	@Id
	@Column(unique=true, nullable=false)
	private String abacPolicyId;
	
	@Column(unique=true, nullable=false)
	private String policyName;
	
	@Column(unique=false, nullable=false)
	private String policyType;
	
	@Column(unique=false, nullable=false)
	private String logicOperator;
	
	@OneToOne(mappedBy="policy", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private CdPolicy cdPolicy;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="resourceId_FK")
	private Resource resource;
	
	@OneToMany(mappedBy="abacPolicy", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<ConditionGroup> conditionGroups;
	
	protected ABACPolicy() 
	{
		this.abacPolicyId = UUID.randomUUID().toString();
		this.conditionGroups = new ArrayList<>();
	}
	
	public ABACPolicy(String name, PolicyType policyType, Resource resource) 
			throws DuplicatedResourcePolicyException 
	{
		this.abacPolicyId = UUID.randomUUID().toString();
		this.policyName = name;
		this.policyType = policyType.toString();
		if (policyType == PolicyType.UPDATE)
		{
			CdPolicy cdPolicy = new CdPolicy(false, false, this);
			this.cdPolicy = cdPolicy;
		}
		this.logicOperator = "AND";
		this.resource = resource;
		resource.addPolicy(this);
		this.conditionGroups = new ArrayList<>();
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
	
	public PolicyResponse getQueryPolicy(User user, String resourceEntityName)
	{		
		return ABACPolicy.unifyConditionGroupPolicies(user, 
													resourceEntityName, 
													this.conditionGroups, 
													getLogicOperator());
		
	}
	
	protected static PolicyResponse unifyConditionGroupPolicies(User user, 
													  String resourceEntityName, 
													  List<ConditionGroup> conditionGroups,
													  LogicOperator logicOperator)
	{
		if (conditionGroups.size() == 0)
		{
			return null;
		}
		
		PolicyResponse policyResponse = new PolicyResponse();

		List<PolicyResponse> allGroupPolicies = new ArrayList<>();
		for (ConditionGroup next : conditionGroups)
		{
			allGroupPolicies.add(next.getConditionGroupPolicies(user, resourceEntityName));
		}		
		
		if (logicOperator == LogicOperator.AND)
		{
			policyResponse.setAccessGranted(true);
			for (int i = 0; i < allGroupPolicies.size(); i++)
			{
				if (allGroupPolicies.get(i).isAccessGranted()) 
				{
					if (policyResponse.getAccessConditions().isEmpty())
					{
						policyResponse.setAccessConditions(allGroupPolicies.get(i).getAccessConditions());					
					}
					else
					{
						policyResponse.addAccessConditions(logicOperator, allGroupPolicies.get(i).getAccessConditions());
					}
				}
				else
				{
					policyResponse.setAccessGranted(false);
					policyResponse.setAccessConditions(null);
					return policyResponse;
				}
				
			}
		}
		else // if logicOperator equals OR
		{
			policyResponse.setAccessGranted(false);
			for (int i = 0; i < allGroupPolicies.size(); i++)
			{
				if (allGroupPolicies.get(i).isAccessGranted()) 
				{
					policyResponse.setAccessGranted(true);
					if (policyResponse.getAccessConditions().isEmpty())
					{
						policyResponse.setAccessConditions(allGroupPolicies.get(i).getAccessConditions());
					}
					else
					{
						policyResponse.addAccessConditions(logicOperator, allGroupPolicies.get(i).getAccessConditions());
					}
				}
			}
		}
		
		return policyResponse;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((policyType == null) ? 0 : policyType.hashCode());
		result = prime * result + ((resource == null) ? 0 : resource.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ABACPolicy other = (ABACPolicy) obj;
		if (policyType == null)
		{
			if (other.policyType != null) return false;
		}
		else if (!policyType.equals(other.policyType)) return false;
		if (resource == null)
		{
			if (other.resource != null) return false;
		}
		else if (!resource.equals(other.resource)) return false;
		return true;
	}
	
}
