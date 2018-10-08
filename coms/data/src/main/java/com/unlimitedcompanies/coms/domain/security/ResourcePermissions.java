package com.unlimitedcompanies.coms.domain.security;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "role_resource")
public class ResourcePermissions
{
	@Id
	private String roleResourceIdentifier;

	@ManyToOne
	@JoinColumn(name = "roleId_FK")
	private Role role;

	@ManyToOne
	@JoinColumn(name = "resourceId_FK")
	private Resource resource;

	@OneToOne
	@JoinColumn(name = "andGroupId_FK")
	private AndGroup andGroup;

	private byte createRec;
	private byte readRec;
	private byte updateRec;
	private byte deleteRec;

	public ResourcePermissions() 
	{
		this.roleResourceIdentifier = UUID.randomUUID().toString();
	}

	public ResourcePermissions(Role role, Resource resource, boolean createRec, boolean readRec, boolean updateRec,
			boolean deleteRec)
	{
		this.roleResourceIdentifier = UUID.randomUUID().toString();
		this.role = role;
		this.resource = resource;
		this.createRec = (byte) (createRec == true ? 1 : 0);
		this.readRec = (byte) (readRec == true ? 1 : 0);
		this.updateRec = (byte) (updateRec == true ? 1 : 0);
		this.deleteRec = (byte) (deleteRec == true ? 1 : 0);
	}

	public ResourcePermissions(Role role, Resource resource, boolean createRec, boolean readRec, boolean updateRec,
			boolean deleteRec, AndGroup conditions)
	{
		this.roleResourceIdentifier = UUID.randomUUID().toString();
		this.role = role;
		this.resource = resource;
		this.createRec = (byte) (createRec == true ? 1 : 0);
		this.readRec = (byte) (readRec == true ? 1 : 0);
		this.updateRec = (byte) (updateRec == true ? 1 : 0);
		this.deleteRec = (byte) (deleteRec == true ? 1 : 0);
		this.andGroup = conditions;
	}

	public String getPermissionId()
	{
		return roleResourceIdentifier;
	}

	public Role getRole()
	{
		return role;
	}

	public Resource getResource()
	{
		return resource;
	}

	public AndGroup getAndGroup()
	{
		return andGroup;
	}

	public byte getCreateRec()
	{
		return createRec;
	}

	public byte getReadRec()
	{
		return readRec;
	}

	public byte getUpdateRec()
	{
		return updateRec;
	}

	public byte getDeleteRec()
	{
		return deleteRec;
	}

	public void setRole(Role role)
	{
		this.role = role;
	}

	public void setResource(Resource resource)
	{
		this.resource = resource;
	}

	public void setAndGroup(AndGroup andGroup)
	{
		this.andGroup = andGroup;
	}

	public void setCreateRec(boolean createRec)
	{
		this.createRec = (byte) (createRec == true ? 1 : 0);
	}

	public void setReadRec(boolean readRec)
	{
		this.readRec = (byte) (readRec == true ? 1 : 0);
	}

	public void setUpdateRec(boolean updateRec)
	{
		this.updateRec = (byte) (updateRec == true ? 1 : 0);
	}

	public void setDeleteRec(boolean deleteRec)
	{
		this.deleteRec = (byte) (deleteRec == true ? 1 : 0);
	}

	public void assignRole(Role role)
	{
		if (!this.role.equals(role))
		{
			this.role = role;
			role.addResourcePermission(this);
		}
	}
	
	public void assignResource(Resource resource)
	{
		if (!this.resource.equals(resource))
		{
			this.resource = resource;
			resource.addPermission(this);
		}
	}
	
	public void assignConditionGroup(AndGroup andGroup)
	{
		if (!this.andGroup.equals(andGroup))
		{
			this.andGroup = andGroup;
			andGroup.assignToPermission(this);
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resource == null) ? 0 : resource.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourcePermissions other = (ResourcePermissions) obj;
		if (resource == null)
		{
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		if (role == null)
		{
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		return true;
	}

}
