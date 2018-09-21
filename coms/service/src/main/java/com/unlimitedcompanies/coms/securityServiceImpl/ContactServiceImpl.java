package com.unlimitedcompanies.coms.securityServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.AuthDao;
import com.unlimitedcompanies.coms.dao.security.ContactDao;
import com.unlimitedcompanies.coms.securityService.ContactService;

@Service
@Transactional
public class ContactServiceImpl implements ContactService
{
	@Autowired
	ContactDao dao;
	
	@Autowired
	AuthDao authenticationDao;

//	@Override
//	public void saveContact(Contact contact)
//	{
//		dao.createContact(contact);		
//	}
//
//	@Override
//	public int findNumberOfContacts()
//	{
//		return dao.getNumberOfContacts();
//	}
//	
//	@Override
//	public List<Contact> findAllContacts()
//	{
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		User loggedUser = null;
//		if (!(authentication instanceof AnonymousAuthenticationToken))
//		{
//			String currentUserName = authentication.getName();
//			loggedUser = authenticationDao.searchUserByUsernameWithContact(currentUserName);
//		}
//		return dao.getAllContacts(loggedUser);
//	}
//
//	@Override
//	public Contact findContactByEmail(String email)
//	{
//		return dao.searchContactByEmail(email);
//	}
//
//	@Override
//	public Contact findContactById(int id)
//	{
//		return dao.searchContactById(id);
//	}
//	
//	@Override
//	public void updateContact(int id, Contact updatedContact)
//	{
//		dao.updateContact(id, updatedContact);
//	}
//
//	@Override
//	public void deleteContact(Contact contact)
//	{
//		dao.removeContact(contact.getContactId());
//	}
//
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
