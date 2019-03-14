package com.unlimitedcompanies.coms.data.exceptions;

public class FieldNotInSearchException extends Exception
{
	private static final long serialVersionUID = 1391217509882069456L;
	
	public String getMessage()
	{
		return "ERROR: The field does not exist in the current search";
	}

}
