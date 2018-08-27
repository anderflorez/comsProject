package com.unlimitedcompanies.coms.webConfig;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.webSecurity.AuthFailureHandler;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = {"com.unlimitedcompanies.coms.webSecurity"})
@Import(ApplicationConfig.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter 
{
	@Autowired
	DataSource datasource;
	
	@Autowired
	AuthFailureHandler authFailureHandler;
	
	@Override
	public void configure(WebSecurity web)
	{
		web.ignoring()
				.antMatchers("/js/**")
				.antMatchers("/images/**")
				.antMatchers("/css/**");
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception
	{
		String users = "select username, password, enabled from user where username = ?";
		String authorities = "select * from role INNER JOIN user_role ON user_role.role_FK = role.roleId INNER JOIN user ON user.username = ?";
		PasswordEncoder pe = new BCryptPasswordEncoder();
		
		auth.jdbcAuthentication()
				.dataSource(datasource)
				.passwordEncoder(pe)
				.usersByUsernameQuery(users)
				.authoritiesByUsernameQuery(authorities);
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception
	{		
		http.antMatcher("/**").authorizeRequests()
					.antMatchers("/initialSetup").permitAll()
					.antMatchers("/app/login.jsp").permitAll()
					.anyRequest().authenticated()
				
				.and().formLogin()
					.loginProcessingUrl("/login")
					.loginPage("/app/login.jsp")
					.failureHandler(authFailureHandler)
					.permitAll()
					
				.and().logout()
					.logoutSuccessUrl("/app/login.jsp")
					
				.and().csrf()
					.disable();
	}
}
