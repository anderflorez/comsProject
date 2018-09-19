package com.unlimitedcompanies.coms.dao.security.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.ResourceField;

@Repository
@Transactional
public class SecuritySetup
{
	@PersistenceContext
	private EntityManager em;

	public void checkResourceList()
	{
		Set<EntityType<?>> entities = em.getMetamodel().getEntities();
		List<String> resources = this.findAllResourceNames();
		resources.add("Resource");
		resources.add("ResourceField");

		for (EntityType<?> entity : entities)
		{
			if (!resources.contains(entity.getName()))
			{
				System.out.println("Saving resource " + entity.getName());
				this.registerResource(entity.getName());
			}
		}
	}

	public void checkResourceFieldList()
	{		
		List<ResourceField> fields = this.findAllResourceFieldsWithResources();
		
		Set<EntityType<?>> entities = em.getMetamodel().getEntities();
		List<ResourceField> foundFields = new ArrayList<>();
		for (EntityType<?> entity : entities)
		{
			if (!entity.getName().equals("Resource") && !entity.getName().equals("ResourceField"))
			{
				String resourceName = entity.getName();
				Resource resource = this.findResourceByName(resourceName);
				for (Attribute<?,?> attribute : entity.getAttributes())
				{
					byte b = 0;
					if (attribute.isAssociation()) b = 1;
					ResourceField foundRF = new ResourceField(attribute.getName(), b, resource);
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
	
	public List<String> findAllResourceNames()
	{
		return em.createQuery("select resource.resourceName from Resource resource", String.class)
							  .getResultList();
	}
	
	public List<ResourceField> findAllResourceFieldsWithResources()
	{
		return em.createQuery("select field from ResourceField field left join fetch field.resource", ResourceField.class)
							  .getResultList();
	}

	public Resource findResourceByName(String name)
	{
		return em.createQuery("select resource from Resource resource where resource.resourceName = :name",
				Resource.class).setParameter("name", name).getSingleResult();
	}

	public void registerResource(String resourceName)
	{
		em.createNativeQuery("INSERT INTO resource (resourceName) VALUES (:resourceName)")
				.setParameter("resourceName", resourceName).executeUpdate();
	}

	public void registerResourceField(ResourceField resourceField)
	{
		em.createNativeQuery(
				"INSERT INTO resourceField (resourceFieldName, association, resourceId_FK) VALUES (:field, :association, :resource)")
				.setParameter("field", resourceField.getResourceFieldName()).setParameter("association", resourceField.getAssociation())
				.setParameter("resource", resourceField.getResource()).executeUpdate();
	}
}
