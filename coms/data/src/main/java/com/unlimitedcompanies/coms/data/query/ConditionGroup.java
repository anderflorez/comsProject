package com.unlimitedcompanies.coms.data.query;

import com.unlimitedcompanies.coms.data.exceptions.FieldNotInSearchException;
import com.unlimitedcompanies.coms.data.exceptions.IncorrectFieldFormatException;

public interface ConditionGroup
{
	public ConditionGroup and(String field, COperator cOperator, String value, char valueType) 
			throws FieldNotInSearchException, IncorrectFieldFormatException;
	public ConditionGroup or(String field, COperator cOperator, String value, char valueType) 
			throws FieldNotInSearchException, IncorrectFieldFormatException;
}
