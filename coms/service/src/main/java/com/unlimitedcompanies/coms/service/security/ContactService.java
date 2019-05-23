package com.unlimitedcompanies.coms.service.security;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.domain.security.Address;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotDeletedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;

@Transactional
public interface ContactService
{
	public void saveContact(Contact contact, String username) throws DuplicateRecordException, NoResourceAccessException;
	public int findNumberOfContacts();
	public List<Contact> searchAllContacts(String username) throws NoResourceAccessException;
	public List<Contact> searchContactsByRange(int elements, int page, String username) throws NoResourceAccessException;
	public Contact searchContactById(int id, String username) throws RecordNotFoundException, NoResourceAccessException;
	public Contact searchContactByCharId(String charId, String username) throws NoResourceAccessException, RecordNotFoundException;
	public Contact searchContactByEmail(String email, String username) throws NoResourceAccessException, RecordNotFoundException;
//	public boolean hasNextContact(int page, int elements);
	public void updateContact(Contact contact, String username) throws NoResourceAccessException;
	public void deleteContact(Contact contact, String username) throws RecordNotDeletedException, NoResourceAccessException;

	public void saveContactAddress(Address address, String username) throws NoResourceAccessException;
	public int findNumberOfContactAddresses();
	public List<Address> searchAllContactAddresses(String username) throws NoResourceAccessException;
	public Address searchContactAddress(Contact contact, String username) throws NoResourceAccessException;
	public Address searchContactAddressById(int id, String username) throws NoResourceAccessException;
//	public List<Address> findContactAddressesByZipCode(String zipCode);
//	
//	public int findNumberOfContacPhones();
//	public void saveContactPhone(Phone phone);
//	public List<Phone> findContactPhoneByNumber(String phoneNumber);
//	public Phone findContactPhoneById(int phoneId);
	
}
