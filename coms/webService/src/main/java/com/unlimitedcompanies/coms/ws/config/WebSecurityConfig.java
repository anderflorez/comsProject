package com.unlimitedcompanies.coms.ws.config;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.unlimitedcompanies.coms.data.config.ApplicationConfig;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = { "com.unlimitedcompanies.coms" })
@Import(ApplicationConfig.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
	@Autowired
	DataSource datasource;
	
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

	@Override
	public void configure(WebSecurity websec)
	{
		// Skipping security for the next defined patterns
		websec.ignoring().antMatchers("/css/**");
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception
	{
		String user = "select username, password, enabled from user where username = ?";
		String authorities = "select * from role INNER JOIN user_role ON user_role.role_FK = role.roleId INNER JOIN user ON user.username = ?";
		PasswordEncoder pe = new BCryptPasswordEncoder();

		// Database user authentication
		auth.jdbcAuthentication()
				.dataSource(datasource)
				.passwordEncoder(pe)
				.usersByUsernameQuery(user)
				.authoritiesByUsernameQuery(authorities);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception
	{
		// Url intercept authorization

		http.antMatcher("/**")
			.authorizeRequests().anyRequest().authenticated()

			// Form authentication configuration
			.and().formLogin()
						.loginProcessingUrl("/login")
						.loginPage("/pages/login.jsp")
						.permitAll()
						
			// Logout configuration
			.and().logout()
						.deleteCookies("JSESSIONID")
						.clearAuthentication(true)
						.invalidateHttpSession(true)
						.logoutSuccessUrl("http://localhost:8080/coms/logout")
						.permitAll()
				
			.and().csrf().disable();
	}

}
