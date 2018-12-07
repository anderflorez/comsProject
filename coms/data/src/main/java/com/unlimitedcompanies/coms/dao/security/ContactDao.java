package com.unlimitedcompanies.coms.dao.security;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.Contact;

public interface ContactDao
{
	public int getNumberOfContacts();
	public void createContact(Contact contact);
	public List<Contact> getAllContacts();
//	public List<Contact> getAllContacts(User loggedUser);
	public List<Contact> getContactsByRange(int page, int elements);
	public Contact getContactById(int id);
	public Contact getContactByCharId(String contactCharId);
	public Contact getContactByEmail(String contactEmail);
	public void updateContact(Contact updatedContact);
	public void deleteContact(int contactId);
	
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
