package com.unlimitedcompanies.coms.ws.reps.security;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.security.User;

@XmlRootElement(name = "usersDetailed")
public class UserDetailedCollection extends ResourceSupport
{
	private List<UserDetailedDTO> users;
	
	public UserDetailedCollection()
	{
		this.users = new ArrayList<>();
	}

	public UserDetailedCollection(List<User> users)
	{
		this.users = new ArrayList<>();
		for (User user : users)
		{
			this.users.add(new UserDetailedDTO(user));
		}
	}

	@XmlElement(name = "userDetailed")
	public List<UserDetailedDTO> getUsers()
	{
		return this.users;
	}
	
	public void setUsers(List<User> users)
	{
		this.users.clear();
		for (User user : users)
		{
			this.users.add(new UserDetailedDTO(user));
		}
	}
}
