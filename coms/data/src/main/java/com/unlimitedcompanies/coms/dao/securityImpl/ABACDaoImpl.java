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
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;

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
//		em.createNativeQuery("INSERT INTO resources (resourceName) VALUES (:resourceName)")
//				.setParameter("resourceName", resourceName).executeUpdate();
	}
	
	@Override
	public void registerResourceField(ResourceField resourceField)
	{
		em.persist(resourceField);
//		em.createNativeQuery(
//				"INSERT INTO resourceFields (resourceFieldName, association, resourceId_FK) VALUES (:field, :association, :resource)")
//				.setParameter("field", resourceField.getResourceFieldName())
//				.setParameter("association", resourceField.getAssociation())
//				.setParameter("resource", resourceField.getResource())
//				.executeUpdate();
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
	public AbacPolicy searchPolicy(Resource resource, PolicyType policyType, String accessConditions)
	{
		String queryString = "select policy from AbacPolicy as policy where policy.resource = :resource and policy.policyType = :policyType";
		
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
	public List<String> searchAllResourceNames()
	{
		return em.createQuery("select resource.resourceName from Resource resource", String.class)
				.getResultList();
	}
	
	@Override
	public Resource searchResourceByName(String name)
	{
		Resource resource = em.createQuery("select resource from Resource resource where resource.resourceName = :name", Resource.class)
				.setParameter("name", name)
				.getSingleResult();
		
		return resource;
	}
	
	@Override
	public Resource searchResourceByNameWithFields(String name)
	{
		return em.createQuery("select resource from Resource resource left join fetch resource.resourceFields where resource.resourceName = :name",
				Resource.class).setParameter("name", name).getSingleResult();
	}
	
	@Override
	public Resource searchResourceByNameWithFieldsAndPolicy(String name)
	{
		return em.createQuery("select resource from Resource resource left join fetch resource.resourceFields left join fetch resource.policies where resource.resourceName = :name",
				Resource.class).setParameter("name", name).getSingleResult();
	}
	
	@Override
	public ResourceField searchResourceFieldById(int fieldId)
	{
		ResourceField field = em.find(ResourceField.class, fieldId);
		if (field == null)
		{
			throw new NoResultException();
		}
		return field;
	}
	
	@Override
	public List<ResourceField> searchAllResourceFieldsWithResources()
	{
		return em.createQuery("select field from ResourceField field left join fetch field.resource", ResourceField.class)
				.getResultList();
	}
	
	@Override
	public void checkResourceList()
	{
		/*
		 * This method will check and make sure all resources found by the entity manager are accounted and stored in the db table resources
		 */
		
		Set<EntityType<?>> entities = em.getMetamodel().getEntities();
		List<String> resources = this.searchAllResourceNames();
		resources.add("Resource");
		resources.add("ResourceField");
		
		for (EntityType<?> entity : entities)
		{
			if (!resources.contains(entity.getName()))
			{
				Resource resource = new Resource(entity.getName());
				this.registerResource(resource);
			}
		}
	}
	
	@Override
	public void checkResourceFieldList()
	{
		/*
		 * This method will check and make sure all resource fields found by the entity manager are accounted and stored in the db table resourceFields
		 */
		
		List<ResourceField> fields = this.searchAllResourceFieldsWithResources();
		
		Set<EntityType<?>> entities = em.getMetamodel().getEntities();
		List<ResourceField> foundFields = new ArrayList<>();
		for (EntityType<?> entity : entities)
		{
			if (!entity.getName().equals("Resource") && !entity.getName().equals("ResourceField"))
			{
				String resourceName = entity.getName();
				Resource resource = this.searchResourceByName(resourceName);
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
