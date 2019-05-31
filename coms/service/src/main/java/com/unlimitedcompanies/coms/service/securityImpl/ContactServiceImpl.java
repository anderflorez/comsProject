package com.unlimitedcompanies.coms.service.securityImpl;

import java.lang.reflect.Field;
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
import com.unlimitedcompanies.coms.domain.security.Address;
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
	public void saveContact(Contact contact, String username) throws DuplicateRecordException, NoResourceAccessException
	{
		Resource contactResource = abacService.searchResourceByName("Contact");
		User user = systemService.searchFullUserByUsername(username);
		
		AbacPolicy contactPolicy = systemService.searchPolicy(contactResource, PolicyType.UPDATE);;
		UserAttribs userAttribs = systemService.getUserAttribs(user.getUserId());
		
		// TODO: Add the projects that are associated to the user
		
		if (contactPolicy.getModifyPolicy(null, userAttribs, user) && contactPolicy.getCdPolicy().isCreatePolicy())
		{
//			List<ResourceField> restrictedFields = systemService.searchRestrictedFields(contactResource, "administrator");
//			contact.cleanRestrictedFields(restrictedFields);
			
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
	public List<Contact> searchAllContacts(String username) throws NoResourceAccessException
	{
		Resource contactResource = abacService.searchResourceByName("Contact");
		User user = systemService.searchFullUserByUsername(username);
		AbacPolicy contactPolicy;
		try
		{
			contactPolicy = systemService.searchPolicy(contactResource, PolicyType.READ);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceReadPolicy resourceReadPolicy = contactPolicy.getReadPolicy("contact", "project", user);
		if (resourceReadPolicy.isReadGranted())
		{
			List<Contact> contacts = contactDao.getAllContacts(resourceReadPolicy.getReadConditions());
			contactDao.clearEntityManager();
			return contacts;
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public List<Contact> searchContactsByRange(int elements, int page, String username) throws NoResourceAccessException
	{
		Resource contactResource = abacService.searchResourceByName("Contact");
		User user = systemService.searchFullUserByUsername(username);
		AbacPolicy contactPolicy;
		try
		{
			contactPolicy = systemService.searchPolicy(contactResource, PolicyType.READ);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceReadPolicy resourceReadPolicy = contactPolicy.getReadPolicy("contact", "project", user);
		if (resourceReadPolicy.isReadGranted())
		{
			List<Contact> contacts = contactDao.getContactsByRange(elements, page - 1, resourceReadPolicy.getReadConditions());
			contactDao.clearEntityManager();
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
	public Contact searchContactById(int id, String username) throws RecordNotFoundException, NoResourceAccessException
	{
		Resource contactResource = abacService.searchResourceByName("Contact");
		User user = systemService.searchFullUserByUsername(username);
		AbacPolicy contactPolicy;
		try
		{
			contactPolicy = systemService.searchPolicy(contactResource, PolicyType.READ);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceReadPolicy resourceReadPolicy = contactPolicy.getReadPolicy("contact", "project", user);
		if (resourceReadPolicy.isReadGranted())
		{
			try
			{
				Contact contact = contactDao.getContactById(id, resourceReadPolicy.getReadConditions());
				contactDao.clearEntityManager();
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
	public Contact searchContactByCharId(String charId, String username) throws NoResourceAccessException, RecordNotFoundException
	{
		Resource contactResource = abacService.searchResourceByName("Contact");
		User user = systemService.searchFullUserByUsername(username);
		
		AbacPolicy contactPolicy;
		try
		{
			contactPolicy = systemService.searchPolicy(contactResource, PolicyType.READ);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceReadPolicy resourceReadPolicy = contactPolicy.getReadPolicy("contact", "project", user);
		if (resourceReadPolicy.isReadGranted())
		{
			try
			{
				Contact contact = contactDao.getContactByCharId(charId, resourceReadPolicy.getReadConditions());
				contactDao.clearEntityManager();
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
	public Contact searchContactByEmail(String email, String username) throws NoResourceAccessException, RecordNotFoundException
	{
		Resource contactResource = abacService.searchResourceByName("Contact");
		User user = systemService.searchFullUserByUsername(username);
		
		AbacPolicy contactPolicy;
		try
		{
			contactPolicy = systemService.searchPolicy(contactResource, PolicyType.READ);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceReadPolicy resourceReadPolicy = contactPolicy.getReadPolicy("contact", "project", user);
		if (resourceReadPolicy.isReadGranted())
		{
			try
			{
				Contact contact = contactDao.getContactByEmail(email, resourceReadPolicy.getReadConditions());
				contactDao.clearEntityManager();
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
	public void updateContact(Contact contact, String username) throws NoResourceAccessException 
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
		
		if (contactPolicy.getModifyPolicy(resourceAttribs, userAttribs, user))
		{
			contactDao.updateContact(contact);
			contactDao.clearEntityManager();
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
	
	@Override
	public void saveContactAddress(Address address, String username) throws NoResourceAccessException
	{
		Resource contactAddressResource = abacService.searchResourceByName("Address");
		User user = systemService.searchFullUserByUsername(username);
		
		AbacPolicy contactAddressPolicy;
		try
		{
			contactAddressPolicy = systemService.searchPolicy(contactAddressResource, PolicyType.UPDATE);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceAttribs resourceAttribs = new ResourceAttribs();
		UserAttribs userAttribs = new UserAttribs(username);
		
		if (contactAddressPolicy.getCdPolicy().isCreatePolicy() && contactAddressPolicy.getModifyPolicy(resourceAttribs, userAttribs, user))
		{
			contactDao.createContactAddress(address);
			contactDao.clearEntityManager();
		}
		
	}

	@Override
	public int findNumberOfContactAddresses()
	{
		return contactDao.getNumberOfAddresses();
	}
	
	@Override
	public List<Address> searchAllContactAddresses(String username) throws NoResourceAccessException
	{
		Resource addressResource = abacService.searchResourceByName("Address");
		User user = systemService.searchFullUserByUsername(username);
		
		AbacPolicy addressPolicy;
		try
		{
			addressPolicy = systemService.searchPolicy(addressResource, PolicyType.READ);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}

		ResourceReadPolicy addressReadPolicy = addressPolicy.getReadPolicy("address", "project", user);
		if (addressReadPolicy.isReadGranted())
		{
			List<Address> addresses = contactDao.getAllAddresses(addressReadPolicy.getReadConditions());
			contactDao.clearEntityManager();
			return addresses;
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public Address searchContactAddress(Contact contact, String username) throws NoResourceAccessException
	{
		Resource addressResource = abacService.searchResourceByName("Address");
		User user = systemService.searchFullUserByUsername(username);
		
		AbacPolicy addressPolicy;
		try
		{
			addressPolicy = systemService.searchPolicy(addressResource, PolicyType.READ);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}

		ResourceReadPolicy addressReadPolicy = addressPolicy.getReadPolicy("address", "project", user);
		if (addressReadPolicy.isReadGranted())
		{
			Address address = contactDao.getContactAddress(contact, addressReadPolicy.getReadConditions());
			contactDao.clearEntityManager();
			return address;
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public Address searchContactAddressById(int id, String username) throws NoResourceAccessException
	{
		Resource addressResource = abacService.searchResourceByName("Address");
		User user = systemService.searchFullUserByUsername(username);
		
		AbacPolicy addressPolicy;
		try
		{
			addressPolicy = systemService.searchPolicy(addressResource, PolicyType.READ);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}

		ResourceReadPolicy addressReadPolicy = addressPolicy.getReadPolicy("address", "project", user);
		if (addressReadPolicy.isReadGranted())
		{
			Address address = contactDao.getContactAddressById(id, addressReadPolicy.getReadConditions());
			contactDao.clearEntityManager();
			return address;
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public void updateAddress(Address address, String username) throws NoResourceAccessException
	{
		Resource addressResource = abacService.searchResourceByName("Address");
		User user = systemService.searchFullUserByUsername(username);
		
		AbacPolicy addressUpdate;
		try
		{
			addressUpdate = systemService.searchPolicy(addressResource, PolicyType.UPDATE);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceAttribs resourceAttribs = this.getContactResourceAttribs(address.getContact().getContactId());
		UserAttribs userAttribs = systemService.getUserAttribs(user.getUserId());
		
		if (addressUpdate.getModifyPolicy(resourceAttribs, userAttribs, user))
		{
			contactDao.updateContactAddress(address);
			contactDao.clearEntityManager();
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	public void deleteContactAddress(Address address, String username) throws NoResourceAccessException, RecordNotDeletedException
	{
		Resource addressResource = abacService.searchResourceByName("Address");
		User user = systemService.searchFullUserByUsername(username);
		
		AbacPolicy addressModifyPolicy;
		try
		{
			addressModifyPolicy = systemService.searchPolicy(addressResource, PolicyType.UPDATE);
		}
		catch (Exception e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceAttribs resourceAttribs = this.getContactResourceAttribs(address.getContact().getContactId());
		UserAttribs userAttribs = systemService.getUserAttribs(user.getUserId());
		
		if (addressModifyPolicy.getModifyPolicy(resourceAttribs, userAttribs, user))
		{
			try
			{
				contactDao.deleteContactAddress(address);
			}
			catch (IllegalArgumentException e)
			{
				throw new RecordNotDeletedException("Contact address provided to be deleted is an illegal entity");
			}
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
//
//	@Override
//	public List<Address> findContactAddressesByZipCode(String zipCode)
//	{
//		return contactDao.searchContactAddressByZipCode(zipCode);
//	}
//
//	@Override
//	public Address findContactAddressById(int addressId)
//	{
//		return contactDao.searchContactAddressById(addressId);
//	}
//
//	@Override
//	public int findNumberOfContacPhones()
//	{
//		return contactDao.getNumberOfContactPhones();
//	}
//
//	@Override
//	public void saveContactPhone(Phone phone)
//	{
//		int contactId = phone.getContact().getContactId();
//		contactDao.createContactPhone(phone, contactId);
//	}
//
//	@Override
//	public List<Phone> findContactPhoneByNumber(String phoneNumber)
//	{
//		return contactDao.searchContactPhonesByNumber(phoneNumber);
//	}
//
//	@Override
//	public Phone findContactPhoneById(int phoneId)
//	{
//		return contactDao.searchContactPhoneById(phoneId);
//	}
	
	
	
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
