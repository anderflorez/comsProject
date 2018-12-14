package com.unlimitedcompanies.coms.service.exceptions;

public class UnknownException extends Exception
{
	private static final long serialVersionUID = -5230664563180911922L;
	
	private Exception causingException;

	public UnknownException()
	{
		this.causingException = null;
	}

	public UnknownException(Exception causingException)
	{
		this.causingException = causingException;
	}

	public Exception getCausingException()
	{
		return causingException;
	}	
}
