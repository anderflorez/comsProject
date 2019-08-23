package com.unlimitedcompanies.coms.domain.abac;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "cdPolicies", uniqueConstraints = {@UniqueConstraint(columnNames = "abacPolicyId_FK")})
public class CdPolicy
{
	@Id
	private String cdPolicyId;
	
	@Column(unique = false, nullable = false)
	private boolean createPolicy;
	
	@Column(unique = false, nullable = false)
	private boolean deletePolicy;
	
	@OneToOne
	@JoinColumn(name = "abacPolicyId_FK")
	private AbacPolicy policy;
	
	protected CdPolicy() 
	{
		this.cdPolicyId = UUID.randomUUID().toString();
	}
	
	protected CdPolicy(boolean createPolicy, boolean deletePolicy, AbacPolicy policy)
	{
		this.cdPolicyId = UUID.randomUUID().toString();
		this.createPolicy = createPolicy;
		this.deletePolicy = deletePolicy;
		this.policy = policy;
	}

	public String getCdPolicyId()
	{
		return cdPolicyId;
	}

	public boolean isCreatePolicy()
	{
		return createPolicy;
	}

	public void setCreatePolicy(boolean createPolicy)
	{
		this.createPolicy = createPolicy;
	}

	public boolean isDeletePolicy()
	{
		return deletePolicy;
	}

	public void setDeletePolicy(boolean deletePolicy)
	{
		this.deletePolicy = deletePolicy;
	}

	public AbacPolicy getPolicy()
	{
		return policy;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (createPolicy ? 1231 : 1237);
		result = prime * result + (deletePolicy ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		CdPolicy other = (CdPolicy) obj;
		if (createPolicy != other.createPolicy) return false;
		if (deletePolicy != other.deletePolicy) return false;
		return true;
	}
}
