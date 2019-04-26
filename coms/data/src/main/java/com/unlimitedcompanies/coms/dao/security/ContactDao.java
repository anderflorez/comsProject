package com.unlimitedcompanies.coms.dao.security;

import java.util.List;
import java.util.Map;

import com.unlimitedcompanies.coms.data.abac.ABACPolicy;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.User;

public interface ContactDao
{
	public void createContact(Contact contact);
	public int getNumberOfContacts();
	boolean existingContact(int contactId);
	public List<Contact> getAllContacts();
	public List<Contact> getAllContacts(String policyAccessConditions);
	public List<Contact> getContactsByRange(int page, int elements);
	public Contact getContactById(int id);
	public Contact getContactByCharId(String contactCharId);
	public Contact getContactByEmail(String email);
	public Contact getContactByEmail(String email, String policyConditions);
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
