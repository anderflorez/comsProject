module testing
{
	requires transitive data;
	requires service;
	requires com.sun.xml.bind;
	
	requires spring.core;
	requires spring.context;
	requires spring.beans;
	
	opens com.unlimitedcompanies.coms.service.integrationTests to spring.core;
}