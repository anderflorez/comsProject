package com.unlimitedcompanies.coms.service.securityImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.AuthDao;
import com.unlimitedcompanies.coms.dao.security.ContactDao;
import com.unlimitedcompanies.coms.data.abac.ABACPolicy;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotDeletedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.ABACService;
import com.unlimitedcompanies.coms.service.security.ContactService;

@Service
@Transactional
public class ContactServiceImpl implements ContactService
{
	@Autowired
	private ContactDao dao;
	
	@Autowired
	private AuthDao authenticationDao;
	
	@Autowired
	private ABACService abacService;

	@Override
	@Transactional(rollbackFor = {DuplicateRecordException.class})
	public Contact saveContact(Contact contact) throws DuplicateRecordException
	{
		try
		{
			dao.createContact(contact);
			return dao.getContactByCharId(contact.getContactCharId());
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
	public int findNumberOfContacts()
	{
		return dao.getNumberOfContacts();
	}
	
	@Override
	public boolean hasNextContact(int page, int elements)
	{
		List<Contact> foundContacts = dao.getContactsByRange((page - 1) * elements, 1);
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
	public List<Contact> searchAllContacts()
	{
		return dao.getAllContacts();
	}
	
	@Override
	public List<Contact> searchAllContacts(String username)
	{
		User user = authenticationDao.getFullUserByUsername(username);
		ABACPolicy contactPolicy = abacService.findPolicyByName("ContactRead");
		if (contactPolicy.entityPoliciesGrant(user))
		{
			return dao.getMultipleContacts();
		}
		else
		{
			// TODO: throw an exception as the user is not authorize to access the resource
			
			return null;
		}
	}
	
	@Override
	public List<Contact> searchContactsByRange(int page, int elements)
	{
		return dao.getContactsByRange(page - 1, elements);
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
//		return dao.getAllContacts(loggedUser);
//	}
	
	@Override
	@Transactional(rollbackFor = {RecordNotFoundException.class})
	public Contact searchContactById(int id) throws RecordNotFoundException
	{
		Contact contact = null;
		try
		{
			contact = dao.getContactById(id);
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
		return dao.getContactByEmail(email);
	}

	@Override
	public Contact searchContactByEmail(String email, String username)
	{
		User user = authenticationDao.getFullUserByUsername(username);
		ABACPolicy contactPolicy = abacService.findPolicyByName("ContactRead");
		if (contactPolicy.entityPoliciesGrant(user))
		{
			Map<String, String> conditions = new HashMap<>();
			conditions.put("email", email);
			return dao.getOneContact(conditions);
		}
		else
		{
			// TODO: throw an exception as the user is not authorize to access the resource
			
			return null;
		}
	}
	
	@Override
	@Transactional(rollbackFor = RecordNotFoundException.class)
	public Contact updateContact(Contact updatedContact) throws RecordNotFoundException
	{
		dao.updateContact(updatedContact);
		return this.searchContactById(updatedContact.getContactId());
	}

	@Override
	@Transactional(rollbackFor = {RecordNotFoundException.class, RecordNotDeletedException.class})
	public void deleteContact(int contactId) throws RecordNotFoundException, RecordNotDeletedException
	{
		try
		{
			dao.deleteContact(contactId);
		} 
		catch (NoResultException e)
		{
			throw new RecordNotFoundException("The contact you are trying to delete could not be found");
		}
		
		if (dao.existingContact(contactId))
		{
			throw new RecordNotDeletedException("The contact could not be deleted");
		}
	}

//	@Override
//	public int findNumberOfContactAddresses()
//	{
//		return dao.getNumberOfAddresses();
//	}
//
//	@Override
//	public void saveContactAddress(Address address)
//	{
//		int contactId = address.getContact().getContactId();
//		dao.createContactAddress(address, contactId);
//	}
//
//	@Override
//	public List<Address> findContactAddressesByZipCode(String zipCode)
//	{
//		return dao.searchContactAddressByZipCode(zipCode);
//	}
//
//	@Override
//	public Address findContactAddressById(int addressId)
//	{
//		return dao.searchContactAddressById(addressId);
//	}
//
//	@Override
//	public int findNumberOfContacPhones()
//	{
//		return dao.getNumberOfContactPhones();
//	}
//
//	@Override
//	public void saveContactPhone(Phone phone)
//	{
//		int contactId = phone.getContact().getContactId();
//		dao.createContactPhone(phone, contactId);
//	}
//
//	@Override
//	public List<Phone> findContactPhoneByNumber(String phoneNumber)
//	{
//		return dao.searchContactPhonesByNumber(phoneNumber);
//	}
//
//	@Override
//	public Phone findContactPhoneById(int phoneId)
//	{
//		return dao.searchContactPhoneById(phoneId);
//	}
}
