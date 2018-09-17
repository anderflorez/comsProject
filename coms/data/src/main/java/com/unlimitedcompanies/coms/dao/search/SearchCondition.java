package com.unlimitedcompanies.coms.dao.search;

public class SearchCondition
{
	private String resource;
	private String field;
	private String value;
	private boolean stringValue;
	private ConditionalOperator operator;

	public SearchCondition(String resource, String field, String value, boolean stringValue, ConditionalOperator operator)
	{
		this.resource = resource;
		this.field = field;
		this.value = value;
		this.stringValue = stringValue;
		this.operator = operator;
	}

	public String getResource()
	{
		return resource;
	}

	public String getField()
	{
		return resource.toLowerCase() + "." + field;
	}

	public String getValue()
	{
		return value;
	}

	public boolean isStringValue()
	{
		return stringValue;
	}

	public ConditionalOperator getOperator()
	{
		return operator;
	}
	
	@Override
	public String toString()
	{
		if (this.isStringValue())
		{
			return this.getField() + " " + this.getOperator() + " '" + this.getValue() + "'";
		}
		else 
		{
			return this.getField() + " " + this.getOperator() + " " + this.getValue();			
		}
	}
}
