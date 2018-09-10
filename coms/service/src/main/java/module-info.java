module service
{
	requires transitive data;
	requires java.management;
	requires spring.security.core;
	requires spring.beans;
	requires spring.context;
	requires spring.tx;
	
	exports com.unlimitedcompanies.coms.securityService;
	exports com.unlimitedcompanies.coms.securityServiceExceptions;
}