package com.unlimitedcompanies.coms.data.abac;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "recordCondition", uniqueConstraints = {@UniqueConstraint(columnNames = "abacPolicy_FK")})
public class CdPolicy
{
	@Id
	private String cdPolicyId;
	
	@Column(unique = false, nullable = false)
	private boolean create;
	
	@Column(unique = false, nullable = false)
	private boolean delete;
	
	@OneToOne
	@JoinColumn(name = "abacPolicy_FK")
	private ABACPolicy policy;
	
	protected CdPolicy() 
	{
		this.cdPolicyId = UUID.randomUUID().toString();
	}
	
	protected CdPolicy(boolean create, boolean delete, ABACPolicy policy)
	{
		this.cdPolicyId = UUID.randomUUID().toString();
		this.create = create;
		this.delete = delete;
		this.policy = policy;
	}

	public boolean isCreate()
	{
		return create;
	}

	public void setCreate(boolean create)
	{
		this.create = create;
	}

	public boolean isDelete()
	{
		return delete;
	}

	public void setDelete(boolean delete)
	{
		this.delete = delete;
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
