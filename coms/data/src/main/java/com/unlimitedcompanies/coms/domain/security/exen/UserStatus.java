package com.unlimitedcompanies.coms.domain.security.exen;

public enum UserStatus
{
	INACTIVE (0) 
	{
		public String toString()
		{
			return "Inactive";
		}
	},
	ACTIVE (1)
	{
		public String toString()
		{
			return "Active";
		}
	},
	DENIED (2)
	{
		public String toString()
		{
			return "Denied";
		}
	};
	
	private final int statusCode;
	
	private UserStatus(int statusCode)
	{
		this.statusCode = statusCode;
	}
	
	public int getStatusCode()
	{
		return this.statusCode;
	}	
	
	public static UserStatus getNewUserStatus(int s)
	{
		if (s == 0) return UserStatus.INACTIVE;
		else if (s == 1) return UserStatus.ACTIVE;
		else if (s == 2) return UserStatus.DENIED;
		else return null;
	}
}
