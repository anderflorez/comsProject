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
	/*
	 * 	<bean id="dataSource" class="org.apache.tomcat.jdbc.pool.DataSource" destroy-method="close">
			<property name="driverClassName" value="com.mysql.cj.jdbc.Driver" />
			<property name="url" value="jdbc:mysql://localhost/coms?autoReconnect=true&amp;useSSL=false&amp;useLegacyDatetimeCode=false&amp;serverTimezone=UTC" />
			<property name="username" value="comsdbadmin" />
			<property name="password" value="Unlimited123!!" />
		</bean>
	 */
	
	@Bean(destroyMethod = "close")
	@Profile("integrationTesting")
	public org.apache.tomcat.jdbc.pool.DataSource testingDataSource()
	{
		org.apache.tomcat.jdbc.pool.DataSource ds = new DataSource();
		ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
		ds.setUrl("jdbc:mysql://localhost/comsTesting?autoReconnect=true&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC");
		ds.setUsername("comsdbadmin");
		ds.setPassword("Unlimited123!!");
		return ds;
	}
	
	@Bean(destroyMethod = "close")
	@Profile("web")
	public DataSource webDataSource()
	{
		DataSource ds = new DataSource();
		ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
		ds.setUrl("jdbc:mysql://localhost/comsTesting?autoReconnect=true&amp;useSSL=false&amp;useLegacyDatetimeCode=false&amp;serverTimezone=UTC");
		ds.setUsername("comsdbadmin");
		ds.setPassword("Unlimited123!!");
		return ds;
	}
	
	@Bean(destroyMethod = "close")
	@Profile("cli")
	public DataSource cliDataSource()
	{
		DataSource ds = new DataSource();
		ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
		ds.setUrl("jdbc:mysql://localhost/comsTesting?autoReconnect=true&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC");
		ds.setUsername("comsdbadmin");
		ds.setPassword("Unlimited123!!");
		return ds;
	}
	
	/*
	 * 	<bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
		<property name="jpaVendorAdapter">
			<bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter">
				<property name="showSql" value="true" />
				<property name="generateDdl" value="false" />
			</bean>
		</property>
		<property name="dataSource" ref="dataSource"/>
	</bean>
	 */
	
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
	
	/*
	 * 	<!-- TransactionManager -->
		<bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager" autowire="byType" />
	
		<!-- Transaction Configuration -->
		<tx:annotation-driven/>
	 */
	
	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf)
	{
		JpaTransactionManager tx = new JpaTransactionManager();
		tx.setEntityManagerFactory(emf);
		return tx;
	}
}
