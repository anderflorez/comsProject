package com.unlimitedcompanies.coms.webservice.security;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ContactNotFoundErrorInformation
{
	private String message;
	
	public ContactNotFoundErrorInformation() {}

	public ContactNotFoundErrorInformation(String message)
	{
		super();
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}
	
	
}
