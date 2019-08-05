package com.unlimitedcompanies.coms.dao.security;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.ContactAddress;
import com.unlimitedcompanies.coms.domain.security.ContactPhone;

public interface ContactDao
{
	public void createContact(Contact contact);
	public int getNumberOfContacts();
	public int getNumberOfAddresses();
	public int getNumberOfContactPhones();
//	boolean existingContact(int contactId);
	public List<Contact> getAllContacts(String policyAccessConditions);
	public List<Contact> getContactsByRange(int elements, int page, String policyAccessConditions);
	public Contact getContactById(int id, String policyAccessConditions);
	public Contact getContactByCharId(String contactCharId, String accessConditions);
	public Contact getContactByEmail(String email, String accessConditions);
	public Contact getContactByPhoneId(int phoneId, String readConditions);
	public Contact getContactWithFullEmployee(int contactId);
	public void updateContact(Contact contact);
	public void deleteAddress(ContactAddress address);
	public void deleteContactPhone(ContactPhone contactPhone);
	public void deleteContact(Contact contact);
	


//	
//	public void createContactPhone(ContactPhone phone, int contactId);
//	public List<ContactPhone> searchContactPhonesByNumber(String phoneNumber);
//	public ContactPhone searchContactPhoneById(int id);
}
