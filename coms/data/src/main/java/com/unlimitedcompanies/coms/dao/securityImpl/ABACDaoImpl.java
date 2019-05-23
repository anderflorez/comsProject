package com.unlimitedcompanies.coms.dao.securityImpl;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.ABACDao;
import com.unlimitedcompanies.coms.domain.abac.ABACPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.User;

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
	public ABACPolicy findPolicy(Resource resource, PolicyType policyType, String accessConditions)
	{
		String queryString = "select policy from ABACPolicy as policy where policy.resource = :resource and policy.policyType = :policyType";
		
		if (accessConditions != null && !accessConditions.isEmpty())
		{
			queryString += " and (" + accessConditions + ")";
		}		

		return em.createQuery(queryString, ABACPolicy.class)
				 .setParameter("resource", resource)
				 .setParameter("policyType", policyType.toString())
				 .getSingleResult();
	}

	@Override
	public User getFullUserWithAttribs(int userId)
	{
		return em.createQuery("select user from User user "
								+ "left join fetch user.roles roles "
								+ "left join fetch user.contact contact "
								+ "left join fetch contact.employee employee "
								+ "left join fetch employee.pmProjects pmProjects "
								+ "left join fetch employee.superintendentProjects superintendentProjects "
								+ "left join fetch employee.foremanProjects foremanProjects "
								+ "where user.userId = :userId", User.class)
					.setParameter("userId", userId)
					.getSingleResult();
	}

}
