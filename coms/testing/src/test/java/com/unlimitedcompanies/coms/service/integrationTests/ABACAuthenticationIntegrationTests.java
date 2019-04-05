package com.unlimitedcompanies.coms.service.integrationTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.data.abac.ABACPolicy;
import com.unlimitedcompanies.coms.data.abac.PolicyType;
import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.data.exceptions.DuplicatedResourcePolicyException;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.service.security.ABACService;
import com.unlimitedcompanies.coms.service.security.SecuritySetupService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
@Transactional
@ActiveProfiles("integrationTesting")
class ABACAuthenticationIntegrationTests
{
	@Autowired
	private ABACService abacService;
	
	@Autowired
	SecuritySetupService setupService;

	@Test
	public void numberOfPoliciesIntegrationTest()
	{
		assertEquals(0, abacService.getNumberOfPolicies(), "Number of policies found in the db failed");
	}
	
	@Test
	public void saveResourcePolicyIntegrationTest() throws DuplicatedResourcePolicyException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFields("User");
		ABACPolicy policy = new ABACPolicy("UserRead", PolicyType.READ, userResource);
		
		abacService.savePolicy(policy);
		
		assertEquals(1, abacService.getNumberOfPolicies(), "Saving resource policy test failed");
	}
	
	@Test
	public void findABACPolicyByNameTest() throws DuplicatedResourcePolicyException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFields("User");
		ABACPolicy policy = new ABACPolicy("UserRead", PolicyType.READ, userResource);
		abacService.savePolicy(policy);
		
		assertEquals(userResource, abacService.findPolicyByName("UserRead"), "Finding policy by name test failed");
	}

}
