package com.unlimitedcompanies.coms.ws.config;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.data.config.ServerURLs;
import com.unlimitedcompanies.coms.ws.config.CustomLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = { "com.unlimitedcompanies.coms" })
@Import({ ApplicationConfig.class, CustomLogoutSuccessHandler.class })
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
	@Autowired
	DataSource datasource;
	
	@Autowired
	CustomLogoutSuccessHandler logoutSuccessHandler;
	
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
    	CorsConfiguration configuration = new CorsConfiguration();
    	configuration.addAllowedOrigin(ServerURLs.CLIENT.toString());
    	configuration.addAllowedMethod(HttpMethod.GET);
    	configuration.addAllowedMethod(HttpMethod.POST);
    	configuration.addAllowedHeader("Authorization");
    	configuration.addAllowedHeader("Content-Type");
    	
    	UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    	source.registerCorsConfiguration("/**", configuration);
    	return source;
    }

	@Override
	public void configure(WebSecurity websec)
	{
		// Skipping security for the next defined patterns
		websec.ignoring()
					.antMatchers("/css/**");
		
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception
	{
		String user = "select username, password, enabled from users where username = ?";
		String authorities = "select * from roles INNER JOIN users_roles ON users_roles.roleId_FK = roles.roleId INNER JOIN users ON users.username = ?";
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

		http.cors()
		
			.and().antMatcher("/**")
					.authorizeRequests().anyRequest().authenticated()

			// Form authentication configuration
			.and().formLogin()
						.loginProcessingUrl("/login")
						.loginPage("/pages/login.jsp")
						.permitAll()
						
			// Logout configuration
			.and().logout()
						.invalidateHttpSession(true)
						.clearAuthentication(true)
						.logoutSuccessHandler(logoutSuccessHandler)
						.permitAll()

			.and().csrf().disable();
	}

}
