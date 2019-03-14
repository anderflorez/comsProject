package com.unlimitedcompanies.coms.data.exceptions;

public class IncorrectFieldFormatException extends Exception
{
	private static final long serialVersionUID = -4904329927685624978L;

	public String getMessage() {
		return "ERROR: The field format entered is incorrect";
	}
}
