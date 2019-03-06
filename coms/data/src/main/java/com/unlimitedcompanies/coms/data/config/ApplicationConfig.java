package com.unlimitedcompanies.coms.data.config;

import javax.persistence.EntityManagerFactory;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.unlimitedcompanies.coms"})
public class ApplicationConfig
{	
	@Bean(name = "testingDataSource", destroyMethod = "close")
	@Profile("integrationTesting")
	public DataSource testingDataSource()
	{
		DataSource ds = new DataSource();
		ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
		// TODO: Change the db location and password when placing the project on production
		ds.setUrl("jdbc:mysql://localhost/comsTesting?autoReconnect=true&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC");
		ds.setUsername("comsdbadmin");
		ds.setPassword("Unlimited123!!");
		return ds;
	}
	
	@Bean(name = "productionDataSource", destroyMethod = "close")
	@Profile("production")
	public DataSource productionDataSource()
	{
		DataSource ds = new DataSource();
		ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
		ds.setUrl("jdbc:mysql://localhost/coms?autoReconnect=true&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC");
		ds.setUsername("comsdbadmin");
		ds.setPassword("Unlimited123!!");
		return ds;
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory (DataSource dataSource)
	{
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setShowSql(true);
		jpaVendorAdapter.setGenerateDdl(false);

		emf.setDataSource(dataSource);
		emf.setJpaVendorAdapter(jpaVendorAdapter);
		
		return emf;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf)
	{
		JpaTransactionManager tx = new JpaTransactionManager();
		tx.setEntityManagerFactory(emf);
		return tx;
	}
}
