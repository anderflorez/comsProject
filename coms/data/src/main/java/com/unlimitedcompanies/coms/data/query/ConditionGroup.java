package com.unlimitedcompanies.coms.data.query;

import com.unlimitedcompanies.coms.data.exceptions.NonExistingFieldException;
import com.unlimitedcompanies.coms.data.exceptions.ConditionMaxLevelException;
import com.unlimitedcompanies.coms.data.exceptions.ExistingConditionGroupException;
import com.unlimitedcompanies.coms.data.exceptions.IncorrectFieldFormatException;
import com.unlimitedcompanies.coms.data.exceptions.NoLogicalOperatorException;

public interface ConditionGroup
{
	public ConditionGroup and(String field, COperator cOperator, String value, char valueType) 
			throws NonExistingFieldException, IncorrectFieldFormatException, NoLogicalOperatorException, ExistingConditionGroupException, ConditionMaxLevelException;
	public ConditionGroup or(String field, COperator cOperator, String value, char valueType) 
			throws NonExistingFieldException, IncorrectFieldFormatException, NoLogicalOperatorException, ExistingConditionGroupException, ConditionMaxLevelException;
}
