package com.unlimitedcompanies.coms.domain.security;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.domain.security.exen.InvalidPhoneNumberException;

@Entity
@Table(name="phones")
public class ContactPhone
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer phoneId;
	private String phoneNumber;
	private String extention;
	private String phoneType;
	
	@ManyToOne
	@JoinColumn(name="contactId_FK")
	private Contact contact;
	
	protected ContactPhone() {}

	protected ContactPhone(String phoneNumber, String extention, String phoneType, Contact contact)
			throws InvalidPhoneNumberException
	{
		// TODO: Implement validation for the phone number input
		try
		{
			Long.parseLong(phoneNumber);
		} 
		catch (NumberFormatException e)
		{
			throw new InvalidPhoneNumberException();
		}

		this.phoneNumber = phoneNumber;
		this.extention = extention;
		this.phoneType = phoneType;
		this.contact = contact;
	}

	public Integer getPhoneId()
	{
		return phoneId;
	}

	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	public String getExtention()
	{
		return extention;
	}

	public void setExtention(String extention)
	{
		this.extention = extention;
	}

	public String getPhoneType()
	{
		return phoneType;
	}

	public void setPhoneType(String phoneType)
	{
		this.phoneType = phoneType;
	}

	public Contact getContact()
	{
		return contact;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((extention == null) ? 0 : extention.hashCode());
		result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContactPhone other = (ContactPhone) obj;
		if (extention == null)
		{
			if (other.extention != null && !other.extention.equals(""))
				return false;
		} else if (!extention.equals(other.extention))
			return false;
		if (phoneNumber == null)
		{
			if (other.phoneNumber != null && !other.phoneNumber.equals(""))
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		return true;
	}
	
	

}
