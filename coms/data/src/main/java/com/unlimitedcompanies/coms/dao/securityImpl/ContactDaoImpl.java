package com.unlimitedcompanies.coms.dao.securityImpl;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.ContactDao;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.exen.RecordNotFoundException;

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
	public void createContact(Contact contact) throws ConstraintViolationException
	{	
		try
		{
			em.createNativeQuery(
					"INSERT INTO contact (contactCharId, firstName, middleName, lastName, email) VALUES (:charId, :fname, :mname, :lname, :email)")
					.setParameter("charId", contact.getContactCharId())
					.setParameter("fname", contact.getFirstName())
					.setParameter("mname", contact.getMiddleName())
					.setParameter("lname", contact.getLastName())
					.setParameter("email", contact.getEmail())
					.executeUpdate();
		} 
		catch (PersistenceException e)
		{
			if (e.getCause() instanceof ConstraintViolationException)
			{
				throw (ConstraintViolationException)e.getCause();
			}
		}
	}
	
	@Override
	public List<Contact> getAllContacts()
	{
		return em.createQuery("select contact from Contact as contact order by contact.firstName", Contact.class)
							  .getResultList();
	}
	
	@Override
	public List<Contact> getContactsByRange(int page, int elements)
	{
		return em.createQuery("select contact from Contact contact order by contact.firstName", Contact.class)
							  .setFirstResult(page * elements)
							  .setMaxResults(elements)
							  .getResultList();
	}
	
	@Override
	public Contact getContactById(int Id) throws RecordNotFoundException
	{
		Contact contact = em.find(Contact.class, Id);
		if (contact == null)
		{
			throw new RecordNotFoundException();
		}
		return contact;
	}
	
	@Override
	public Contact getContactByCharId(String contactCharId)
	{
		return em.createQuery("select contact from Contact as contact where contact.contactCharId = :charId", Contact.class)
							  .setParameter("charId", contactCharId)
							  .getSingleResult();
	}

	@Override
	public Contact getContactByEmail(String contactEmail)
	{
		return em.createQuery("select contact from Contact as contact where contact.email = :email", Contact.class)
							  .setParameter("email", contactEmail)
							  .getSingleResult();
	}

	@Override
	public void updateContact(Contact updatedContact) throws RecordNotFoundException
	{
		Contact foundContact = this.getContactById(updatedContact.getContactId());
		foundContact.setFirstName(updatedContact.getFirstName());
		foundContact.setMiddleName(updatedContact.getMiddleName());
		foundContact.setLastName(updatedContact.getLastName());
		foundContact.setEmail(updatedContact.getEmail());
	}

	@Override
	public void deleteContact(int contactId) throws RecordNotFoundException
	{
		Contact contact = this.getContactById(contactId);
		em.remove(contact);
	}

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
