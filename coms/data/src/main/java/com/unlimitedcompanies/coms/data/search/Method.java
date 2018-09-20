package com.unlimitedcompanies.coms.data.search;

public enum Method
{
	AND("AND"),
	OR("OR"),
	NOT("NOT");
	
	private String name;

	private Method(String name)
	{
		this.name = name;
	}
	
	@Override
	public String toString()
	{
		return " " + this.name + " ";
	}
}
