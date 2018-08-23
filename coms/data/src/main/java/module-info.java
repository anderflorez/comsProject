module data
{
	requires transitive spring.orm;
	requires transitive spring.tx;
	requires transitive tomcat.jdbc;
	requires transitive java.persistence;
	requires spring.beans;
	requires spring.context;
	requires java.validation;
	
	exports com.unlimitedcompanies.coms.dao.security;
	exports com.unlimitedcompanies.coms.dao.security.exceptions;
	exports com.unlimitedcompanies.coms.domain.security;
	exports com.unlimitedcompanies.coms.domain.security.exceptions;
	exports com.unlimitedcompanies.coms.data.config;
}
