package com.unlimitedcompanies.coms.service.exceptions;

public class RecordNotFoundException extends Exception
{
	private static final long serialVersionUID = -5484638512932438252L;
	
	private String message;

	public RecordNotFoundException(String message)
	{
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}
}
