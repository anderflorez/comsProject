package com.unlimitedcompanies.coms.domain.unitTests;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.data.exceptions.DuplicatedResourcePolicyException;
import com.unlimitedcompanies.coms.data.exceptions.NoParentPolicyOrResourceException;
import com.unlimitedcompanies.coms.service.system.SystemService;

public class Client
{
	public static void main(String[] args) throws DuplicatedResourcePolicyException, NoParentPolicyOrResourceException
	{
		AnnotationConfigApplicationContext container = new AnnotationConfigApplicationContext();
		container.getEnvironment().setActiveProfiles("production");
		container.register(ApplicationConfig.class);
		container.refresh();
		
		SystemService setupService = container.getBean(SystemService.class);
		
		setupService.initialSetup();
		
		container.close();
	}

}


// MAIN SYSTEM AND SECURITY

// TODO: Pay attention to the annotation @Transactional(rollbackFor = SomeException.class) - it presented errors before and it is being removed now
// TODO: Design a task system
// TODO: if possible design and implement a logging system
