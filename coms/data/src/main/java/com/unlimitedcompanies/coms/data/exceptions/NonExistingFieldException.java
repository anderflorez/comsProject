package com.unlimitedcompanies.coms.data.exceptions;

public class NonExistingFieldException extends Exception
{
	private static final long serialVersionUID = 1391217509882069456L;
	private String message;
	
	public NonExistingFieldException(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return this.message;
	}

}
