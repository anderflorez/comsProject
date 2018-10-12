package com.unlimitedcompanies.coms.securityService;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.Contact;

public interface ContactService
{
	public Contact saveContact(Contact Contact);
	public int findNumberOfContacts();
	public List<Contact> searchAllContacts();
	public Contact searchContactById(String id);
	public Contact searchContactByEmail(String email);
	public void updateContact(String id, Contact updatedContact);
	public void deleteContact(String contactId);
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
