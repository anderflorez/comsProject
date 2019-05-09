package com.unlimitedcompanies.coms.domain.abac;

import java.util.ArrayList;
import java.util.List;

import com.unlimitedcompanies.coms.domain.projects.Project;
import com.unlimitedcompanies.coms.domain.security.Role;

public class UserAttribs
{
	private String username;
	private List<String> roles;
	private List<String> projects;
	
	public UserAttribs(String username)
	{
		this.username = username;
		this.roles = new ArrayList<>();
		this.projects = new ArrayList<>();
	}
	
	public String getUsername()
	{
		return username;
	}

	public List<String> getRoles()
	{
		return roles;
	}

	public void setRoles(List<String> roles)
	{
		this.roles = roles;
	}
	
	public void addRole(Role role)
	{
		this.roles.add(role.getRoleName());
	}

	public List<String> getProjects()
	{
		return projects;
	}

	public void setProjects(List<String> projects)
	{
		this.projects = projects;
	}
	
	public void addProject(Project project)
	{
		this.projects.add(project.getProjectName());
	}
	
}
