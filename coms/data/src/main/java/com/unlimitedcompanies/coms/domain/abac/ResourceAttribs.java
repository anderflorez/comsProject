package com.unlimitedcompanies.coms.domain.abac;

import java.util.List;

public class ResourceAttribs
{
	private List<String> projectNames;
	private List<String> projectManagers;
	private List<String> projectSuperintendents;
	private List<String> projectForemen;
	private String username;
	private List<String> roles;
	
	public ResourceAttribs() {}

	public List<String> getProjectNames()
	{
		return projectNames;
	}

	public void setProjectNames(List<String> projectNames)
	{
		this.projectNames = projectNames;
	}
	
	public void addProjectName(String projectName)
	{
		this.projectNames.add(projectName);
	}

	public List<String> getProjectManagers()
	{
		return projectManagers;
	}

	public void setProjectManagers(List<String> projectManagers)
	{
		this.projectManagers = projectManagers;
	}
	
	public void addProjectManager(String projectManager)
	{
		this.projectManagers.add(projectManager);
	}

	public List<String> getProjectSuperintendents()
	{
		return projectSuperintendents;
	}

	public void setProjectSuperintendents(List<String> projectSuperintendents)
	{
		this.projectSuperintendents = projectSuperintendents;
	}
	
	public void addProjectSuperintendent(String superintendent)
	{
		this.projectSuperintendents.add(superintendent);
	}

	public List<String> getProjectForemen()
	{
		return projectForemen;
	}

	public void setProjectForemen(List<String> projectForemen)
	{
		this.projectForemen = projectForemen;
	}
	
	public void addProjectForman(String foreman)
	{
		this.projectForemen.add(foreman);
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public List<String> getRoles()
	{
		return roles;
	}

	public void setRoles(List<String> roles)
	{
		this.roles = roles;
	}
	
	public void addRole(String role)
	{
		this.roles.add(role);
	}
}
