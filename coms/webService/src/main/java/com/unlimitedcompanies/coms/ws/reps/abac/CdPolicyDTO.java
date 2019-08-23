package com.unlimitedcompanies.coms.ws.reps.abac;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.abac.CdPolicy;

@XmlRootElement(name = "cdPolicy")
public class CdPolicyDTO extends ResourceSupport
{
	private String cdPolicyId;
	private boolean createPolicy;
	private boolean deletePolicy;
	
	public CdPolicyDTO() {}
	
	public CdPolicyDTO(CdPolicy cdPolicy)
	{
		this.cdPolicyId = cdPolicy.getCdPolicyId();
		this.createPolicy = cdPolicy.isCreatePolicy();
		this.deletePolicy = cdPolicy.isDeletePolicy();
	}

	public String getCdPolicyId()
	{
		return cdPolicyId;
	}

	public void setCdPolicyId(String cdPolicyId)
	{
		this.cdPolicyId = cdPolicyId;
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
}
