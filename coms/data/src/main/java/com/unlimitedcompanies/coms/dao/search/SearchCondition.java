package com.unlimitedcompanies.coms.dao.search;

public class SearchCondition
{
	private String resource;
	private String field;
	private Operator operator;
	private Object value;

	public SearchCondition(String resource, String field, Operator operator, Object value)
	{
		this.resource = resource.toLowerCase();
		this.field = field;
		this.operator = operator;
		this.value = value;
	}

	public String getResource()
	{
		return resource;
	}

	public String getFullFieldName()
	{
		return this.resource + "." + field;
	}
	
	public String getFieldName()
	{
		return this.field;
	}

	public Operator getOperator()
	{
		return operator;
	}
	
	public Object getValue()
	{
		return value;
	}
	
	@Override
	public String toString()
	{
		return this.getFullFieldName() + " " + this.getOperator() + " :" + this.getFieldName();
	}
}
