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
}
