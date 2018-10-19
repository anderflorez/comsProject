module service
{
	requires transitive data;
	requires java.management;
	requires java.sql;
	requires spring.security.core;
	requires spring.beans;
	requires spring.context;
	requires spring.tx;
	requires java.base;
	
	exports com.unlimitedcompanies.coms.securityService;
	exports com.unlimitedcompanies.coms.securityServiceExceptions;
}