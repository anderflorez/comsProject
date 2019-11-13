package com.unlimitedcompanies.coms.ws.config;

import com.unlimitedcompanies.coms.data.config.ServerURLs;

//TODO: This class needs to be replaced with an enum

public class RestLinks
{
	// TODO: This address needs to use the ServerURLs enum to refer to the provider server instead of localhost
	public static final String FULL_REST_URL_BASE = ServerURLs.PROVIDER + "/rest/";
	public static final String URI_REST_BASE = "/rest/";
}
