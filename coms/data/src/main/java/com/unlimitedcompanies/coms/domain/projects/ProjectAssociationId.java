package com.unlimitedcompanies.coms.domain.projects;

import java.io.Serializable;

public class ProjectAssociationId implements Serializable
{
	private static final long serialVersionUID = -288981363192003455L;
	private int projectId;
	private int employeeId;
	
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
		ProjectAssociationId other = (ProjectAssociationId) obj;
		if (employeeId != other.employeeId) return false;
		if (projectId != other.projectId) return false;
		return true;
	}
	
	
}
