package com.unlimitedcompanies.coms.webConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter 
{
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception
	{
		PasswordEncoder pe = new BCryptPasswordEncoder();		
		auth.inMemoryAuthentication()
				.withUser("anf")
				.password(pe.encode("secret"))
				.roles("ADMIN")
				
				.and().passwordEncoder(new BCryptPasswordEncoder());
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception
	{		
		http.antMatcher("/**")
				.authorizeRequests()
					.antMatchers("/css/**").permitAll()
					.anyRequest().hasRole("ADMIN")
				
				.and()					
				.formLogin()
					.loginProcessingUrl("/login")
					.loginPage("/app/login.jsp")
					.permitAll()
					
				.and()
				.logout()
					.logoutSuccessUrl("/app/login.jsp")
					
				.and()
				.csrf().disable();
		
	}
}
