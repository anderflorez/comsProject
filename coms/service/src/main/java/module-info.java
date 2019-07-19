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
	exports com.unlimitedcompanies.coms.service.abac to testing;
	
}