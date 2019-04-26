package com.unlimitedcompanies.coms.data.abac;

import java.util.ArrayList;
import java.util.List;

import com.unlimitedcompanies.coms.domain.security.Role;
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
			List<String> roleNames = new ArrayList<>();
			for (Role next : user.getRoles())
			{
				roleNames.add(next.getRoleName());
			}
			return roleNames;
		}
		else if (this.userField.equals("projectName"))
		{
			// TODO: Add logic here when the project db table and java classes exist
			return null;
		}
		else
		{
			List<String> username = new ArrayList<>();
			username.add(user.getUsername());
			return username;
		}
	}
}
