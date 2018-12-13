package com.unlimitedcompanies.coms.ws.security.reps;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "resource_link")
public class ResourceLink
{
	private String resourceName;
	private String resourceBaseURL;

	public ResourceLink() {}

	public ResourceLink(String resourceName, String resourceBaseURL)
	{
		this.resourceName = resourceName;
		this.resourceBaseURL = resourceBaseURL;
	}

	@XmlElement(name = "resource_name")
	public String getResourceName()
	{
		return resourceName;
	}

	public void setResourceName(String resourceName)
	{
		this.resourceName = resourceName;
	}

	@XmlElement(name = "resource_base_url")
	public String getResourceBaseURL()
	{
		return resourceBaseURL;
	}

	public void setResourceBaseURL(String resourceBaseURL)
	{
		this.resourceBaseURL = resourceBaseURL;
	}
}
