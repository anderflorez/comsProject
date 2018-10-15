module data
{
	requires transitive java.persistence;
	requires transitive tomcat.jdbc;
	requires transitive spring.orm;
	requires transitive spring.tx;

	requires java.sql;
	requires java.management;
	requires java.validation;
	
	requires spring.beans;
	requires spring.context;
	requires spring.security.core;
	
	exports com.unlimitedcompanies.coms.data.config;
	exports com.unlimitedcompanies.coms.domain.search;
	exports com.unlimitedcompanies.coms.dao.security;
	exports com.unlimitedcompanies.coms.dao.securitySettings;
	exports com.unlimitedcompanies.coms.dao.security.exceptions;
	exports com.unlimitedcompanies.coms.domain.security;
	exports com.unlimitedcompanies.coms.domain.security.exen;
}
