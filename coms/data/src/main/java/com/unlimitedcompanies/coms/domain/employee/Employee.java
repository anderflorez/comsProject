package com.unlimitedcompanies.coms.domain.employee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.domain.projects.ProjectAccess;
import com.unlimitedcompanies.coms.domain.projects.ProjectMember;
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
	
	@OneToMany(mappedBy = "employee")
	private List<ProjectMember> projectMembers;
	
	protected Employee() 
	{
		this.projectMembers = new ArrayList<>();
	}
	
	public Employee(Contact contact)
	{
		this.projectMembers = new ArrayList<>();
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

	public List<String> getPMAssociatedProjectNames()
	{
		List<String> projectNames = new ArrayList<>();
		for (ProjectMember member : this.projectMembers)
		{
			if (member.getProjectAccess() == ProjectAccess.PROJECT_MANAGER)
			{
				projectNames.add(member.getProject().getProjectName());
			}
		}
		return projectNames;
	}
	
	public List<String> getSuperintendentAssociatedProjectNames()
	{
		List<String> projectNames = new ArrayList<>();		
		for (ProjectMember member : this.projectMembers)
		{
			if (member.getProjectAccess() == ProjectAccess.SUPERINTENDENT)
			{
				projectNames.add(member.getProject().getProjectName());
			}
		}
		
		return projectNames;
	}
	
	public List<String> getForemanAssociatedProjectNames()
	{
		List<String> projectNames = new ArrayList<>();		
		for (ProjectMember member : this.projectMembers)
		{
			if (member.getProjectAccess() == ProjectAccess.FOREMAN)
			{
				projectNames.add(member.getProject().getProjectName());
			}
		}
		
		return projectNames;
	}
	
	public List<ProjectMember> getAssociatedProjects()
	{
		return Collections.unmodifiableList(this.projectMembers);
	}
	
	public List<String> getAssociatedProjectNames()
	{
		List<String> projectNames = new ArrayList<>();
		
		for(ProjectMember member : this.projectMembers)
		{
			projectNames.add(member.getProject().getProjectName());
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
