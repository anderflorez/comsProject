package com.unlimitedcompanies.coms.service.securityImpl;

import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.AuthDao;
import com.unlimitedcompanies.coms.dao.security.ContactDao;
import com.unlimitedcompanies.coms.domain.abac.ABACPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.ResourceAttribs;
import com.unlimitedcompanies.coms.domain.abac.ResourceReadPolicy;
import com.unlimitedcompanies.coms.domain.abac.UserAttribs;
import com.unlimitedcompanies.coms.domain.employee.Employee;
import com.unlimitedcompanies.coms.domain.security.Address;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.abac.SystemAbacService;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotDeletedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.ContactService;
import com.unlimitedcompanies.coms.service.security.SecuritySetupService;

@Service
@Transactional
public class ContactServiceImpl implements ContactService
{
	@Autowired
	private ContactDao contactDao;
	
	@Autowired
	private AuthDao authenticationDao;
	
	@Autowired
	private SystemAbacService systemAbacService;
	
	@Autowired
	private SecuritySetupService securitySetupService;

	@Override
	public void saveContact(Contact contact, String username) throws DuplicateRecordException, NoResourceAccessException
	{
		Resource contactResource = securitySetupService.findResourceByName("Contact");
		User user = authenticationDao.getFullUserByUsername(username);	
		
		ABACPolicy contactPolicy;
		try
		{
			contactPolicy = systemAbacService.findPolicy(contactResource, PolicyType.UPDATE);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		UserAttribs userAttribs = new UserAttribs(username);
		userAttribs.setRoles(user.getRoleNames());
		
		// TODO: Add the projects that are associated to the user
		
		if (contactPolicy.getModifyPolicy(null, userAttribs, user) && contactPolicy.getCdPolicy().isCreatePolicy())
		{
			try
			{
				contactDao.createContact(contact);
				contactDao.clearEntityManager();
			} 
			catch (ConstraintViolationException e)
			{
				if (e.getConstraintName().endsWith("_UNIQUE"))
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
	
	@Override
	public List<Contact> searchAllContacts(String username) throws NoResourceAccessException
	{
		Resource contactResource = securitySetupService.findResourceByName("Contact");
		User user = authenticationDao.getFullUserByUsername(username);
		ABACPolicy contactPolicy;
		try
		{
			contactPolicy = systemAbacService.findPolicy(contactResource, PolicyType.READ);
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
		Resource contactResource = securitySetupService.findResourceByName("Contact");
		User user = authenticationDao.getFullUserByUsername(username);
		ABACPolicy contactPolicy;
		try
		{
			contactPolicy = systemAbacService.findPolicy(contactResource, PolicyType.READ);
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
		Resource contactResource = securitySetupService.findResourceByName("Contact");
		User user = authenticationDao.getFullUserByUsername(username);
		ABACPolicy contactPolicy;
		try
		{
			contactPolicy = systemAbacService.findPolicy(contactResource, PolicyType.READ);
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
		Resource contactResource = securitySetupService.findResourceByName("Contact");
		User user = authenticationDao.getFullUserByUsername(username);
		
		ABACPolicy contactPolicy;
		try
		{
			contactPolicy = systemAbacService.findPolicy(contactResource, PolicyType.READ);
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
		Resource contactResource = securitySetupService.findResourceByName("Contact");
		User user = authenticationDao.getFullUserByUsername(username);
		
		ABACPolicy contactPolicy;
		try
		{
			contactPolicy = systemAbacService.findPolicy(contactResource, PolicyType.READ);
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
		Resource contactResource = securitySetupService.findResourceByName("Contact");
		User user = authenticationDao.getFullUserByUsername(username);
		
		ABACPolicy contactPolicy;
		try
		{
			contactPolicy = systemAbacService.findPolicy(contactResource, PolicyType.UPDATE);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceAttribs resourceAttribs = this.getContactResourceAttribs(contact.getContactId());
		UserAttribs userAttribs = systemAbacService.getUserAttribs(user.getUserId());
		
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
		Resource contactResource = securitySetupService.findResourceByName("Contact");
		User user = authenticationDao.getFullUserByUsername(username);
		
		ABACPolicy contactPolicy;
		try
		{
			contactPolicy = systemAbacService.findPolicy(contactResource, PolicyType.UPDATE);
		}
		catch (NoResultException e)
		{
			throw new NoResourceAccessException();
		}
		
		ResourceAttribs resourceAttribs = this.getContactResourceAttribs(contact.getContactId());
		UserAttribs userAttribs = systemAbacService.getUserAttribs(user.getUserId());
		
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
		Resource contactAddressResource = securitySetupService.findResourceByName("Address");
		User user = authenticationDao.getFullUserByUsername(username);
		
		ABACPolicy contactAddressPolicy;
		try
		{
			contactAddressPolicy = systemAbacService.findPolicy(contactAddressResource, PolicyType.UPDATE);
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
		Resource addressResource = securitySetupService.findResourceByName("Address");
		User user = authenticationDao.getFullUserByUsername(username);
		
		ABACPolicy addressPolicy;
		try
		{
			addressPolicy = systemAbacService.findPolicy(addressResource, PolicyType.READ);
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
		Resource addressResource = securitySetupService.findResourceByName("Address");
		User user = authenticationDao.getFullUserByUsername(username);
		
		ABACPolicy addressPolicy;
		try
		{
			addressPolicy = systemAbacService.findPolicy(addressResource, PolicyType.READ);
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
		Resource addressResource = securitySetupService.findResourceByName("Address");
		User user = authenticationDao.getFullUserByUsername(username);
		
		ABACPolicy addressPolicy;
		try
		{
			addressPolicy = systemAbacService.findPolicy(addressResource, PolicyType.READ);
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
}
