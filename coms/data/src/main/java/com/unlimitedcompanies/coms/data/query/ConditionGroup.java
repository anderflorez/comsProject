package com.unlimitedcompanies.coms.data.query;

import java.util.Set;

import com.unlimitedcompanies.coms.domain.security.ResourceField;

public interface ConditionGroup
{
	public ConditionGroup and(String field, COperator cOperator, String value, char valueType);
	public ConditionGroup or(String field, COperator cOperator, String value, char valueType);
}
