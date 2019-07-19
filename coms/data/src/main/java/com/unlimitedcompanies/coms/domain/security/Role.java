package com.unlimitedcompanies.coms.domain.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.unlimitedcompanies.coms.domain.abac.ResourceField;

@Entity
@Table(name="roles")
public class Role
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Integer roleId;
	
	@Column(unique=true, nullable=false)
	private String roleName;
	
	@ManyToMany(mappedBy = "roles")
	private Set<User> users = new HashSet<>();
	
	@ManyToMany
	@JoinTable(name = "restrictedFields", 
			   joinColumns = {@JoinColumn(name = "roleId_FK")}, 
			   inverseJoinColumns = {@JoinColumn(name = "resourceFieldId_FK")})
	private List<ResourceField> restrictedFields = new ArrayList<>();
	
	protected Role() 
	{
		this.users = new HashSet<>();
		this.restrictedFields = new ArrayList<>();
	}

	public Role(String roleName)
	{
		this.users = new HashSet<>();
		this.restrictedFields = new ArrayList<>();
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
	
	public Set<User> getUsers()
	{
		return Collections.unmodifiableSet(this.users);
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
	
	public List<ResourceField> getRestrictedFields()
	{
		return Collections.unmodifiableList(this.restrictedFields);
	}

	public void addRestrictedField(ResourceField field)
	{
		if (this.restrictedFields != null && !this.restrictedFields.contains(field))
		{
			this.restrictedFields.add(field);
			field.assignRestrictedRole(this);
		}
	}
	
	public void clearRestrictedFields()
	{
		this.restrictedFields = new ArrayList<>();
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
