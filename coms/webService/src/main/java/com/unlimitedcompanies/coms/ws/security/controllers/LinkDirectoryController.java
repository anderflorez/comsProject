package com.unlimitedcompanies.coms.ws.security.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unlimitedcompanies.coms.ws.config.LinkDirectory;

@RestController
public class LinkDirectoryController
{
	@Autowired
	LinkDirectory linkDirectory;
	
	@RequestMapping(name = "/restDirectory")
	public Map<String, String> getLinkDirectory()
	{
		return linkDirectory.getLinkDir();
	}
}
