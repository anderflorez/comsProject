package com.unlimitedcompanies.coms.domain.search;

public enum Operator
{
	EQUAL("="),
	NOT_EQUAL("!="),
	GRATER_THAN(">"),
	LESS_THAN("<");
	
	private String operator;

	private Operator(String operator)
	{
		this.operator = operator;
	}	
	
	@Override
	public String toString()
	{
		return this.operator;
	}
	
	public String toStringLetters()
	{
		if (this.operator.equals("=")) { return "EQUAL"; }
		else {return null;}
	}
	
	public static Operator getOperator(String o)
	{
		if (o.equals("=")) { return Operator.EQUAL; }
		else {return null;}
	}
}
