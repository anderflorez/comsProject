package com.unlimitedcompanies.coms.ws.reps.abac;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.LazyInitializationException;
import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;

@XmlRootElement(name = "policies")
public class PolicyCollectionResponse extends ResourceSupport
{
	private List<PolicyDTO> policies;
	private Integer page;
	private Integer prevPage;
	private Integer nextPage;
	
	protected PolicyCollectionResponse() 
	{
		this.policies = new ArrayList<>();
	}
	
	public PolicyCollectionResponse(List<AbacPolicy> abacPolicies)
	{
		try
		{
			if (abacPolicies != null)
			{			
				this.policies = new ArrayList<>();
				for (AbacPolicy abacPolicy : abacPolicies)
				{
					this.policies.add(new PolicyDTO(abacPolicy));
				} 
			}
		}
		catch (LazyInitializationException e)
		{
			// The resource is not available
			this.policies = null;
		}
	}
	
	public PolicyCollectionResponse(Set<AbacPolicy> abacPolicies)
	{
		try
		{
			if (abacPolicies != null)
			{
				this.policies = new ArrayList<>();
				for (AbacPolicy abacPolicy : abacPolicies)
				{
					this.policies.add(new PolicyDTO(abacPolicy));
				} 
			}
		}
		catch (LazyInitializationException e)
		{
			// The resource is not available
			this.policies = null;
		}
	}

	@XmlElement(name = "policy")
	public List<PolicyDTO> getPolicies()
	{
		return policies;
	}

//	public void setPolicies(List<?> policies)
//	{
//		if (!policies.isEmpty())
//		{
//			this.policies.clear();
//			if (policies.get(0).getClass() == AbacPolicy.class)
//			{
//				for (int i = 0; i < policies.size(); i++)
//				{
//					this.policies.add(new PolicyDTO((AbacPolicy) policies.get(i)));
//				}
//			}
//			else if (policies.get(0).getClass() == PolicyDTO.class)
//			{
//				for (int i = 0; i < policies.size(); i++)
//				{
//					this.policies.add((PolicyDTO) policies.get(i));
//				}
//			}
//		}
//	}

//	public void setPolicies(List<AbacPolicy> abacPolicies)
//	{
//		this.policies.clear();
//		for (AbacPolicy abacPolicy : abacPolicies)
//		{
//			this.policies.add(new PolicyDTO(abacPolicy));
//		}
//	}
//	
//	public void setPolicies(List<PolicyDTO> abacPolicies)
//	{
//		this.policies.clear();
//		for (PolicyDTO policyDTO : abacPolicies)
//		{
//			this.policies.add(policyDTO);
//		}
//	}
	
	public Integer getPage()
	{
		return page;
	}
	
	public void setPage(Integer page)
	{
		this.page = page;
	}

	public Integer getPrevPage()
	{
		return prevPage;
	}

	public void setPrevPage(Integer prevPage)
	{
		this.prevPage = prevPage;
	}

	public Integer getNextPage()
	{
		return nextPage;
	}

	public void setNextPage(Integer nextPage)
	{
		this.nextPage = nextPage;
	}
		
}
