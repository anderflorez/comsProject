package com.unlimitedcompanies.coms.ws.reps;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "resource_link")
public class ResourceLink
{
	private String resourceName;
	private String resourceURI;

	public ResourceLink() {}

	public ResourceLink(String resourceName, String resourceURI)
	{
		this.resourceName = resourceName;
		this.resourceURI = resourceURI;
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

	@XmlElement(name = "resource_uri")
	public String getResourceURI()
	{
		return resourceURI;
	}

	public void setResourceURI(String resourceURI)
	{
		this.resourceURI = resourceURI;
	}
}
