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
import com.unlimitedcompanies.coms.domain.abac.ResourceReadPolicy;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotDeletedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.ABACService;
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
	private ABACService abacService;
	
	@Autowired
	private SecuritySetupService securitySetupService;

	@Override
	@Transactional(rollbackFor = {DuplicateRecordException.class})
	public Contact saveContact(Contact contact) throws DuplicateRecordException
	{
		try
		{
			contactDao.createContact(contact);
			return contactDao.getContactByCharId(contact.getContactCharId());
		} 
		catch (ConstraintViolationException e)
		{
			if (e.getConstraintName().endsWith("_UNIQUE"))
			{
				throw new DuplicateRecordException();
			}
			return null;
		}
	}
	
	@Override
	public Contact saveContact(Contact contact, String username) throws DuplicateRecordException, NoResourceAccessException
	{
		Resource contactResource = securitySetupService.findResourceByName("Contact");
		User user = authenticationDao.getFullUserByUsername(username);
		ABACPolicy contactPolicy = abacService.findPolicy(contactResource, PolicyType.UPDATE, "system");
		
		
		
		
		// TODO: This has nothing to do with read, change this for a method that returns the whether there is permission to create the resource or not
		ResourceReadPolicy resourceReadPolicy = contactPolicy.getReadPolicy("contact", "project", "user", user);
		
		if (resourceReadPolicy.isReadGranted() && contactPolicy.getCdPolicy().isCreatePolicy())
		{
			try
			{
				contactDao.createContact(contact);
				return contactDao.getContactByCharId(contact.getContactCharId());
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
	
	@Override
	public boolean hasNextContact(int page, int elements)
	{
		List<Contact> foundContacts = contactDao.getContactsByRange((page - 1) * elements, 1);
		if (foundContacts.isEmpty()) 
		{
			return false;
		}
		else 
		{
			return true;
		}
	}
	
	@Override
	public List<Contact> searchAllContacts(String username) throws NoResourceAccessException
	{
		User user = authenticationDao.getFullUserByUsername(username);
		ABACPolicy contactPolicy = abacService.findPolicyByName("ContactRead");
		ResourceReadPolicy resourceReadPolicy = contactPolicy.getReadPolicy("contact", "project", "user", user);
		
		if (resourceReadPolicy.isReadGranted())
		{
			return contactDao.getAllContacts(resourceReadPolicy.getReadConditions());
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	// TODO check this method functionality
//	@Override
//	public List<Contact> searchAllContacts()
//	{
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		User loggedUser = null;
//		if (!(authentication instanceof AnonymousAuthenticationToken))
//		{
//			String currentUserName = authentication.getName();
//			loggedUser = authenticationDao.getUserByUsernameWithContact(currentUserName);
//		}
//		return contactDao.getAllContacts(loggedUser);
//	}

	@Override
	public List<Contact> searchContactsByRange(int page, int elements)
	{
		return contactDao.getContactsByRange(page - 1, elements);
	}
		
	@Override
	@Transactional(rollbackFor = {RecordNotFoundException.class})
	public Contact searchContactById(int id) throws RecordNotFoundException
	{
		Contact contact = null;
		try
		{
			contact = contactDao.getContactById(id);
		} 
		catch (NoResultException e)
		{
			throw new RecordNotFoundException("The contact could not be found");
		}
		return contact;
	}
	
	@Override
	public Contact searchContactByEmail(String email)
	{
		return contactDao.getContactByEmail(email);
	}

	@Override
	public Contact searchContactByEmail(String email, String username) throws NoResourceAccessException
	{
		User user = authenticationDao.getFullUserByUsername(username);
		ABACPolicy contactPolicy = abacService.findPolicyByName("ContactRead");
		ResourceReadPolicy resourceReadPolicy = contactPolicy.getReadPolicy("contact", "project", "user", user);
		
		if (resourceReadPolicy.isReadGranted())
		{
			return contactDao.getContactByEmail(email, resourceReadPolicy.getReadConditions());
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}
	
	@Override
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public Contact updateContact(Contact updatedContact) throws RecordNotFoundException
	{
		contactDao.updateContact(updatedContact);
		return this.searchContactById(updatedContact.getContactId());
	}
	
	@Override
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public Contact updateContact(int contactId, 
								 String firstName, 
								 String middleName, 
								 String lastName, 
								 String email, 
								 String username) throws RecordNotFoundException, NoResourceAccessException
	{
		User user = authenticationDao.getFullUserByUsername(username);
		Resource resource = securitySetupService.findResourceByName("Contact");
		ABACPolicy policy = abacService.findPolicy(resource, PolicyType.UPDATE, username);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// TODO: Use the update policy instead of the read policy
		ResourceReadPolicy resourceReadPolicy = policy.getReadPolicy("contact", "project", "user", user);
		
		if (resourceReadPolicy.isReadGranted())
		{
			contactDao.updateContact(contactId, firstName, middleName, lastName, email);
			return this.searchContactById(contactId);
		}
		else
		{
			throw new NoResourceAccessException();
		}
	}

	@Override
	@Transactional(rollbackFor = {RecordNotFoundException.class, RecordNotDeletedException.class})
	public void deleteContact(int contactId) throws RecordNotFoundException, RecordNotDeletedException
	{
		try
		{
			contactDao.deleteContact(contactId);
		} 
		catch (NoResultException e)
		{
			throw new RecordNotFoundException("The contact you are trying to delete could not be found");
		}
		
		if (contactDao.existingContact(contactId))
		{
			throw new RecordNotDeletedException("The contact could not be deleted");
		}
	}

//	@Override
//	public int findNumberOfContactAddresses()
//	{
//		return contactDao.getNumberOfAddresses();
//	}
//
//	@Override
//	public void saveContactAddress(Address address)
//	{
//		int contactId = address.getContact().getContactId();
//		contactDao.createContactAddress(address, contactId);
//	}
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
