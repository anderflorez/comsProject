package com.unlimitedcompanies.coms.securityServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.ContactDao;
import com.unlimitedcompanies.coms.domain.security.Address;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Phone;
import com.unlimitedcompanies.coms.securityService.ContactService;

@Service
@Transactional
public class ContactServiceImpl implements ContactService
{
	@Autowired
	ContactDao dao;

	@Override
	public int findNumberOfContacts()
	{
		return dao.getNumberOfContacts();
	}
	
	@Override
	public List<Contact> findAllContacts()
	{
		return dao.getAllContacts();
	}

	@Override
	public void saveContact(Contact contact)
	{
		dao.createContact(contact);
		
	}

	@Override
	public Contact findContactByEmail(String email)
	{
		return dao.searchContactByEmail(email);
	}

	@Override
	public Contact findContactById(int id)
	{
		return dao.searchContactById(id);
	}
	
	@Override
	public void updateContact(int id, Contact updatedContact)
	{
		dao.updateContact(id, updatedContact);
	}

	@Override
	public void deleteContact(Contact contact)
	{
		dao.removeContact(contact.getContactId());
	}

	@Override
	public int findNumberOfContactAddresses()
	{
		return dao.getNumberOfAddresses();
	}

	@Override
	public void saveContactAddress(Address address)
	{
		int contactId = address.getContact().getContactId();
		dao.createContactAddress(address, contactId);
	}

	@Override
	public List<Address> findContactAddressesByZipCode(String zipCode)
	{
		return dao.searchContactAddressByZipCode(zipCode);
	}

	@Override
	public Address findContactAddressById(int addressId)
	{
		return dao.searchContactAddressById(addressId);
	}

	@Override
	public int findNumberOfContacPhones()
	{
		return dao.getNumberOfContactPhones();
	}

	@Override
	public void saveContactPhone(Phone phone)
	{
		int contactId = phone.getContact().getContactId();
		dao.createContactPhone(phone, contactId);
	}

	@Override
	public List<Phone> findContactPhoneByNumber(String phoneNumber)
	{
		return dao.searchContactPhonesByNumber(phoneNumber);
	}

	@Override
	public Phone findContactPhoneById(int phoneId)
	{
		return dao.searchContactPhoneById(phoneId);
	}
}
