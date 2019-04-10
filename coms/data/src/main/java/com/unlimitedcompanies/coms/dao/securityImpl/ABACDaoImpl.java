package com.unlimitedcompanies.coms.dao.securityImpl;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.ABACDao;
import com.unlimitedcompanies.coms.data.abac.ABACPolicy;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class ABACDaoImpl implements ABACDao
{
	@PersistenceContext
	private EntityManager em;

	@Override
	public int getNumberOfPolicies()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(abacPolicyId) FROM abacPolicy").getSingleResult();
		return bigInt.intValue();
	}
	
	@Override
	public int getNumberOfConditionGroups()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(conditionGroupId) FROM conditionGroup").getSingleResult();
		return bigInt.intValue();
	}
	
	@Override
	public int getNumberOfEntityConditions()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(entityConditionId) FROM entityCondition").getSingleResult();
		return bigInt.intValue();
	}
	
	@Override
	public int getNumberOfRecordConditions()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(recordConditionId) FROM recordCondition").getSingleResult();
		return bigInt.intValue();
	}

	@Override
	public void savePolicy(ABACPolicy policy)
	{
		em.persist(policy);
	}

	@Override
	public ABACPolicy findPolicyByName(String policyName)
	{
		return em.createQuery("select policy from ABACPolicy as policy where policy.policyName = :name", ABACPolicy.class)
				 .setParameter("name", policyName)
				 .getSingleResult();
	}

}
