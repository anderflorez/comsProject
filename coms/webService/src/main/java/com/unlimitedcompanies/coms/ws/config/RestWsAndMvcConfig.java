package com.unlimitedcompanies.coms.ws.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = {"com.unlimitedcompanies.coms"})
@EnableWebMvc
public class RestWsAndMvcConfig implements WebMvcConfigurer
{
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry)
	{
		WebMvcConfigurer.super.addResourceHandlers(registry);
		
		registry.addResourceHandler("/css/**").addResourceLocations("/css/");
	}
	
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer)
	{
		configurer.favorPathExtension(false);
		
		configurer.favorParameter(true);
		Map<String, MediaType> map = new HashMap<>();
		map.put("xml", MediaType.APPLICATION_XML);
		map.put("json", MediaType.APPLICATION_JSON);
		configurer.parameterName("mediaType").mediaTypes(map);
	
		configurer.ignoreAcceptHeader(false);
		configurer.mediaTypes(map);
	}
	
}
