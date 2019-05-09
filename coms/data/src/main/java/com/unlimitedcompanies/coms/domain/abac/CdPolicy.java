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
	private ABACPolicy policy;
	
	protected CdPolicy() 
	{
		this.cdPolicyId = UUID.randomUUID().toString();
	}
	
	protected CdPolicy(boolean createPolicy, boolean deletePolicy, ABACPolicy policy)
	{
		this.cdPolicyId = UUID.randomUUID().toString();
		this.createPolicy = createPolicy;
		this.deletePolicy = deletePolicy;
		this.policy = policy;
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

	public String getAddPolicyId()
	{
		return cdPolicyId;
	}

	public ABACPolicy getPolicy()
	{
		return policy;
	}
}
