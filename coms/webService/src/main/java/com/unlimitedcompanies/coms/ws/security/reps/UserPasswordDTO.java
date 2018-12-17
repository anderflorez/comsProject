package com.unlimitedcompanies.coms.ws.security.reps;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

@XmlRootElement(name = "user")
public class UserPasswordDTO extends ResourceSupport
{
	private Integer userId;
	private char[] oldPassword;
	private char[] newPassword;
	
	public UserPasswordDTO() {}

	public Integer getUserId()
	{
		return userId;
	}

	public void setUserId(Integer userId)
	{
		this.userId = userId;
	}

	public char[] getOldPassword()
	{
		return oldPassword;
	}

	public void setOldPassword(char[] oldPassword)
	{
		this.oldPassword = oldPassword;
	}

	public char[] getNewPassword()
	{
		return newPassword;
	}

	public void setNewPassword(char[] newPassword)
	{
		this.newPassword = newPassword;
	}

}
