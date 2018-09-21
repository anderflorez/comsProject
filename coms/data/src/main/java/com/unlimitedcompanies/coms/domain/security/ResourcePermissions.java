package com.unlimitedcompanies.coms.domain.security;

import javax.persistence.Entity;
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
	private Integer role_resource_Id;

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

	public ResourcePermissions() {}

	public ResourcePermissions(Role role, Resource resource, boolean createRec, boolean readRec, boolean updateRec,
			boolean deleteRec)
	{
		this.role_resource_Id = null;
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
		this.role_resource_Id = null;
		this.role = role;
		this.resource = resource;
		this.createRec = (byte) (createRec == true ? 1 : 0);
		this.readRec = (byte) (readRec == true ? 1 : 0);
		this.updateRec = (byte) (updateRec == true ? 1 : 0);
		this.deleteRec = (byte) (deleteRec == true ? 1 : 0);
		this.andGroup = conditions;
	}

	public Integer getRole_resource_Id()
	{
		return role_resource_Id;
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

	public void setCreateRec(byte createRec)
	{
		this.createRec = createRec;
	}

	public void setReadRec(byte readRec)
	{
		this.readRec = readRec;
	}

	public void setUpdateRec(byte updateRec)
	{
		this.updateRec = updateRec;
	}

	public void setDeleteRec(byte deleteRec)
	{
		this.deleteRec = deleteRec;
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
