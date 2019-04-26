package com.unlimitedcompanies.coms.dao.securitySettingsImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.AuthDao;
import com.unlimitedcompanies.coms.dao.security.ContactDao;
import com.unlimitedcompanies.coms.dao.securitySettings.SecuritySetupDao;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.ResourceField;
import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.domain.security.User;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class SecuritySetupDaoImpl implements SecuritySetupDao
{
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private ContactDao contactDao;
	
	@Autowired
	private AuthDao authDao;
	
	@Override
	public void initialSetup()
	{
		// Check and make sure there are no risks by performing this operation
		// Get the number of records for several important resources
		// This code will run only for initial setup; it will run if and only if the app has never been used before		
		
		// This method will create an initial administrator contact, user and role
		// and then it will check all the entities and their fields available and save the lists in the db
		// with that information it will create the permissions for the administrator role
		
		int contacts = contactDao.getNumberOfContacts();
		int users = authDao.getNumberOfUsers();
		int roles = authDao.getNumberOfRoles();
		int permissions = authDao.getNumberOfPermissions();
		
		if (users == 0 && roles == 0 && contacts == 0 && permissions == 0)
		{
			Role adminRole = authDao.createAdminRole();			
			
			contactDao.createContact(new Contact("Administrator", null, null, "uec_ops_support@unlimitedcompanies.com"));
			Contact adminContact = contactDao.getContactByEmail("uec_ops_support@unlimitedcompanies.com");
			
			PasswordEncoder pe = new BCryptPasswordEncoder();
			authDao.createUser(new User("administrator", pe.encode("uec123").toCharArray(), adminContact));
			User adminUser = authDao.getUserByUsername("administrator");
			
			authDao.assignUserToRole(adminUser.getUserId(), adminRole.getRoleId());
			
			System.out.println("Created Administrator contact, user and role");
		}
	}
	
	@Override
	public void checkAllResources()
	{
		this.checkResourceList();
		this.checkResourceFieldList();
	}

	@Override
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

	@Override
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
	
	@Override
	public List<String> findAllResourceNames()
	{
		return em.createQuery("select resource.resourceName from Resource resource", String.class)
							  .getResultList();
	}
	
	@Override
	public List<ResourceField> findAllResourceFieldsWithResources()
	{
		return em.createQuery("select field from ResourceField field left join fetch field.resource", ResourceField.class)
							  .getResultList();
	}

	@Override
	public Resource findResourceByName(String name)
	{
		return em.createQuery("select resource from Resource resource where resource.resourceName = :name",
				Resource.class).setParameter("name", name).getSingleResult();
	}
	
	@Override
	public Resource findResourceByNameWithFields(String name)
	{
		return em.createQuery("select resource from Resource resource left join fetch resource.resourceFields where resource.resourceName = :name",
				Resource.class).setParameter("name", name).getSingleResult();
	}
	
	@Override
	public Resource findResourceByNameWithFieldsAndPolicy(String name)
	{
		return em.createQuery("select resource from Resource resource left join fetch resource.resourceFields left join fetch resource.policies where resource.resourceName = :name",
				Resource.class).setParameter("name", name).getSingleResult();
	}

	@Override
	public void registerResource(String resourceName)
	{
		em.createNativeQuery("INSERT INTO resources (resourceName) VALUES (:resourceName)")
				.setParameter("resourceName", resourceName).executeUpdate();
	}

	@Override
	public void registerResourceField(ResourceField resourceField)
	{
		em.createNativeQuery(
				"INSERT INTO resourceFields (resourceFieldName, association, resourceId_FK) VALUES (:field, :association, :resource)")
				.setParameter("field", resourceField.getResourceFieldName())
				.setParameter("association", resourceField.getAssociation())
				.setParameter("resource", resourceField.getResource())
				.executeUpdate();
	}
}
