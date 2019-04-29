package com.unlimitedcompanies.coms.dao.securityImpl;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.ABACDao;
import com.unlimitedcompanies.coms.data.abac.ABACPolicy;
import com.unlimitedcompanies.coms.data.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.security.Resource;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class ABACDaoImpl implements ABACDao
{
	@PersistenceContext
	private EntityManager em;

	@Override
	public int getNumberOfPolicies()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(abacPolicyId) FROM abacPolicies").getSingleResult();
		return bigInt.intValue();
	}
	
	@Override
	public int getNumberOfConditionGroups()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(conditionGroupId) FROM conditionGroups").getSingleResult();
		return bigInt.intValue();
	}
	
	@Override
	public int getNumberOfEntityConditions()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(entityConditionId) FROM entityConditions").getSingleResult();
		return bigInt.intValue();
	}
	
	@Override
	public int getNumberOfAttributeConditions()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(attributeConditionId) FROM attributeConditions").getSingleResult();
		return bigInt.intValue();
	}

	@Override
	public void savePolicy(ABACPolicy policy)
	{
		em.persist(policy);
	}
	
	@Override
	public ABACPolicy findPolicy(Resource resource, PolicyType policyType)
	{
		return em.createQuery("select policy from ABACPolicy as policy "
								+ "where policy.resource = :resource "
								+ "and policy.policyType = :policyType", 
								ABACPolicy.class)
									.setParameter("resource", resource)
									.setParameter("policyType", policyType.toString())
									.getSingleResult();
	}

	@Override
	public ABACPolicy findPolicyByName(String policyName)
	{
		ABACPolicy policy = em.createQuery("select policy from ABACPolicy as policy where policy.policyName = :name", ABACPolicy.class)
				 .setParameter("name", policyName)
				 .getSingleResult();
		
		return policy;
	}

}
