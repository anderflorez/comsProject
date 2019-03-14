package com.unlimitedcompanies.coms.service.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.data.exceptions.FieldNotInSearchException;
import com.unlimitedcompanies.coms.data.exceptions.IncorrectFieldFormatException;
import com.unlimitedcompanies.coms.data.query.COperator;
import com.unlimitedcompanies.coms.data.query.SearchQuery;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.service.security.SearchQueryService;
import com.unlimitedcompanies.coms.service.security.SecuritySetupService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
@Transactional
@ActiveProfiles("integrationTesting")
public class SearchServiceIntegrationTest
{
	@Autowired
	SecuritySetupService setupService;
	
	@Autowired
	SearchQueryService searchService;
	
	@Test
	public void saveObjectQueryWithJoinsTest()
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFields("User");
		Resource contactResource = setupService.findResourceByNameWithFields("Contact");
		Resource roleResource = setupService.findResourceByNameWithFields("Role");
		
		SearchQuery sq = new SearchQuery(userResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact", contactResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("roles"), "role", roleResource);
		
		searchService.storeSearchQuery(sq);

		System.out.println();
		System.out.println(sq.generateFullQuery());
		assertEquals(1, searchService.storedSearchQueriesNum());
		assertEquals(3, searchService.storedSearchQueriesPathNum());
	}
	
	@Test
	public void saveSingleResultQueryWithJoinsTest()
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFields("User");
		Resource contactResource = setupService.findResourceByNameWithFields("Contact");
		Resource roleResource = setupService.findResourceByNameWithFields("Role");
		
		SearchQuery sq = new SearchQuery(userResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact", contactResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("roles"), "role", roleResource);
		sq.assignSingleResultField("contact", "firstName");

		searchService.storeSearchQuery(sq);
		
		String expectedQuery = "select contact.firstName from User as root left join fetch root.contact as contact left join fetch root.roles as role;";
		SearchQuery foundQuery = searchService.findQueryById(sq.getSearchQueryId());
		assertEquals(expectedQuery, foundQuery.generateFullQuery());
	}
	
	@Test
	public void saveSingleResultQueryWithConditionsTest()
			throws FieldNotInSearchException, IncorrectFieldFormatException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFields("User");
		Resource contactResource = setupService.findResourceByNameWithFields("Contact");
		Resource roleResource = setupService.findResourceByNameWithFields("Role");
		
		SearchQuery sq = new SearchQuery(userResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact", contactResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("roles"), "role", roleResource);
		sq.assignSingleResultField("contact", "firstName");
		sq.where("role.roleName", COperator.EQUALS, "Administrator", 't');

		searchService.storeSearchQuery(sq);
		
		String expectedQuery = "select contact.firstName from User as root left join fetch root.contact as contact left join fetch root.roles as role where role.roleName = Administrator;";
		SearchQuery foundQuery = searchService.findQueryById(sq.getSearchQueryId());
		assertEquals(expectedQuery, foundQuery.generateFullQuery());
	}
	
	@Test
	public void saveObjectQueryWithMultiLevelConditionsTest()
			throws FieldNotInSearchException, IncorrectFieldFormatException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFields("User");
		Resource contactResource = setupService.findResourceByNameWithFields("Contact");
		Resource roleResource = setupService.findResourceByNameWithFields("Role");
		
		SearchQuery sq = new SearchQuery(userResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact", contactResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("roles"), "role", roleResource);
		sq.assignSingleResultField("contact", "firstName");
		sq.where("role.roleName", COperator.EQUALS, "Administrator", 't')
			.and("role.roleName", COperator.NOT_EQUAL, "Manager", 't')
			.or("contact.firstName", COperator.EQUALS, "Administrator", 't')
			.or("root.username", COperator.EQUALS, "administrator", 't')
			.and("contact.email", COperator.EQUALS, "uec_ops_support@unlimitedcompanies.com", 't');

		searchService.storeSearchQuery(sq);
		
		String expectedQuery = "select contact.firstName from User as root left join fetch root.contact as contact left join fetch root.roles as role where role.roleName = Administrator and role.roleName != Manager and (contact.firstName = Administrator or root.username = administrator or (contact.email = uec_ops_support@unlimitedcompanies.com));";
		SearchQuery foundQuery = searchService.findQueryById(sq.getSearchQueryId());
		System.out.println(foundQuery.generateFullQuery());
		assertEquals(expectedQuery, foundQuery.generateFullQuery());
	}
	
	// This test method should expect to get an exception 
//	@Test
//	public void saveStoredSearchQueryWithWrongFieldsNotAllowedTest()
//	{
//		setupService.checkAllResources();
//		Resource userResource = setupService.findResourceByNameWithFields("User");
//		Resource contactResource = setupService.findResourceByNameWithFields("Contact");
//		Resource roleResource = setupService.findResourceByNameWithFields("Role");
//		
//		SearchQuery sq = new SearchQuery(userResource);
//		// TODO: The next line should expect an exception
//		sq.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact", contactResource)
//		  .leftJoinFetch(userResource.getResourceFieldByName("roles"), "role", roleResource);
//	}
	
	@Test
	public void findStoredSearchQueryTest()
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFields("User");
		Resource contactResource = setupService.findResourceByNameWithFields("Contact");
		Resource roleResource = setupService.findResourceByNameWithFields("Role");
		
		SearchQuery sq = new SearchQuery(userResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact", contactResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("roles"), "role", roleResource);
		searchService.storeSearchQuery(sq);
		
		SearchQuery foundSQ = searchService.findQueryById(sq.getSearchQueryId());
		
		assertEquals("select root from User as root left join fetch root.contact as contact left join fetch root.roles as role;",
					foundSQ.generateFullQuery());
		assertEquals(sq.getQueryResource().getPathId(), foundSQ.getQueryResource().getPathId());
	}
}
