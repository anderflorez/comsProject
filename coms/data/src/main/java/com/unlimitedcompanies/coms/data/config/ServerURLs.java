package com.unlimitedcompanies.coms.data.config;

public enum ServerURLs
{
	PROVIDER("http://192.168.1.31:8080/comsws"),
	PROVIDER_DATABASE("jdbc:mysql://localhost"),
	CLIENT("http://192.168.1.31:3000"),
//	CLIENT("http://localhost:8080"),
	CLIENT_REDIRECT("http://192.168.1.31:3000/tokenmanager");
//	CLIENT_REDIRECT("http://localhost:8080/coms/tokenmanager");
	
	private String uri;
	
	private ServerURLs(String uri)
	{
		this.uri = uri;
	}
	
	public String toString() 
	{
		return this.uri;
	}
}
