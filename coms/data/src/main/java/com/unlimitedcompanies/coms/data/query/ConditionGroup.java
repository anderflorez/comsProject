package com.unlimitedcompanies.coms.data.query;

import java.util.Set;

import com.unlimitedcompanies.coms.domain.security.ResourceField;

public interface ConditionGroup
{
	public ConditionGroup and(String field, COperator cOperator, String value, char valueType);
	public ConditionGroup or(String field, COperator cOperator, String value, char valueType);
	
	public static boolean verifyField(String alias, String field, Path path)
	{
		if (path.getAlias().equals(alias))
		{
			Set<ResourceField> rfs = path.getResource().getResourceFields();
			for (ResourceField next : rfs)
			{
				if (next.getResourceFieldName().equals(field))
				{
					return true;
				}
			}
			return false;
		}
		else
		{
			if (!path.getBranches().isEmpty())
			{
				for (Path nextPath : path.getBranches())
				{
					if (verifyField(alias, field, nextPath))
					{
						return true;
					}
				}
				return false;
			}
			else
			{
				return false;
			}
		}
	}
}
