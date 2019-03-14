package com.unlimitedcompanies.coms.data.exceptions;

public class NoLogicalOperatorException extends Exception
{
	private static final long serialVersionUID = 6004710314522950627L;
	private String message = "There are existing conditions without an operator in the Condition Group";

	public String getMessage()
	{
		return this.message;
	}
}
