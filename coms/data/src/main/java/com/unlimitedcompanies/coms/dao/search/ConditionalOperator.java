package com.unlimitedcompanies.coms.dao.search;

public enum ConditionalOperator
{
	EQUAL("="),
	NOT_EQUAL("!="),
	GRATER_THAN(">"),
	LESS_THAN("<");
	
	private String operator;

	private ConditionalOperator(String operator)
	{
		this.operator = operator;
	}	
	
	@Override
	public String toString()
	{
		return this.operator;
	}
}
