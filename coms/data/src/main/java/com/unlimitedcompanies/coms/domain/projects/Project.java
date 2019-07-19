package com.unlimitedcompanies.coms.domain.projects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.domain.employee.Employee;

@Entity
@Table(name = "projects")
public class Project
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer projectId;
	
	@Column(unique = true, nullable = false)
	private int jobNumber;
	
	@Column(unique = true, nullable = false)
	private String projectName;
	
	@OneToMany(mappedBy="project")
	private List<ProjectMember> projectMembers;
	
	protected Project() 
	{
		this.projectMembers = new ArrayList<>();
	}

	public Project(int jobNumber, String projectName)
	{
		this.jobNumber = jobNumber;
		this.projectName = projectName;
		this.projectMembers = new ArrayList<>();
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

	public List<ProjectMember> getProjectMembers()
	{
		return Collections.unmodifiableList(this.projectMembers);
	}
	
	public void addProjectMember(Employee employee, ProjectAccess projectAccess)
	{
		// TODO: Make sure this does NOT add a new employee - only existing employees in the db
		ProjectMember member = new ProjectMember(employee, this, projectAccess);
		if (this.projectMembers == null)
		{
			this.projectMembers = new ArrayList<>();
		}
		this.projectMembers.add(member);
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
