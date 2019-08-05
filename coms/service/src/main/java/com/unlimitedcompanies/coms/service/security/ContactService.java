package com.unlimitedcompanies.coms.service.security;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.ContactPhone;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotDeletedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;

@Transactional
public interface ContactService
{
	public void saveContact(Contact contact, String username) 
			throws DuplicateRecordException, NoResourceAccessException, RecordNotFoundException;
	public int findNumberOfContacts();
	public int findNumberOfContactAddresses();
	public int findNumberOfContactPhones();
	public List<Contact> searchAllContacts(String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public List<Contact> searchContactsByRange(int elements, int page, String signedUsername) 
			throws NoResourceAccessException, RecordNotFoundException;
	public Contact searchContactById(int id, String signedUsername) throws RecordNotFoundException, NoResourceAccessException;
	public Contact searchContactByCharId(String charId, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public Contact searchContactByEmail(String email, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public Contact searchContactByPhoneId(int phoneId, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
//	public boolean hasNextContact(int page, int elements);
	public void updateContact(Contact contact, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public void removeAddress(Contact contact, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public void removeContactPhone(ContactPhone contactPhone, String signedUsername) throws NoResourceAccessException, RecordNotFoundException;
	public void deleteContact(Contact contact, String signedUsername) 
			throws RecordNotDeletedException, NoResourceAccessException, RecordNotFoundException;
}
