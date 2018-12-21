package com.unlimitedcompanies.coms.ws.security.reps;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.security.User;

@XmlRootElement(name = "users")
public class UserCollectionResponse extends ResourceSupport
{
	private List<UserDTO> users;
	private Integer prevPage;
	private Integer nextPage;
	
	public UserCollectionResponse()
	{
		this.users = new ArrayList<>();
		this.prevPage = null;
		this.nextPage = null;
	}
	
	public UserCollectionResponse(List<User> domainUsers)
	{
		this.users = new ArrayList<>();
		for (User user : domainUsers)
		{
			this.users.add(new UserDTO(user));
		}
	}

	@XmlElement(name = "user")
	public List<UserDTO> getUsers()
	{
		return users;
	}

	public void setUsers(List<User> domainUsers)
	{
		this.users.clear();
		for (User user : domainUsers)
		{
			this.users.add(new UserDTO(user));
		}
	}

	public Integer getPrevPage()
	{
		return prevPage;
	}

	public void setPrevPage(Integer prevPage)
	{
		this.prevPage = prevPage;
	}

	public Integer getNextPage()
	{
		return nextPage;
	}

	public void setNextPage(Integer nextPage)
	{
		this.nextPage = nextPage;
	}
	
}
