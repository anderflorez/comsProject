package com.unlimitedcompanies.coms.securityService;

import java.util.List;

import com.unlimitedcompanies.coms.domain.security.Address;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Phone;

public interface ContactService
{
	public void saveContact(Contact Contact);
	public int findNumberOfContacts();
	public List<Contact> findAllContacts();
	public Contact findContactByEmail(String email);
	public Contact findContactById(int id);
	public void updateContact(int id, Contact updatedContact);
	public void deleteContact(Contact contact);
	
	public int findNumberOfContactAddresses();
	public void saveContactAddress(Address address);
	public List<Address> findContactAddressesByZipCode(String zipCode);
	public Address findContactAddressById(int addressId);
	
	public int findNumberOfContacPhones();
	public void saveContactPhone(Phone phone);
	public List<Phone> findContactPhoneByNumber(String phoneNumber);
	public Phone findContactPhoneById(int phoneId);
}
