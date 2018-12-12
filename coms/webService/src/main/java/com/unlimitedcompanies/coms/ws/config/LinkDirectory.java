package com.unlimitedcompanies.coms.ws.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class LinkDirectory
{
	private String restURL;
	private List<String> resources;
	
	public LinkDirectory()
	{
		this.restURL = "http://localhost:8080/comsws/rest/";
		this.resources = new ArrayList<>();
	}

	public String getRestURL()
	{
		return restURL;
	}

	public List<String> getResources()
	{
		return Collections.unmodifiableList(this.resources);
	}

	public void addResources(String resource)
	{
		this.resources.add(resource);
	}
	
	public Map<String, String> getLinkDir()
	{
		Map<String, String> baseURLs = new HashMap<String, String>();
		for (String resource : this.resources)
		{
			baseURLs.put(resource, this.restURL + resource + "/");
		}
		return baseURLs;
	}
	
}
