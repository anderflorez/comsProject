package com.unlimitedcompanies.coms.data.exceptions;

public class ExistingConditionGroupException extends Exception
{
	private static final long serialVersionUID = -9104376672925290218L;
	
	private String message;
	
	public ExistingConditionGroupException(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return this.message;
	}
}
