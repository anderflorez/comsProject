package com.unlimitedcompanies.coms.dao.securityImpl;

import java.math.BigInteger;
import java.util.Collections;
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
import com.unlimitedcompanies.coms.domain.security.ContactAddress;
import com.unlimitedcompanies.coms.domain.security.ContactPhone;

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
	public int getNumberOfAddresses()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(addressId) FROM addresses").getSingleResult();
		return bigInt.intValue();
	}
	
	@Override
	public int getNumberOfContactPhones()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(phoneId) FROM phones").getSingleResult();
		return bigInt.intValue();
	}
	
	@Override
	public Contact getContactWithFullEmployee(int contactId)
	{
		Contact contact = em.createQuery("select contact from Contact as contact left join fetch contact.employee as employee "
											+ "left join fetch employee.projectMembers as member "
											+ "where contact.contactId = :contactId", Contact.class)
								.setParameter("contactId", contactId)
								.getSingleResult();
		
		return contact;
	}
	
//	
//	@Override
//	public boolean existingContact(int contactId)
//	{
//		Contact contact = em.find(Contact.class, contactId);
//		return contact == null ? false : true;
//	}
	
	@Override
	public List<Contact> getAllContacts(String policyAccessConditions)
	{
		String stringQuery = "select contact from Contact as contact";
		
		if (policyAccessConditions != null && !policyAccessConditions.isEmpty())
		{
			stringQuery += " where " + policyAccessConditions;
		}
		stringQuery += " order by contact.firstName";
				
		List<Contact> contacts = em.createQuery(stringQuery, Contact.class).getResultList();
		
		return Collections.unmodifiableList(contacts);
	}
	
	@Override
	public List<Contact> getContactsByRange(int elements, int page, String policyAccessConditions)
	{
		String stringQuery = "select contact from Contact contact";
		
		if (policyAccessConditions != null && !policyAccessConditions.isEmpty())
		{
			stringQuery += " where " + policyAccessConditions;
		}
		
		stringQuery += " order by contact.firstName";
		
		List<Contact> contacts = em.createQuery(stringQuery, Contact.class)
									 .setFirstResult(page * elements)
									 .setMaxResults(elements)
									 .getResultList();
		
		return Collections.unmodifiableList(contacts);
	}
	
	@Override
	public Contact getContactById(int id, String accessConditions)
	{
		String stringQuery = "select contact from Contact contact where contact.contactId = :contactId";
		if (accessConditions != null && !accessConditions.isEmpty())
		{
			stringQuery += " and " + accessConditions;
		}
		
		Contact contact = em.createQuery(stringQuery, Contact.class).setParameter("contactId", id).getSingleResult();
		
		return contact;
	}
	
	@Override
	public Contact getContactByCharId(String contactCharId, String accessConditions)
	{
		String stringQuery = "select contact from Contact as contact where contact.contactCharId = :charId";
		if (accessConditions != null && !accessConditions.isEmpty())
		{
			stringQuery += " and " + accessConditions;
		}
		
		Contact contact = em.createQuery(stringQuery, Contact.class).setParameter("charId", contactCharId).getSingleResult();
		
		return contact;
	}
	
	@Override
	public Contact getContactByEmail(String email, String accessConditions)
	{
		String stringQuery = "select contact from Contact as contact where contact.email = :email";
		if (accessConditions != null && !accessConditions.isEmpty())
		{
			stringQuery += " and " + accessConditions;
		}
		
		Contact contact = em.createQuery(stringQuery, Contact.class).setParameter("email", email).getSingleResult();
		
		return contact;
	}
	
	@Override
	public Contact getContactByPhoneId(int phoneId, String readConditions)
	{
		String stringQuery = "select contact from Contact as contact left join fetch contact.contactPhones as phone where phone.phoneId = :phoneId";
		if (readConditions != null && !readConditions.isEmpty())
		{
			stringQuery += " and " + readConditions;
		}
		
		return em.createQuery(stringQuery, Contact.class).setParameter("phoneId", phoneId).getSingleResult();
	}
	
	@Override
	public void updateContact(Contact contact)
	{
		em.merge(contact);
	}
	
	@Override
	public void deleteAddress(ContactAddress address)
	{
		em.createQuery("delete from ContactAddress where addressId = :id")
			.setParameter("id", address.getAddressId())
			.executeUpdate();
	}
	
	@Override
	public void deleteContactPhone(ContactPhone contactPhone)
	{
		em.createQuery("delete from ContactPhone where phoneId = :id")
			.setParameter("id", contactPhone.getPhoneId())
			.executeUpdate();
	}

	@Override
	public void deleteContact(Contact contact)
	{
		Contact removeContact = em.merge(contact);
		em.remove(removeContact);
	}

//
//	@Override
//	public List<ContactAddress> searchContactAddressByZipCode(String zipCode)
//	{
//		return em.createQuery("select address from ContactAddress as address where address.zipCode = :zip", ContactAddress.class)
//				  			  .setParameter("zip", zipCode)
//				  			  .getResultList();
//	}
//
//	@Override
//	public ContactAddress searchContactAddressById(int id)
//	{
//		return em.find(ContactAddress.class, id);
//	}
//
//	@Override
//	public void createContactPhone(ContactPhone phone, int contactId)
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
//	public List<ContactPhone> searchContactPhonesByNumber(String phoneNumber)
//	{
//		return em.createQuery("select phone from ContactPhone as phone where phone.phoneNumber = :number", ContactPhone.class)
//							  .setParameter("number", phoneNumber)
//							  .getResultList();
//	}
//
//	@Override
//	public ContactPhone searchContactPhoneById(int id)
//	{
//		return em.createQuery("select phone from ContactPhone as phone where phone.phoneId = :id", ContactPhone.class)
//							  .setParameter("id", id)
//							  .getSingleResult();
//	}

	@Override
	public void clearEntityManager()
	{
		em.flush();
		em.clear();		
	}

	
}
