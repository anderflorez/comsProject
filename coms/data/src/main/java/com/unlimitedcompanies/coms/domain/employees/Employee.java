package com.unlimitedcompanies.coms.domain.employees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.domain.projects.Project;
import com.unlimitedcompanies.coms.domain.security.User;

@Entity
@Table(name = "employees")
public class Employee
{
	@Id
	private Integer employeeId;
	
	@OneToOne
	@JoinColumn(name = "userId_FK")
	private User user;
	
	@ManyToMany(mappedBy = "projectManagers")
	private List<Project> pmProjects;
	
	@ManyToMany(mappedBy = "superintendents")
	private List<Project> superintendentProjects;
	
	@ManyToMany(mappedBy = "foremen")
	private List<Project> foremanProjects;
	
	protected Employee() 
	{
		this.pmProjects = new ArrayList<>();
		this.superintendentProjects = new ArrayList<>();
		this.foremanProjects = new ArrayList<>();
	}
	
	public Employee(User user)
	{
		this.pmProjects = new ArrayList<>();
		this.superintendentProjects = new ArrayList<>();
		this.foremanProjects = new ArrayList<>();
		this.user = user;
	}

	public Integer getEmployeeId()
	{
		return employeeId;
	}

	public User getUser()
	{
		return user;
	}

	public List<Project> getPmProjects()
	{
		return Collections.unmodifiableList(pmProjects);
	}
	
	public void assignAsProjectManager(Project project)
	{
		this.pmProjects.add(project);
		if (!project.getProjectManagers().contains(this))
		{
			project.addProjectManager(this);
		}
	}

	public List<Project> getSuperintendentProjects()
	{
		return Collections.unmodifiableList(superintendentProjects);
	}
	
	public void assignAsSuperintendent(Project project)
	{
		this.superintendentProjects.add(project);
		if (!project.getSuperintendents().contains(this))
		{
			project.addSuperintendent(this);
		}
	}

	public List<Project> getForemanProjects()
	{
		return Collections.unmodifiableList(superintendentProjects);
	}
	
	public void assignAsForeman(Project project)
	{
		this.foremanProjects.add(project);
		if (!project.getForemen().contains(this))
		{
			project.addForman(this);
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Employee other = (Employee) obj;
		if (user == null)
		{
			if (other.user != null) return false;
		}
		else if (!user.equals(other.user)) return false;
		return true;
	}
		
}
