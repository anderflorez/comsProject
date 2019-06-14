package com.unlimitedcompanies.coms.service.securityImpl;

import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.ContactDao;
import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.domain.abac.ResourceAttribs;
import com.unlimitedcompanies.coms.domain.abac.ResourceReadPolicy;
import com.unlimitedcompanies.coms.domain.abac.UserAttribs;
import com.unlimitedcompanies.coms.domain.employee.Employee;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.abac.SystemService;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotDeletedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.ABACService;
import com.unlimitedcompanies.coms.service.security.ContactService;

@Service
@Transactional
public class ContactServiceImpl implements ContactService
{
	@Autowired
	private ContactDao contactDao;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private ABACService abacService;

	@Override
	public void saveContact(Contact contact, String signedUsername) throws DuplicateRecordException, NoResourceAccessException
	{
		Resource contactResource = abacService.searchResourceByName("Contact");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy contactPolicy = systemService.searchPolicy(contactResource, PolicyType.UPDATE);;
		UserAttribs userAttribs = systemService.getUserAttribs(signedUser.getUserId());
		
		// TODO: Add the projects that are associated to the user
		
		if (contactPolicy.getModifyPolicy(null, userAttribs, signedUser) && contactPolicy.getCdPolicy().isCreatePolicy())
		{
			List<String> restrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), contactResource.getResourceId());
			if (restrictedFields.size() > 0)
			{
				contact.cleanRestrictedFields(restrictedFields);
			}
			
			try
			{
				contactDao.createContact(contact);
				contactDao.clearEntityManager();
			} 
			catch (ConstraintViolationException e)
			{
				if (e.getConstraintName() != null && e.getConstraintName().endsWith("_UNIQUE"))
				{
					throw new DuplicateRecordException();
				}
				throw e;
			}
		}
		else
		{
			throw new NoResourceAccessException();
		}
		
	}

	@Override
	public int findNumberOfContacts()
	{
		return contactDao.getNumberOfContacts();
	}
	
	@Override
	public int findNumberOfContactAddresses()
	{
		return contactDao.getNumberOfAddresses();
	}
	
	@Override
	public int findNumberOfContactPhones()
	{
		return contactDao.getNumberOfContactPhones();
	}
	
	@Override
	public List<Contact> searchAllContacts(String signedUsername) throws NoResourceAccessException
	{
		Resource contactResource = abacService.searchResourceByName("Contact");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		AbacPolicy contactPolicy;
		try
		{
			contactPolicy = systemService.searchPolicy(contactResource, PolicyType.READ);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceReadPolicy resourceReadPolicy = contactPolicy.getReadPolicy("contact", "project", signedUser);
		if (resourceReadPolicy.isReadGranted())
		{
			List<Contact> contacts = contactDao.getAllContacts(resourceReadPolicy.getReadConditions());
			contactDao.clearEntityManager();
			
			// Check if there are any restricted fields for the requesting user
			List<String> restrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), contactResource.getResourceId());
			if (restrictedFields.size() > 0)
			{
				for (Contact contact : contacts)
				{
					contact.cleanRestrictedFields(restrictedFields);
				}
			}
			
			return contacts;
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public List<Contact> searchContactsByRange(int elements, int page, String signedUsername) throws NoResourceAccessException
	{
		Resource contactResource = abacService.searchResourceByName("Contact");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		AbacPolicy contactPolicy;
		try
		{
			contactPolicy = systemService.searchPolicy(contactResource, PolicyType.READ);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceReadPolicy resourceReadPolicy = contactPolicy.getReadPolicy("contact", "project", signedUser);
		if (resourceReadPolicy.isReadGranted())
		{
			List<Contact> contacts = contactDao.getContactsByRange(elements, page - 1, resourceReadPolicy.getReadConditions());
			contactDao.clearEntityManager();
			
			// Check if there are any restricted fields for the requesting user
			List<String> restrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), contactResource.getResourceId());
			if (restrictedFields.size() > 0)
			{
				for (Contact contact : contacts)
				{
					contact.cleanRestrictedFields(restrictedFields);
				}
			}
			
			return contacts;
		}
		else
		{
			throw new NoResourceAccessException();
		}
		
	}
	
