package com.unlimitedcompanies.coms.dao.security;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.Address;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Phone;

public interface ContactDao
{
	public void createContact(Contact contact);
	public int getNumberOfContacts();
	public Contact searchContactByEmail(String contactEmail);
	public Contact searchContactById(int Id);
	public void updateContact(int id, Contact contact);
	public void removeContact(int id);
	
	public int getNumberOfAddresses();
	public void createContactAddress(Address address, int contactId);
	public List<Address> searchContactAddressByZipCode(String zipCode);
	public Address searchContactAddressById(int id);
	
	public int getNumberOfContactPhones();
	public void createContactPhone(Phone phone, int contactId);
	public List<Phone> searchContactPhonesByNumber(String phoneNumber);
	public Phone searchContactPhoneById(int id);
}