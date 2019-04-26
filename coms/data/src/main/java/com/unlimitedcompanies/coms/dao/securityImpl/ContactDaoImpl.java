package com.unlimitedcompanies.coms.dao.securityImpl;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.ContactDao;
import com.unlimitedcompanies.coms.domain.security.Contact;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class ContactDaoImpl implements ContactDao
{
	@PersistenceContext
	private EntityManager em;

	@Override
	public void createContact(Contact contact)
	{	
		try
		{
//			em.createNativeQuery(
//					"INSERT INTO contacts (contactCharId, firstName, middleName, lastName, email) VALUES (:charId, :fname, :mname, :lname, :email)")
//					.setParameter("charId", contact.getContactCharId())
//					.setParameter("fname", contact.getFirstName())
//					.setParameter("mname", contact.getMiddleName())
//					.setParameter("lname", contact.getLastName())
//					.setParameter("email", contact.getEmail())
//					.executeUpdate();
			em.persist(contact);
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
	public int getNumberOfContacts()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(contactId) FROM contacts").getSingleResult();
		return bigInt.intValue();
	}
	
	@Override
	public boolean existingContact(int contactId)
	{
		Contact contact = em.find(Contact.class, contactId);
		return contact == null ? false : true;
	}
	
	@Override
	public List<Contact> getAllContacts(String policyAccessConditions)
	{
		String stringQuery = "select contact from Contact as contact";
		
		if (!policyAccessConditions.isEmpty())
		{
			stringQuery += " where " + policyAccessConditions;
		}
		stringQuery += " order by contact.firstName";
				
		return em.createQuery(stringQuery, Contact.class)
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
	public Contact getContactById(int Id)
	{
		Contact contact = em.find(Contact.class, Id);
		if (contact == null)
		{
			throw new NoResultException();
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
	public Contact getContactByEmail(String email)
	{
		return em.createQuery("select contact from Contact as contact where contact.email = :email", Contact.class)
		  .setParameter("email", email)
		  .getSingleResult();
	}
	
	@Override
	public Contact getContactByEmail(String email, String policyConditions)
	{
		String stringQuery = "select contact from Contact as contact where contact.email = :email";
		if (!policyConditions.isEmpty())
		{
			stringQuery += " " + policyConditions;
		}

		return em.createQuery(stringQuery, Contact.class)
				.setParameter("email", email)
				.getSingleResult();
	}

	@Override
	public void updateContact(Contact updatedContact)
	{
		Contact foundContact = this.getContactById(updatedContact.getContactId());
		foundContact.setFirstName(updatedContact.getFirstName());
		foundContact.setMiddleName(updatedContact.getMiddleName());
		foundContact.setLastName(updatedContact.getLastName());
		foundContact.setEmail(updatedContact.getEmail());
	}

	@Override
	public void deleteContact(int contactId)
	{
		Contact contact = this.getContactById(contactId);
		em.remove(contact);
	}

//	@Override
//	public int getNumberOfAddresses()
//	{
//		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(addressId) FROM addresses").getSingleResult();
//		return bigInt.intValue();
//	}
//
//	@Override
//	public void createContactAddress(Address address, int contactId)
//	{
//		em.createNativeQuery("INSERT INTO addresses (street, city, state, zipCode, contactId_FK) VALUES (:street, :city, :state, :zipCode, :contact)")
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
//		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(phoneId) FROM phones").getSingleResult();
//		return bigInt.intValue();
//	}
//
//	@Override
//	public void createContactPhone(Phone phone, int contactId)
//	{
//		em.createNativeQuery("INSERT INTO phones (phoneNumber, extention, phoneType, contactId_FK) VALUES (:phoneNumber, :extention, :phoneType, :contact)")
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