//	@Override
//	public boolean hasNextContact(int page, int elements)
//	{
//		List<Contact> foundContacts = contactDao.getContactsByRange((page - 1) * elements, 1);
//		if (foundContacts.isEmpty()) 
//		{
//			return false;
//		}
//		else 
//		{
//			return true;
//		}
//	}
		
	@Override
	@Transactional(rollbackFor = {RecordNotFoundException.class})
	public Contact searchContactById(int id, String signedUsername) throws RecordNotFoundException, NoResourceAccessException
	{
		Resource contactResource = abacService.searchResourceByName("Contact");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		AbacPolicy contactPolicy;
		try
		{
			contactPolicy = systemService.searchPolicy(contactResource, PolicyType.READ);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceReadPolicy resourceReadPolicy = contactPolicy.getReadPolicy("contact", "project", signedUser);
		if (resourceReadPolicy.isReadGranted())
		{
			try
			{
				Contact contact = contactDao.getContactById(id, resourceReadPolicy.getReadConditions());
				contactDao.clearEntityManager();
				
				// Check if there are any restricted fields for the requesting user
				List<String> restrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), contactResource.getResourceId());
				if (restrictedFields.size() > 0)
				{
					contact.cleanRestrictedFields(restrictedFields);
				}
				
				return contact;
			} 
			catch (NoResultException e)
			{
				throw new RecordNotFoundException("The contact could not be found");
			}	
		}
		else
		{
			throw new NoResourceAccessException();
		}		
	}
	
	@Override
	public Contact searchContactByCharId(String charId, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		Resource contactResource = abacService.searchResourceByName("Contact");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy contactPolicy;
		try
		{
			contactPolicy = systemService.searchPolicy(contactResource, PolicyType.READ);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceReadPolicy resourceReadPolicy = contactPolicy.getReadPolicy("contact", "project", signedUser);
		if (resourceReadPolicy.isReadGranted())
		{
			try
			{
				Contact contact = contactDao.getContactByCharId(charId, resourceReadPolicy.getReadConditions());
				contactDao.clearEntityManager();
				
				// Check if there are any restricted fields for the requesting user
				List<String> restrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), contactResource.getResourceId());
				if (restrictedFields.size() > 0)
				{
					contact.cleanRestrictedFields(restrictedFields);
				}
								
				return contact;
			}
			catch (NoResultException e)
			{
				throw new RecordNotFoundException("The contact could not be found");
			}
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}

	@Override
	public Contact searchContactByEmail(String email, String signedUsername) throws NoResourceAccessException, RecordNotFoundException
	{
		Resource contactResource = abacService.searchResourceByName("Contact");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy contactPolicy;
		try
		{
			contactPolicy = systemService.searchPolicy(contactResource, PolicyType.READ);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceReadPolicy resourceReadPolicy = contactPolicy.getReadPolicy("contact", "project", signedUser);
		if (resourceReadPolicy.isReadGranted())
		{
			try
			{
				Contact contact = contactDao.getContactByEmail(email, resourceReadPolicy.getReadConditions());
				contactDao.clearEntityManager();
				
				// Check if there are any restricted fields for the requesting user
				List<String> restrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), contactResource.getResourceId());
				if (restrictedFields.size() > 0)
				{
					contact.cleanRestrictedFields(restrictedFields);
				}
				
				return contact;
			}
			catch (NoResultException e)
			{
				throw new RecordNotFoundException("The contact could not be found");
			}
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateContact(Contact contact, String signedUsername) throws NoResourceAccessException 
	{
		Resource contactResource = abacService.searchResourceByName("Contact");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy contactPolicy;
		try
		{
			contactPolicy = systemService.searchPolicy(contactResource, PolicyType.UPDATE);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceAttribs resourceAttribs = this.getContactResourceAttribs(contact.getContactId());
		UserAttribs userAttribs = systemService.getUserAttribs(signedUser.getUserId());
		
		if (contactPolicy.getModifyPolicy(resourceAttribs, userAttribs, signedUser))
		{
			// Check if there are any restricted fields for the requesting user
			List<String> restrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), contactResource.getResourceId());
			if (restrictedFields.size() > 0)
			{
				Contact foundContact = contactDao.getContactById(contact.getContactId(), null);
				contact.cleanRestrictedFields(restrictedFields, foundContact);
			}
			
			contactDao.updateContact(contact);
			contactDao.clearEntityManager();
		}
		else
		{
			throw new NoResourceAccessException();
		}
		
	}
	
	@Override
	public void removeAddress(Contact contact, String signedUsername) throws NoResourceAccessException
	{
		Resource contactResource = abacService.searchResourceByName("Contact");
		User signedUser = systemService.searchFullUserByUsername(signedUsername);
		
		AbacPolicy contactPolicy;
		try
		{
			contactPolicy = systemService.searchPolicy(contactResource, PolicyType.UPDATE);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceAttribs resourceAttribs = this.getContactResourceAttribs(contact.getContactId());
		UserAttribs userAttribs = systemService.getUserAttribs(signedUser.getUserId());
		
		if (contactPolicy.getModifyPolicy(resourceAttribs, userAttribs, signedUser))
		{
			// Check if there are any restricted fields for the requesting user
			List<String> restrictedFields = systemService.searchRestrictedFields(signedUser.getUserId(), contactResource.getResourceId());
			if (restrictedFields.size() > 0)
			{
				Contact foundContact = contactDao.getContactById(contact.getContactId(), null);
				contact.cleanRestrictedFields(restrictedFields, foundContact);
			}
			
			if (contact.getAddress() != null)
			{
				contactDao.deleteAddress(contact.getAddress());
				contact.removeAddress();
				contactDao.clearEntityManager();
			}
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}

	@Override
	@Transactional(rollbackFor = {RecordNotFoundException.class, RecordNotDeletedException.class, NoResourceAccessException.class})
	public void deleteContact(Contact contact, String username) throws RecordNotDeletedException, NoResourceAccessException
	{
		Resource contactResource = abacService.searchResourceByName("Contact");
		User user = systemService.searchFullUserByUsername(username);
		
		AbacPolicy contactPolicy;
		try
		{
			contactPolicy = systemService.searchPolicy(contactResource, PolicyType.UPDATE);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceAttribs resourceAttribs = this.getContactResourceAttribs(contact.getContactId());
		UserAttribs userAttribs = systemService.getUserAttribs(user.getUserId());
		
		if (contactPolicy.getCdPolicy().isDeletePolicy() && contactPolicy.getModifyPolicy(resourceAttribs, userAttribs, user))
		{
			try
			{
				contactDao.deleteContact(contact);
			} 
			catch (IllegalArgumentException e)
			{
				throw new RecordNotDeletedException("The contact provided to be deleted is an illegal entity");
			}
			
			// TODO: Possibly create a checking method to ensure the record was deleted
//			if (contactDao.existingContact(contactId))
//			{
//				throw new RecordNotDeletedException("The contact could not be deleted");
//			}
		}
		
	}
	
	private ResourceAttribs getContactResourceAttribs(int contactId)
	{
		Contact contact = contactDao.getContactWithFullEmployee(contactId);
		Employee employee = contact.getEmployee();

		ResourceAttribs resourceAttribs = new ResourceAttribs();
		
		if (employee != null)
		{
			resourceAttribs.setProjectManagers(employee.getPmProjectNames());
			resourceAttribs.setProjectSuperintendents(employee.getSuperintendentProjectNames());
			resourceAttribs.setProjectForemen(employee.getForemanProjectNames());
		}
		
		return resourceAttribs;

	}
}
