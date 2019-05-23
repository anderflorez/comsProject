package com.unlimitedcompanies.coms.domain.abac;

import java.util.ArrayList;
import java.util.List;

public class ResourceAttribs
{
	private List<String> projectNames;
	private List<String> projectManagers;
	private List<String> projectSuperintendents;
	private List<String> projectForemen;
	
	public ResourceAttribs() 
	{
		this.projectNames = new ArrayList<>();
		this.projectManagers = new ArrayList<>();
		this.projectSuperintendents = new ArrayList<>();
		this.projectForemen = new ArrayList<>();
	}

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

}
