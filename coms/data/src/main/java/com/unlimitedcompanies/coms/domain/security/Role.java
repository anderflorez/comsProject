package com.unlimitedcompanies.coms.domain.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="role")
public class Role
{
	@Id
	private Integer roleId;
	private String roleName;
	
	@ManyToMany(mappedBy = "roles")
	private List<User> users = new ArrayList<>();
	
	@OneToMany(mappedBy = "role")
	private Set<ResourcePermissions> resourcePermissions = new HashSet<>();
	
	@ManyToMany
	@JoinTable(name = "role_resourceField", 
			   joinColumns = {@JoinColumn(name = "roleId_FK")}, 
			   inverseJoinColumns = {@JoinColumn(name = "resourceFieldId_FK")})
	private List<ResourceField> restrictedFields = new ArrayList<>();
	
	protected Role() {}

	public Role(String roleName)
	{
		this.roleName = roleName;
	}
	
	public Integer getRoleId()
	{
		return this.roleId;
	}
	
	public void setRoleId(Integer roleId)
	{
		this.roleId = roleId;
	}
		
	public String getRoleName()
	{
		return this.roleName;
	}
	
	public void setRoleName(String roleName)
	{
		this.roleName = roleName;
	}
	
	public List<User> getMembers()
	{
		return Collections.unmodifiableList(this.users);
	}
	
	public void addUser(User user)
	{
		if (!users.contains(user))
		{
			this.users.add(user);
			user.addRole(this);	
		}
	}
	
	public void removeUser(User user)
	{
		if (this.users.contains(user))
		{
			this.users.remove(user);
			user.removeRole(this);
		}
	}
	
	public void addResourcePermission(ResourcePermissions permission)
	{
		if (!this.resourcePermissions.contains(permission))
		{
			this.resourcePermissions.add(permission);
			permission.assignRole(this);
		}
	}
	
	public void addRestrictedField(ResourceField field)
	{
		if (!this.restrictedFields.contains(field))
		{
			this.restrictedFields.add(field);
			field.assignRestrictedRole(this);
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((roleName == null) ? 0 : roleName.hashCode());
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
		Role other = (Role) obj;
		if (roleName == null)
		{
			if (other.roleName != null)
				return false;
		} else if (!roleName.equals(other.roleName))
			return false;
		return true;
	}
}
