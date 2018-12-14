package com.unlimitedcompanies.coms.service.exceptions;

public class RecordNotDeletedException extends Exception
{
	private static final long serialVersionUID = -8443640037639801424L;
	
	private String message;

	public RecordNotDeletedException(String message)
	{
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}
}
