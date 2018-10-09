package com.unlimitedcompanies.coms.dao.securityImpl;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.ContactDao;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.User;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class ContactDaoImpl implements ContactDao
{
	@PersistenceContext
	private EntityManager em;

	@Override
	public int getNumberOfContacts()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(contactId) FROM contact").getSingleResult();
		return bigInt.intValue();
	}

	@Override
	public void createContact(Contact contact)
	{
		em.persist(contact);
		
		// TODO: Delete after it's been tested
//		em.createNativeQuery(
//				"INSERT INTO contact (contactId, firstName, middleName, lastName, email) VALUES (:id, :fname, :mname, :lname, :email)")
//				.setParameter("id", contact.getContactId())
//				.setParameter("fname", contact.getFirstName())
//				.setParameter("mname", contact.getMiddleName())
//				.setParameter("lname", contact.getLastName())
//				.setParameter("email", contact.getEmail())
//				.executeUpdate();
	}
	
	@Override
	public List<Contact> getAllContacts(User loggedUser)
	{		
		return em.createQuery("select contact from Contact as contact", Contact.class)
							  .getResultList();
	}
	
	@Override
	public Contact getContactById(String Id)
	{
		Contact contact = em.find(Contact.class, Id);
		if (contact == null)
		{
			throw new NoResultException();
		}
		return contact;
	}

	@Override
	public Contact getContactByEmail(String contactEmail)
	{
		return em.createQuery("select contact from Contact as contact where contact.email = :email", Contact.class)
							  .setParameter("email", contactEmail)
							  .getSingleResult();
	}

	@Override
	public void updateContact(String id, Contact contact)
	{
		Contact foundContact = this.getContactById(id);
		foundContact.setFirstName(contact.getFirstName());
		foundContact.setMiddleName(contact.getMiddleName());
		foundContact.setLastName(contact.getLastName());
		foundContact.setEmail(contact.getEmail());
	}

//	@Override
//	public void removeContact(int id)
//	{
//		Contact deleteContact = em.find(Contact.class, id);
//		em.remove(deleteContact);
//	}
//
//	@Override
//	public int getNumberOfAddresses()
//	{
//		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(addressId) FROM address").getSingleResult();
//		return bigInt.intValue();
//	}
//
//	@Override
//	public void createContactAddress(Address address, int contactId)
//	{
//		em.createNativeQuery("INSERT INTO address (street, city, state, zipCode, contact_FK) VALUES (:street, :city, :state, :zipCode, :contact)")
//							 .setParameter("street", address.getStreet())
//							 .setParameter("city", address.getCity())
//							 .setParameter("state", address.getState())
//							 .setParameter("zipCode", address.getZipCode())
//							 .setParameter("contact", contactId)
//							 .executeUpdate();
//	}
//
//	@Override
//	public List<Address> searchContactAddressByZipCode(String zipCode)
//	{
//		return em.createQuery("select address from Address as address where address.zipCode = :zip", Address.class)
//				  			  .setParameter("zip", zipCode)
//				  			  .getResultList();
//	}
//
//	@Override
//	public Address searchContactAddressById(int id)
//	{
//		return em.find(Address.class, id);
//	}
//
//	@Override
//	public int getNumberOfContactPhones()
//	{
//		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(phoneId) FROM phone").getSingleResult();
//		return bigInt.intValue();
//	}
//
//	@Override
//	public void createContactPhone(Phone phone, int contactId)
//	{
//		em.createNativeQuery("INSERT INTO phone (phoneNumber, extention, phoneType, contact_FK) VALUES (:phoneNumber, :extention, :phoneType, :contact)")
//							 .setParameter("phoneNumber", phone.getPhoneNumber())
//							 .setParameter("extention", phone.getExtention())
//							 .setParameter("phoneType", phone.getPhoneType())
//							 .setParameter("contact", contactId)
//							 .executeUpdate();
//	}
//
//	@Override
//	public List<Phone> searchContactPhonesByNumber(String phoneNumber)
//	{
//		return em.createQuery("select phone from Phone as phone where phone.phoneNumber = :number", Phone.class)
//							  .setParameter("number", phoneNumber)
//							  .getResultList();
//	}
//
//	@Override
//	public Phone searchContactPhoneById(int id)
//	{
//		return em.createQuery("select phone from Phone as phone where phone.phoneId = :id", Phone.class)
//							  .setParameter("id", id)
//							  .getSingleResult();
//	}

}
