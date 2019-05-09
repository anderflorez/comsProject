package com.unlimitedcompanies.coms.domain.abac;

public enum ComparisonOperator
{
	EQUALS("="), 
	NOT_EQUALS("!=");
	
	private String operator;
	
	private ComparisonOperator(String operator)
	{
		this.operator = operator;
	}

	public String getOperator()
	{
		return operator;
	}
}
