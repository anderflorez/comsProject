package com.unlimitedcompanies.coms.ws.appMgmt;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

@XmlRootElement
public class RepFacade extends ResourceSupport
{
	private int error_code;
	private String error_message;
	
	public RepFacade() 
	{
		this.error_code = 200;
	}

	public int getErrorCode()
	{
		return error_code;
	}

	public void setErrorCode(int errorCode)
	{
		this.error_code = errorCode;
	}

	public String getErrorMessage()
	{
		return error_message;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.error_message = errorMessage;
	}
		
}
