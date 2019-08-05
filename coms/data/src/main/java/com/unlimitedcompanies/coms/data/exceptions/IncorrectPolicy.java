package com.unlimitedcompanies.coms.data.exceptions;

public class IncorrectPolicy extends Exception
{
	private static final long serialVersionUID = 335499231930889175L;

	private String message;
	
	public IncorrectPolicy(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return this.message;
	}
}
