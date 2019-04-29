package com.unlimitedcompanies.coms.service.security;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotDeletedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;

public interface ContactService
{
	public Contact saveContact(Contact Contact) throws DuplicateRecordException;
	public int findNumberOfContacts();
	public boolean hasNextContact(int page, int elements);
	public List<Contact> searchAllContacts(String username) throws NoResourceAccessException;
	public List<Contact> searchContactsByRange(int page, int elements);
	public Contact searchContactById(int id) throws RecordNotFoundException;
	public Contact searchContactByEmail(String email);
	public Contact searchContactByEmail(String email, String username) throws NoResourceAccessException;
	public Contact updateContact(Contact updatedContact) throws RecordNotFoundException;
	public Contact updateContact(int contactId, String firstName, String middleName, String lastName, String email, String username) 
			throws RecordNotFoundException, NoResourceAccessException;
	public void deleteContact(int contactId) throws RecordNotFoundException, RecordNotDeletedException;
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
