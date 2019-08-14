open module service
{
	requires transitive data;
	requires java.management;
	requires java.sql;
	requires java.base;
	requires org.hibernate.orm.core;
	requires spring.beans;
	requires spring.context;
	requires spring.tx;
	
	exports com.unlimitedcompanies.coms.service.security;
	exports com.unlimitedcompanies.coms.service.employee;
	exports com.unlimitedcompanies.coms.service.exceptions;
	
	// TODO: Evaluate if the next line is needed - it supports the CLI client being capable of running initial setup
	exports com.unlimitedcompanies.coms.service.system to testing, uiCLI;
	
}