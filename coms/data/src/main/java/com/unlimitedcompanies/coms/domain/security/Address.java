package com.unlimitedcompanies.coms.domain.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="addresses")
public class Address
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Integer addressId;
	
	private String street;
	private String city;
	private String state;
	private String zipCode;
	
	@ManyToOne
	@JoinColumn(name="contactId_FK")
	private Contact contact;
	
	protected Address() {}

	public Address(String street, String city, String state, String zipCode, Contact contact)
	{
		this.street = street;
		this.city = city;
		this.state = state;
		this.zipCode = zipCode;
		this.contact = contact;
	}

	public Integer getAddressId()
	{
		return addressId;
	}
	
	public void setFullAddress(String street, String city, String state, String zipCode)
	{
		this.street = street;
		this.city = city;
		this.state = state;
		this.zipCode = zipCode;
	}

	public String getStreet()
	{
		return street;
	}

	public void setStreet(String street)
	{
		this.street = street;
	}

	public String getCity()
	{
		return city;
	}
	
	public void setCity(String city)
	{
		this.city = city;
	}

	public String getState()
	{
		return state;
	}
	
	public void setState(String state)
	{
		this.state = state;
	}

	public String getZipCode()
	{
		return zipCode;
	}
	
	public void setZipCode(String zipCode)
	{
		this.zipCode = zipCode;
	}

	public Contact getContact()
	{
		return contact;
	}

	public void setContact(Contact contact)
	{
		this.contact = contact;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((street == null) ? 0 : street.hashCode());
		result = prime * result + ((zipCode == null) ? 0 : zipCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Address other = (Address) obj;
		if (city == null)
		{
			if (other.city != null) return false;
		}
		else if (!city.equals(other.city)) return false;
		if (state == null)
		{
			if (other.state != null) return false;
		}
		else if (!state.equals(other.state)) return false;
		if (street == null)
		{
			if (other.street != null) return false;
		}
		else if (!street.equals(other.street)) return false;
		if (zipCode == null)
		{
			if (other.zipCode != null) return false;
		}
		else if (!zipCode.equals(other.zipCode)) return false;
		return true;
	}
	
}
