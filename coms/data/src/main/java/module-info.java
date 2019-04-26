module data
{
	requires transitive java.persistence;
	requires transitive tomcat.jdbc;
	requires transitive spring.orm;
	requires transitive spring.tx;

	requires java.sql;
	requires java.management;
	requires java.validation;
	requires com.sun.xml.bind;
	requires org.hibernate.orm.core;
	
	requires spring.beans;
	requires spring.context;
	requires spring.security.core;
	
	exports com.unlimitedcompanies.coms.data.config;
	exports com.unlimitedcompanies.coms.data.query;
	exports com.unlimitedcompanies.coms.data.abac;
	exports com.unlimitedcompanies.coms.data.exceptions;
	exports com.unlimitedcompanies.coms.domain.security;
	exports com.unlimitedcompanies.coms.domain.security.exen;
	exports com.unlimitedcompanies.coms.domain.employees;
	exports com.unlimitedcompanies.coms.domain.projects;
	exports com.unlimitedcompanies.coms.dao.security;
	exports com.unlimitedcompanies.coms.dao.securitySettings;
	exports com.unlimitedcompanies.coms.dao.security.exceptions;
}
