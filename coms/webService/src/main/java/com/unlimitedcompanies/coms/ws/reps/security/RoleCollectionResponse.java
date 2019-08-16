package com.unlimitedcompanies.coms.ws.reps.security;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.security.Role;

@XmlRootElement(name = "roles")
public class RoleCollectionResponse extends ResourceSupport
{
	private List<RoleDTO> roles;
	private Integer next;
	private Integer prev;
	
	public RoleCollectionResponse() 
	{
		this.roles = new ArrayList<>();
		this.next = null;
		this.prev = null;
	}

	public RoleCollectionResponse(List<Role> domainRole)
	{
		this.roles = new ArrayList<>();
		for (Role role : domainRole)
		{
			this.roles.add(new RoleDTO(role));
		}
	}

	@XmlElement(name = "role")
	public List<RoleDTO> getRoles()
	{
		return roles;
	}

	public void setRoles(List<Role> domainRole)
	{
		this.roles.clear();
		for (Role role : domainRole)
		{
			this.roles.add(new RoleDTO(role));
		}
	}

	public Integer getNext()
	{
		return next;
	}

	public void setNext(Integer next)
	{
		this.next = next;
	}

	public Integer getPrev()
	{
		return prev;
	}

	public void setPrev(Integer prev)
	{
		this.prev = prev;
	}
	
}
