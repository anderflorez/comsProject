module uiweb
{
	requires data;
	requires service;
	
	requires org.hibernate.orm.core;
	
	requires spring.core;	
	requires spring.context;
	requires spring.beans;
	requires spring.web;
	requires spring.webmvc;
	requires spring.security.core;
	requires spring.security.web;
	requires spring.security.config;
	
	requires javax.servlet.api;
	requires java.sql;
}