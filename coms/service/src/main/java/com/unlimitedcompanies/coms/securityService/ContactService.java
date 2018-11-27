package com.unlimitedcompanies.coms.securityService;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.securityServiceExceptions.ContactNotFoundException;

public interface ContactService
{
	public Contact saveContact(Contact Contact);
	public int findNumberOfContacts();
	public List<Contact> searchAllContacts();
	public Contact searchContactById(int id) throws ContactNotFoundException;
	public Contact searchContactByEmail(String email);
	public void updateContact(int id, Contact updatedContact);
	public void deleteContact(int contactId);
//	
//	public int findNumberOfContactAddresses();
//	public void saveContactAddress(Address address);
//	public List<Address> findContactAddressesByZipCode(String zipCode);
//	public Address findContactAddressById(int addressId);
//	
//	public int findNumberOfContacPhones();
//	public void saveContactPhone(Phone phone);
//	public List<Phone> findContactPhoneByNumber(String phoneNumber);
//	public Phone findContactPhoneById(int phoneId);
	
}
