package com.unlimitedcompanies.coms.securityServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.AuthDao;
import com.unlimitedcompanies.coms.dao.security.ContactDao;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.securityService.ContactService;

@Service
@Transactional
public class ContactServiceImpl implements ContactService
{
	@Autowired
	ContactDao dao;
	
	@Autowired
	AuthDao authenticationDao;

	@Override
	public Contact saveContact(Contact contact)
	{
		dao.createContact(contact);
		return dao.getContactById(contact.getContactId());
	}

	@Override
	public int findNumberOfContacts()
	{
		return dao.getNumberOfContacts();
	}
	
	@Override
	public List<Contact> searchAllContacts()
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User loggedUser = null;
		if (!(authentication instanceof AnonymousAuthenticationToken))
		{
			String currentUserName = authentication.getName();
			loggedUser = authenticationDao.getUserByUsernameWithContact(currentUserName);
		}
		return dao.getAllContacts(loggedUser);
	}
	
	@Override
	public Contact searchContactById(String id)
	{
		return dao.getContactById(id);
	}

	@Override
	public Contact searchContactByEmail(String email)
	{
		return dao.getContactByEmail(email);
	}
	
	@Override
	public void updateContact(String id, Contact updatedContact)
	{
		dao.updateContact(id, updatedContact);
	}

	@Override
	public void deleteContact(String contactId)
	{
		dao.deleteContact(contactId);
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
