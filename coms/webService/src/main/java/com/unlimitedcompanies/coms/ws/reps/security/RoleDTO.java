package com.unlimitedcompanies.coms.ws.reps.security;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.security.Role;

@XmlRootElement(name = "role")
public class RoleDTO extends ResourceSupport
{
	private Integer roleId;
	private String roleName;

	public RoleDTO() {}

	public RoleDTO(Role role)
	{
		this.roleId = role.getRoleId();
		this.roleName = role.getRoleName();
	}

	public Integer getRoleId()
	{
		return roleId;
	}

	public void setRoleId(Integer roleId)
	{
		this.roleId = roleId;
	}

	public String getRoleName()
	{
		return roleName;
	}

	public void setRoleName(String roleName)
	{
		this.roleName = roleName;
	}
	
}
