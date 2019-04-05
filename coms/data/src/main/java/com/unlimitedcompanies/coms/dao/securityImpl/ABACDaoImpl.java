package com.unlimitedcompanies.coms.dao.securityImpl;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
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
	public void savePolicy(ABACPolicy policy)
	{
		try
		{
			em.createNativeQuery("INSERT INTO abacPolicy (policyName, policyType, logicOperator, resourceId_FK) VALUES (:name, :type, :operator, :resource)")
			  .setParameter("name", policy.getPolicyName())
			  .setParameter("type", policy.getType().toString())
			  .setParameter("operator", policy.getOperator().toString())
			  .setParameter("resource", policy.getResource().getResourceId())
			  .executeUpdate();
		}
		catch (PersistenceException e)
		{
			if (e.getCause() instanceof ConstraintViolationException)
			{
				throw (ConstraintViolationException)e.getCause();
			}
			else
			{
				throw e;
			}
		}
	}

	@Override
	public ABACPolicy findPolicyByName(String policyName)
	{
		return em.createQuery("select policy from ABACPolicy as policy where policy.policyName = :name", ABACPolicy.class)
				 .setParameter("name", policyName)
				 .getSingleResult();
	}

}
