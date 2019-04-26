package com.unlimitedcompanies.coms.domain.projects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.domain.employees.Employee;

@Entity
@Table(name = "projects")
public class Project
{
	@Id
	private Integer projectId;
	
	@Column(unique = true, nullable = false)
	private int jobNumber;
	
	@Column(unique = true, nullable = false)
	private String projectName;
	
	@ManyToMany
	@JoinTable(name = "projectManagers",
				joinColumns = {@JoinColumn(name = "projectId_FK")},
				inverseJoinColumns = {@JoinColumn(name = "employeeId_FK")})
	private List<Employee> projectManagers;
	
	@ManyToMany
	@JoinTable(name = "superintendents",
				joinColumns = {@JoinColumn(name = "projectId_FK")},
				inverseJoinColumns = {@JoinColumn(name = "employeeId_FK")})
	private List<Employee> superintendents;
	
	@ManyToMany
	@JoinTable(name = "foremen",
				joinColumns = {@JoinColumn(name = "projectId_FK")},
				inverseJoinColumns = {@JoinColumn(name = "employeeId_FK")})
	private List<Employee> foremen;
	
	protected Project() 
	{
		this.projectManagers = new ArrayList<>();
		this.superintendents = new ArrayList<>();
		this.foremen = new ArrayList<>();
	}

	public Project(int jobNumber, String projectName)
	{
		this.projectManagers = new ArrayList<>();
		this.superintendents = new ArrayList<>();
		this.foremen = new ArrayList<>();
		this.jobNumber = jobNumber;
		this.projectName = projectName;
	}

	public Integer getProjectId()
	{
		return projectId;
	}

	public int getJobNumber()
	{
		return jobNumber;
	}

	public String getProjectName()
	{
		return projectName;
	}

	public List<Employee> getProjectManagers()
	{
		return Collections.unmodifiableList(this.projectManagers);
	}
	
	public void addProjectManager(Employee projectManager)
	{
		this.projectManagers.add(projectManager);
		if (!projectManager.getPmProjects().contains(this))
		{
			projectManager.assignAsProjectManager(this);
		}
	}

	public List<Employee> getSuperintendents()
	{
		return Collections.unmodifiableList(superintendents);
	}
	
	public void addSuperintendent(Employee superintendent)
	{
		this.superintendents.add(superintendent);
		if (!superintendent.getSuperintendentProjects().contains(this))
		{
			superintendent.assignAsSuperintendent(this);
		}
	}

	public List<Employee> getForemen()
	{
		return Collections.unmodifiableList(foremen);
	}
	
	public void addForman(Employee foreman)
	{
		this.foremen.add(foreman);
		if (foreman.getForemanProjects().contains(this))
		{
			foreman.assignAsForeman(this);
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + jobNumber;
		result = prime * result + ((projectName == null) ? 0 : projectName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Project other = (Project) obj;
		if (jobNumber != other.jobNumber) return false;
		if (projectName == null)
		{
			if (other.projectName != null) return false;
		}
		else if (!projectName.equals(other.projectName)) return false;
		return true;
	}
	
}
