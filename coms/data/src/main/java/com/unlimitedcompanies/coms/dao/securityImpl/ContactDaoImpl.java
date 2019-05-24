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
import com.unlimitedcompanies.coms.domain.security.Address;
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
	public Contact getContactWithFullEmployee(int contactId)
	{
		Contact contact = em.createQuery("select contact from Contact contact left join fetch contact.employee employee "
											+ "left join fetch employee.pmProjects pmProjects "
											+ "left join fetch employee.superintendentProjects superintendentProjects "
											+ "left join fetch employee.foremanProjects foremanProjects "
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
	public void updateContact(Contact contact)
	{
		em.merge(contact);
	}

	@Override
	public void deleteContact(Contact contact)
	{
		Contact removeContact = em.merge(contact);
		em.remove(removeContact);
	}

	@Override
	public int getNumberOfAddresses()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(addressId) FROM addresses").getSingleResult();
		return bigInt.intValue();
	}

	@Override
	public void createContactAddress(Address address)
	{
		em.persist(address);
	}
	
	@Override
	public List<Address> getAllAddresses(String accessConditions)
	{
		String stringQuery = "select address from Address address";
		if (accessConditions != null && !accessConditions.isEmpty())
		{
			stringQuery += " where " + accessConditions;
		}
				
		List<Address> addresses = em.createQuery(stringQuery, Address.class).getResultList();
		
		return Collections.unmodifiableList(addresses);
	}
	
	@Override
	public Address getContactAddress(Contact contact, String readConditions)
	{
		String stringQuery = "select address from Address address where address.contact = :contact";
		if (readConditions != null && !readConditions.isEmpty())
		{
			stringQuery += " and " + readConditions;
		}
				
		Address address = em.createQuery(stringQuery, Address.class).setParameter("contact", contact).getSingleResult();
		
		return address;
	}
	
	@Override
	public Address getContactAddressById(int id, String readConditions)
	{
		String stringQuery = "select address from Address address where address.addressId = :id";
		if (readConditions != null && !readConditions.isEmpty())
		{
			stringQuery += " and " + readConditions;
		}
				
		Address address = em.createQuery(stringQuery, Address.class).setParameter("id", id).getSingleResult();
		
		return address;
	}
	
	@Override
	public void updateContactAddress(Address address)
	{
		em.merge(address);
	}
	
	@Override
	public void deleteContactAddress(Address address)
	{
		Address deleteAddress = em.merge(address);
		em.remove(deleteAddress);		
	}
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

	@Override
	public void clearEntityManager()
	{
		em.flush();
		em.clear();		
	}

	
}
