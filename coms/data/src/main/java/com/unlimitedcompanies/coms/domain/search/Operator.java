package com.unlimitedcompanies.coms.domain.search;

public enum Operator
{
	EQUALS("="),
	NOT_EQUAL("!="),
	GRATER_THAN(">"),
	LESS_THAN("<");
	
	private String operator;

	private Operator(String operator)
	{
		this.operator = operator;
	}
	
	public String getOperator()
	{
		return this.operator;
	}
	
	public static Operator getNewOperator(String o)
	{
		if (o.equals("=")) { return Operator.EQUALS; }
		if (o.equals("!=")) { return Operator.NOT_EQUAL; }
		if (o.equals(">")) { return Operator.GRATER_THAN; }
		if (o.equals("<")) { return Operator.LESS_THAN; }
		else {return null;}
	}
}
