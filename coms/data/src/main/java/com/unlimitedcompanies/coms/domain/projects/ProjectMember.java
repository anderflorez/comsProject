package com.unlimitedcompanies.coms.domain.projects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.domain.employee.Employee;

@Entity
@Table(name="projectMember")
@IdClass(ProjectAssociationId.class)
public class ProjectMember
{
	@Id
	@Column(name="employeeId_FK")
	private int employeeId;
	
	@Id
	@Column(name="projectId_FK")
	private int projectId;
	
	@ManyToOne
	@JoinColumn(name="employeeId_FK", updatable = false, insertable = false)
	private Employee employee;
	
	@ManyToOne
	@JoinColumn(name="projectId_FK", updatable = false, insertable = false)
	private Project project;
	
	@Column(name="projectAccess")
	private String projectAccess;
	
	protected ProjectMember() {}
	
	public ProjectMember(Employee employee, Project project, ProjectAccess projectAccess)
	{
		this.employeeId = employee.getEmployeeId();
		this.projectId = project.getProjectId();
		this.employee = employee;
		this.project = project;
		this.projectAccess = projectAccess.toString();
	}

	public int getEmployeeId()
	{
		return employeeId;
	}

	public int getProjectId()
	{
		return projectId;
	}

	public Employee getEmployee()
	{
		return employee;
	}

	public Project getProject()
	{
		return project;
	}

	public ProjectAccess getProjectAccess()
	{
		return ProjectAccess.valueOf(this.projectAccess.toUpperCase());
	}

	public void setProjectAccess(String projectAccess)
	{
		this.projectAccess = projectAccess;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + employeeId;
		result = prime * result + projectId;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ProjectMember other = (ProjectMember) obj;
		if (employeeId != other.employeeId) return false;
		if (projectId != other.projectId) return false;
		return true;
	}
}
