package com.unlimitedcompanies.coms.securityServiceImpl;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.AuthDao;
import com.unlimitedcompanies.coms.dao.security.ContactDao;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.exen.RecordNotFoundException;
import com.unlimitedcompanies.coms.securityService.ContactService;
import com.unlimitedcompanies.coms.securityServiceExceptions.ContactNotDeletedException;
import com.unlimitedcompanies.coms.securityServiceExceptions.ContactNotFoundException;
import com.unlimitedcompanies.coms.securityServiceExceptions.DuplicateContactEntryException;

@Service
@Transactional
public class ContactServiceImpl implements ContactService
{
	@Autowired
	ContactDao dao;
	
	@Autowired
	AuthDao authenticationDao;

	@Override
	@Transactional(rollbackFor = {DuplicateContactEntryException.class})
	public Contact saveContact(Contact contact) throws DuplicateContactEntryException
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
				throw new DuplicateContactEntryException();
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
	public List<Contact> searchAllContacts()
	{
		return dao.getAllContacts();
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
	public Contact searchContactById(int id) throws ContactNotFoundException
	{
		Contact contact = null;
		try
		{
			contact = dao.getContactById(id);
		} catch (RecordNotFoundException e)
		{
			throw new ContactNotFoundException();
		}
		return contact;
	}

	@Override
	public Contact searchContactByEmail(String email)
	{
		return dao.getContactByEmail(email);
	}
	
	@Override
	public Contact updateContact(Contact updatedContact) throws ContactNotFoundException
	{
		try
		{
			dao.updateContact(updatedContact);
			return this.searchContactById(updatedContact.getContactId());
		} 
		catch (RecordNotFoundException e)
		{
			throw new ContactNotFoundException();
		}
	}

	@Override
	public void deleteContact(int contactId) throws ContactNotFoundException, ContactNotDeletedException
	{
		try
		{
			dao.deleteContact(contactId);
		} 
		catch (RecordNotFoundException e)
		{
			throw new ContactNotFoundException();
		}
		
		try
		{
			dao.getContactById(contactId);
			throw new ContactNotDeletedException();
		} 
		catch (RecordNotFoundException e) {}
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
