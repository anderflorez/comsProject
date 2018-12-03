package com.unlimitedcompanies.coms.securityService;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.securityServiceExceptions.ContactNotDeletedException;
import com.unlimitedcompanies.coms.securityServiceExceptions.ContactNotFoundException;
import com.unlimitedcompanies.coms.securityServiceExceptions.DuplicateContactEntryException;

public interface ContactService
{
	public Contact saveContact(Contact Contact) throws DuplicateContactEntryException;
	public int findNumberOfContacts();
	public List<Contact> searchAllContacts();
	public List<Contact> searchContactsByRange(int page, int elements);
	public Contact searchContactById(int id) throws ContactNotFoundException;
	public Contact searchContactByEmail(String email);
	public Contact updateContact(Contact updatedContact) throws ContactNotFoundException;
	public void deleteContact(int contactId) throws ContactNotFoundException, ContactNotDeletedException;
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
