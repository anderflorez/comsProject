package com.unlimitedcompanies.coms.dao.security;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.Address;
import com.unlimitedcompanies.coms.domain.security.Contact;

public interface ContactDao
{
	public void createContact(Contact contact);
	public int getNumberOfContacts();
//	boolean existingContact(int contactId);
	public List<Contact> getAllContacts(String policyAccessConditions);
	public List<Contact> getContactsByRange(int elements, int page, String policyAccessConditions);
	public Contact getContactById(int id, String policyAccessConditions);
	public Contact getContactByCharId(String contactCharId, String accessConditions);
	public Contact getContactByEmail(String email, String accessConditions);
	public Contact getContactWithFullEmployee(int contactId);
	public void updateContact(Contact contact);
	public void deleteContact(Contact contact);
	
	public void createContactAddress(Address address);
	public int getNumberOfAddresses();
	public List<Address> getAllAddresses(String accessConditions);
	public Address getContactAddress(Contact contact, String readConditions);
	public Address getContactAddressById(int id, String readConditions);
//	public List<Address> searchContactAddressByZipCode(String zipCode);
//	
//	public int getNumberOfContactPhones();
//	public void createContactPhone(Phone phone, int contactId);
//	public List<Phone> searchContactPhonesByNumber(String phoneNumber);
//	public Phone searchContactPhoneById(int id);
	
	public void clearEntityManager();
}
