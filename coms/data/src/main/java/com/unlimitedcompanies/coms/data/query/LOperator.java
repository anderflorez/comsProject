package com.unlimitedcompanies.coms.data.query;

public enum LOperator
{
	AND,
	OR;
	
	public String symbolOperator()
	{
		if (this.equals(AND))
		{
			return "and";
		}
		else if (this.equals(OR)) 
		{
			return "or";
		}
		return this.toString();
	}
}
