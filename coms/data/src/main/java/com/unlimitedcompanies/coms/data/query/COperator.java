package com.unlimitedcompanies.coms.data.query;

public enum COperator
{
	EQUALS,
	NOT_EQUAL,
	GRATER_THAN,
	LESS_THAN;
	
	public String symbolOperator()
	{
		if (this.equals(EQUALS))
		{
			return "=";
		}
		else if (this.equals(NOT_EQUAL)) 
		{
			return "!=";
		}
		else if(this.equals(GRATER_THAN))
		{
			return ">";
		}
		else if(this.equals(LESS_THAN))
		{
			return "<";
		}
		return this.toString();
	}
}
