package com.unlimitedcompanies.coms.service.exceptions;

public class RecordNotChangedException extends Exception
{
	private static final long serialVersionUID = 3856392234808903023L;
	private String message;

	public RecordNotChangedException(String message)
	{
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}
	
}
