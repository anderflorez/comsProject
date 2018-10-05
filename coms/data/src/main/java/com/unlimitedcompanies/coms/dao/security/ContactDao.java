package com.unlimitedcompanies.coms.dao.security;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.User;

public interface ContactDao
{
	public int getNumberOfContacts();
	public void createContact(Contact contact);
	public List<Contact> getAllContacts(User loggedUser);
	public Contact getContactByEmail(String contactEmail);
	public Contact getContactById(String id);
//	public void updateContact(int id, Contact contact);
//	public void removeContact(int id);
//	
//	public int getNumberOfAddresses();
//	public void createContactAddress(Address address, int contactId);
//	public List<Address> searchContactAddressByZipCode(String zipCode);
//	public Address searchContactAddressById(int id);
//	
//	public int getNumberOfContactPhones();
//	public void createContactPhone(Phone phone, int contactId);
//	public List<Phone> searchContactPhonesByNumber(String phoneNumber);
//	public Phone searchContactPhoneById(int id);
}
