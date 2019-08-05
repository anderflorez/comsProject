package com.unlimitedcompanies.coms.domain.abac;

import java.util.ArrayList;
import java.util.List;

import com.unlimitedcompanies.coms.domain.security.User;

public enum UserAttribute
{
	USERNAME("username"), 
	ROLES("role.roleName"), 
	PROJECTS("project.projectName");
	
	private String userField;
	
	private UserAttribute(String userField)
	{
		this.userField = userField;
	}

	public List<String> getUserField(User user)
	{
		if (this.userField.equals("roleName"))
		{
			return user.getRoleNames();
		}
		else if (this.userField.equals("projectName"))
		{
			return user.getContact().getEmployee().getAssociatedProjectNames();
		}
		else
		{
			List<String> username = new ArrayList<>();
			username.add(user.getUsername());
			return username;
		}
	}
}
