package com.unlimitedcompanies.coms.domain.employee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.domain.projects.Project;
import com.unlimitedcompanies.coms.domain.security.Contact;

@Entity
@Table(name = "employees")
public class Employee
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer employeeId;
	
	@OneToOne
	@JoinColumn(name = "contactId_FK")
	private Contact contact;
	
	@ManyToMany(mappedBy = "projectManagers")
	private Set<Project> pmProjects;
	
	@ManyToMany(mappedBy = "superintendents")
	private Set<Project> superintendentProjects;
	
	@ManyToMany(mappedBy = "foremen")
	private Set<Project> foremanProjects;
	
	protected Employee() 
	{
		this.pmProjects = new HashSet<>();
		this.superintendentProjects = new HashSet<>();
		this.foremanProjects = new HashSet<>();
	}
	
	public Employee(Contact contact)
	{
		this.pmProjects = new HashSet<>();
		this.superintendentProjects = new HashSet<>();
		this.foremanProjects = new HashSet<>();
		this.contact = contact;
	}

	public Integer getEmployeeId()
	{
		return employeeId;
	}

	public Contact getContact()
	{
		return contact;
	}

	public Set<Project> getPmProjects()
	{
		return Collections.unmodifiableSet(pmProjects);
	}
	
	public List<String> getPmProjectNames()
	{
		List<String> projectNames = new ArrayList<>();
		
		for (Project next : this.pmProjects)
		{
			projectNames.add(next.getProjectName());
		}
		
		return projectNames;
	}
	
	public void assignAsProjectManager(Project project)
	{
		this.pmProjects.add(project);
		if (!project.getProjectManagers().contains(this))
		{
			project.addProjectManager(this);
		}
	}

	public Set<Project> getSuperintendentProjects()
	{
		return Collections.unmodifiableSet(superintendentProjects);
	}
	
	public List<String> getSuperintendentProjectNames()
	{
		List<String> projectNames = new ArrayList<>();
		
		for (Project next : this.superintendentProjects)
		{
			projectNames.add(next.getProjectName());
		}
		
		return projectNames;
	}
	
	public void assignAsSuperintendent(Project project)
	{
		this.superintendentProjects.add(project);
		if (!project.getSuperintendents().contains(this))
		{
			project.addSuperintendent(this);
		}
	}

	public Set<Project> getForemanProjects()
	{
		return Collections.unmodifiableSet(superintendentProjects);
	}
	
	public List<String> getForemanProjectNames()
	{
		List<String> projectNames = new ArrayList<>();
		
		for (Project next : this.foremanProjects)
		{
			projectNames.add(next.getProjectName());
		}
		
		return projectNames;
	}
	
	public void assignAsForeman(Project project)
	{
		this.foremanProjects.add(project);
		if (!project.getForemen().contains(this))
		{
			project.addForman(this);
		}
	}
	
	public List<String> getAssociatedProjectNames()
	{
		List<String> projectNames = new ArrayList<>();
		
		for (Project next : this.pmProjects)
		{
			projectNames.add(next.getProjectName());
		}
		for (Project next : this.superintendentProjects)
		{
			projectNames.add(next.getProjectName());
		}
		for (Project next : this.foremanProjects)
		{
			projectNames.add(next.getProjectName());
		}
		
		return projectNames;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contact == null) ? 0 : contact.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Employee other = (Employee) obj;
		if (contact == null)
		{
			if (other.contact != null) return false;
		}
		else if (!contact.equals(other.contact)) return false;
		return true;
	}
		
}
