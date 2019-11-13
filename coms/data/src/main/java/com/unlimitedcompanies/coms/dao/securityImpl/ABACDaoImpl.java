package com.unlimitedcompanies.coms.dao.securityImpl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.ABACDao;
import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.domain.abac.ResourceField;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class ABACDaoImpl implements ABACDao
{
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public void savePolicy(AbacPolicy policy)
	{
		em.persist(policy);
	}
	
	@Override
	public void registerResource(Resource resource)
	{
		em.persist(resource);
	}
	
	@Override
	public void registerResourceField(ResourceField resourceField)
	{
		em.persist(resourceField);
	}

	@Override
	public int getNumberOfPolicies()
	{		
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(abacPolicyId) FROM abacPolicies").getSingleResult();
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
	public int getNumberOfRestrictedFields()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(resourceFieldId_FK) FROM restrictedFields").getSingleResult();
		return bigInt.intValue();
	}
	
	@Override
	public int getNumberOfMainPolicies()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(abacPolicyId) FROM abacPolicies WHERE abacPolicyId_FK IS NULL").getSingleResult();
		return bigInt.intValue();
	}
	
	@Override
	@Transactional(noRollbackFor = NoResultException.class)
	public AbacPolicy getPolicy(Resource resource, PolicyType policyType, String accessConditions)
	{
		String queryString = "select p1 from AbacPolicy p1 "
				+ "left join fetch p1.subPolicies p2 "
				+ "left join fetch p2.subPolicies p3 "
				+ "left join fetch p3.subPolicies p4 "
				+ "where p1.resource = :resource and p1.policyType = :policyType";
		
		if (accessConditions != null && !accessConditions.isEmpty())
		{
			queryString += " and (" + accessConditions + ")";
		}

		return em.createQuery(queryString, AbacPolicy.class)
				 .setParameter("resource", resource)
				 .setParameter("policyType", policyType.toString())
				 .getSingleResult();
	}
	
	@Override
	public AbacPolicy getPolicyWithRestrictedFields(Resource resource, PolicyType policyType, String accessConditions)
	{
		String queryString = "select p1 from AbacPolicy p1 "
				+ "left join fetch p1.subPolicies p2 "
				+ "left join fetch p2.subPolicies p3 "
				+ "left join fetch p3.subPolicies p4 "
				+ "left join fetch p1.resource resource "
				+ "left join fetch resource.resourceFields resourceFields "
				+ "where resource = :resource and p1.policyType = :policyType";
		
		if (accessConditions != null && !accessConditions.isEmpty())
		{
			queryString += " and (" + accessConditions + ")";
		}		

		return em.createQuery(queryString, AbacPolicy.class)
				 .setParameter("resource", resource)
				 .setParameter("policyType", policyType.toString())
				 .getSingleResult();
	}
	
	@Override
	public AbacPolicy getPolicyById(String abacPolicyId, String accessConditions)
	{
		String queryString = "select p1 from AbacPolicy p1 "
				+ "left join fetch p1.subPolicies p2 "
				+ "left join fetch p2.subPolicies p3 "
				+ "left join fetch p3.subPolicies p4 "
				+ "where p1.abacPolicyId = :id";
		
		if (accessConditions != null && !accessConditions.isEmpty())
		{
			queryString += " and (" + accessConditions + ")";
		}

		return em.createQuery(queryString, AbacPolicy.class)
					.setParameter("id", abacPolicyId)
					.getSingleResult();
	}
	
	@Override
	public List<AbacPolicy> getPoliciesByRange(int elements, int page, String accessConditions)
	{
		String queryString = "select policy from AbacPolicy policy where policy.resource != null";
		if (accessConditions != null && !accessConditions.isEmpty())
		{
			queryString += " where " + accessConditions;
		}
		queryString += " order by policy.policyName";
		
		return em.createQuery(queryString, AbacPolicy.class)
				 .setFirstResult(page * elements)
				 .setMaxResults(elements)
				 .getResultList();
	}
	
	@Override
	public List<Resource> getAllResources()
	{
		return em.createQuery("select resource from Resource resource", Resource.class)
					.getResultList();
	}
	
	@Override
	public List<String> getAllResourceNames()
	{
		return em.createQuery("select resource.resourceName from Resource resource", String.class)
				.getResultList();
	}
	
	@Override
	public Resource getResourceByName(String name)
	{
		return em.createQuery("select resource from Resource resource "
				+ "left join fetch resource.policies p1 "
				+ "left join fetch p1.subPolicies p2 "
				+ "left join fetch p2.subPolicies p3 "
				+ "left join fetch p3.subPolicies p4 "
				+ "where resource.resourceName = :name", Resource.class)
				.setParameter("name", name)
				.getSingleResult();
	}
	
	@Override
	public Resource getResourceByNameWithFields(String name)
	{
		return em.createQuery("select resource from Resource resource "
				+ "left join fetch resource.policies p1 "
				+ "left join fetch p1.subPolicies p2 "
				+ "left join fetch p2.subPolicies p3 "
				+ "left join fetch p3.subPolicies p4 "
				+ "left join fetch resource.resourceFields "
				+ "where resource.resourceName = :name",
				Resource.class).setParameter("name", name).getSingleResult();
	}
	
	@Override
	public ResourceField getResourceFieldById(int fieldId)
	{
		ResourceField field = em.find(ResourceField.class, fieldId);
		if (field == null)
		{
			throw new NoResultException();
		}
		return field;
	}
	
	@Override
	public List<ResourceField> getAllResourceFieldsWithResources()
	{
		return em.createQuery("select field from ResourceField field left join fetch field.resource", ResourceField.class)
				.getResultList();
	}
	
	@Override
	public List<ResourceField> getRestrictedFields(int userId, int resourceId)
	{
		String queryString = "select field from ResourceField field "
							+ "join field.resource resource "
							+ "join field.restrictedForRoles role "
							+ "join role.users user "
							+ "where resource.resourceId = :resourceId "
							+ "and user.userId = :userId";
		
		return em.createQuery(queryString, ResourceField.class)
				 .setParameter("resourceId", resourceId)
				 .setParameter("userId", userId)
				 .getResultList();
	}
	
	@Override
	public void deletePolicy(String abacPolicyId)
	{
		AbacPolicy policy = em.find(AbacPolicy.class, abacPolicyId);
		if (policy == null) throw new NoResultException();
		em.remove(policy);
	}
	
	@Override
	public void checkResourceList()
	{
		/*
		 * This method will check and make sure all resources found by the entity manager are accounted and stored in the db table resources
		 */
		
		Set<EntityType<?>> entities = em.getMetamodel().getEntities();
		
		List<String> resources = this.getAllResourceNames();
		
		resources.add("ContactAddress");
		resources.add("ContactPhone");
		
		for (EntityType<?> entity : entities)
		{
			if (!resources.contains(entity.getName()))
			{
				Resource resource = new Resource(entity.getName());
				this.registerResource(resource);
			}
		}
		
		Resource fieldResource = new Resource("RestrictedField");
		this.registerResource(fieldResource);
	}
	
	@Override
	public void checkResourceFieldList()
	{
		/*
		 * This method will check and make sure all resource fields found by the entity manager are accounted and stored in the db table resourceFields
		 */
		
		List<ResourceField> fields = this.getAllResourceFieldsWithResources();
		
		Set<EntityType<?>> entities = em.getMetamodel().getEntities();
		List<ResourceField> foundFields = new ArrayList<>();
		
		List<String> nonResources = new ArrayList<>();
		nonResources.add("Resource");
		nonResources.add("ResourceField");
		nonResources.add("ContactAddress");
		nonResources.add("ContactPhone");
		
		for (EntityType<?> entity : entities)
		{
			if (!nonResources.contains(entity.getName()))
			{
				String resourceName = entity.getName();
				Resource resource = this.getResourceByName(resourceName);
				for (Attribute<?,?> attribute : entity.getAttributes())
				{
					ResourceField foundRF = new ResourceField(attribute.getName(), attribute.isAssociation(), resource);
					foundFields.add(foundRF);
				}				
			}
		}
		
		for (ResourceField rf : foundFields)
		{
			if (!fields.contains(rf))
			{
				this.registerResourceField(rf);
			}
		}
	}
	
}
