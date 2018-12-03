package com.unlimitedcompanies.coms.ws.appMgmt;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.springframework.hateoas.ResourceSupport;

public class RepresentationFacade extends ResourceSupport
{	
	private int statusCode;
	private String success;
	
	@XmlElement
	private List<String> errors;
	
	@XmlElement
	private List<String> messages;
	
	public RepresentationFacade()
	{
		this.statusCode = 200;
		this.errors = new ArrayList<>();
		this.messages = new ArrayList<>();
	}

	public int getStatusCode()
	{
		return statusCode;
	}

	public void setStatusCode(int statusCode)
	{
		this.statusCode = statusCode;
	}

	public String getSuccess()
	{
		return success;
	}

	public void setSuccess(String success)
	{
		this.success = success;
	}

	public List<String> getErrors()
	{
		return this.errors;
	}
	
	public void addError(String error)
	{
		this.errors.add(error);
	}

	public List<String> getMessages()
	{
		return this.messages;
	}

	public void addMessage(String message)
	{
		this.messages.add(message);
	}
	
}
