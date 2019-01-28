package com.unlimitedcompanies.coms.data.query;

public interface ConditionGroup
{
	public ConditionGroup and(String field, COperator cOperator, String value, char valueType);
	public ConditionGroup or(String field, COperator cOperator, String value, char valueType);
}
