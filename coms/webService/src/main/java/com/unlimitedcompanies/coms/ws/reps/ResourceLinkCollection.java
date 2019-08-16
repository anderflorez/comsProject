package com.unlimitedcompanies.coms.ws.reps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "resource_collection")
public class ResourceLinkCollection
{
	@XmlElement(name = "resource")
	private List<ResourceLink> resources;
	
	public ResourceLinkCollection()
	{
		this.resources = new ArrayList<>();
	}

	public List<ResourceLink> getResources()
	{
		return Collections.unmodifiableList(this.resources);
	}

	public void addResources(ResourceLink resource)
	{
		this.resources.add(resource);
	}
}
