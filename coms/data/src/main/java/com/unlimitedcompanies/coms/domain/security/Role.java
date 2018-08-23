package com.unlimitedcompanies.coms.domain.security;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
	
	protected Role() {}

	public Role(String roleName)
	{	
		this.roleName = roleName;
	}
	
	public Integer getRoleId()
	{
		return this.roleId;
	}
	
	public String getRoleName()
	{
		return this.roleName;
	}
	
	public List<User> getMembers()
	{
		return this.users;
	}
	
	public void addUser(User user)
	{
		this.users.add(user);
		user.getRoles().add(this);
	}
	
	public void removeUser(User user)
	{
		this.users.remove(user);
		user.getRoles().remove(this);
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
