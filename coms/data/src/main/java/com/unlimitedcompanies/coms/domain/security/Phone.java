package com.unlimitedcompanies.coms.domain.security;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.domain.security.exceptions.InvalidPhoneNumberException;

@Entity
@Table(name="phone")
public class Phone
{
	@Id
	private Integer phoneId;
	private String phoneNumber;
	private String extention;
	private String phoneType;
	
	@ManyToOne
	@JoinColumn(name="contact_FK")
	private Contact contact;
	
	protected Phone() {}

	public Phone(String phoneNumber, String extention, String phoneType, Contact contact)
			throws InvalidPhoneNumberException
	{
		try
		{
			Long.parseLong(phoneNumber);
		} catch (NumberFormatException e)
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

	public String getExtention()
	{
		return extention;
	}

	public String getPhoneType()
	{
		return phoneType;
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
		Phone other = (Phone) obj;
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
