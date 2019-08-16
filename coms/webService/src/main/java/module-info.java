module webService
{
	requires transitive data;
	requires transitive service;
	
	requires spring.core;
	requires spring.beans;
	requires spring.context;
	requires spring.web;
	requires spring.webmvc;
	requires spring.security.core;
	requires spring.security.web;
	requires spring.security.config;
	requires spring.security.oauth2;
	requires spring.hateoas;
	requires org.hibernate.orm.core;
	
	requires java.xml.bind;
	requires javax.servlet.api;

	exports com.unlimitedcompanies.coms.ws.reps;
}