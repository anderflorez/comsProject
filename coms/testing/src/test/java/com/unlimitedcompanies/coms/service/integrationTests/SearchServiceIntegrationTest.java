package com.unlimitedcompanies.coms.service.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.data.exceptions.ConditionMaxLevelException;
import com.unlimitedcompanies.coms.data.exceptions.ExistingConditionGroupException;
import com.unlimitedcompanies.coms.data.exceptions.NonExistingFieldException;
import com.unlimitedcompanies.coms.data.exceptions.IncorrectFieldFormatException;
import com.unlimitedcompanies.coms.data.exceptions.NoLogicalOperatorException;
import com.unlimitedcompanies.coms.data.query.COperator;
import com.unlimitedcompanies.coms.data.query.SearchQuery;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.ContactService;
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
	
	@Autowired
	ContactService contactService;
	
	@Test
	public void saveObjectQueryWithJoinsTest() throws NonExistingFieldException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFields("User");
		Resource contactResource = setupService.findResourceByNameWithFields("Contact");
		Resource roleResource = setupService.findResourceByNameWithFields("Role");
		
		SearchQuery sq = new SearchQuery(userResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact", contactResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("roles"), "role", roleResource);
		
		searchService.storeSearchQuery(sq);

		assertEquals(1, searchService.storedSearchQueriesNum());
		assertEquals(3, searchService.storedSearchQueriesPathNum());
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
	public void findStoredSearchQueryTest() throws NonExistingFieldException
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
		
		assertEquals("select root from User as root left join fetch root.contact as contact left join fetch root.roles as role",
				foundSQ.generateFullQuery());
		assertEquals(sq.getQueryResource().getPathId(), foundSQ.getQueryResource().getPathId());
	}
	
	@Test
	public void saveSingleResultQueryWithJoinsTest() throws NonExistingFieldException
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
		
		// TODO: Move this next type of test for the created db views
		String expectedQuery = "select contact.firstName from User as root "
				+ "left join fetch root.contact as contact "
				+ "left join fetch root.roles as role";
		SearchQuery foundQuery = searchService.findQueryById(sq.getSearchQueryId());
		assertEquals(expectedQuery, foundQuery.generateFullQuery());
		
		assertEquals(1, searchService.storedSearchQueriesNum());
		assertEquals(3, searchService.storedSearchQueriesPathNum());
	}
	
	@Test
	public void saveSingleResultQueryWithConditionsTest()
			throws NonExistingFieldException, IncorrectFieldFormatException, 
				   ExistingConditionGroupException, NoLogicalOperatorException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFields("User");
		Resource contactResource = setupService.findResourceByNameWithFields("Contact");
		Resource roleResource = setupService.findResourceByNameWithFields("Role");
		
		SearchQuery sq = new SearchQuery(userResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact", contactResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("roles"), "role", roleResource);
		sq.assignSingleResultField("contact", "firstName");
		sq.where("role.roleName", COperator.EQUALS, "Administrator");

		searchService.storeSearchQuery(sq);
		
		// TODO: Move this next type of test for the created db views
		String expectedQuery = "select contact.firstName from User as root "
				+ "left join fetch root.contact as contact "
				+ "left join fetch root.roles as role "
				+ "where role.roleName = Administrator";
		SearchQuery foundQuery = searchService.findQueryById(sq.getSearchQueryId());
		assertEquals(expectedQuery, foundQuery.generateFullQuery());
		
		assertEquals(1, searchService.storedSearchQueriesNum());
		assertEquals(2, searchService.storedSearchQueriesPathNum());
	}
	
	@Test
	public void saveObjectQueryWithMultiLevelConditionsTest()
			throws NonExistingFieldException, IncorrectFieldFormatException, ExistingConditionGroupException, 
				   NoLogicalOperatorException, ConditionMaxLevelException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFields("User");
		Resource contactResource = setupService.findResourceByNameWithFields("Contact");
		Resource roleResource = setupService.findResourceByNameWithFields("Role");
		
		SearchQuery sq = new SearchQuery(userResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact", contactResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("roles"), "role", roleResource);
		sq.assignSingleResultField("contact", "firstName");
		sq.where("role.roleName", COperator.EQUALS, "Administrator")
			.and("role.roleName", COperator.NOT_EQUAL, "Manager")
			.or("contact.firstName", COperator.EQUALS, "Administrator")
			.or("root.username", COperator.EQUALS, "administrator")
			.and("contact.email", COperator.EQUALS, "uec_ops_support@unlimitedcompanies.com");

		searchService.storeSearchQuery(sq);
		
		String expectedQuery = "select contact.firstName from User as root "
				+ "left join fetch root.contact as contact "
				+ "left join fetch root.roles as role "
				+ "where role.roleName = Administrator "
				+ "and role.roleName != Manager "
				+ "and (contact.firstName = Administrator "
				+ "or root.username = administrator "
				+ "or (contact.email = uec_ops_support@unlimitedcompanies.com))";
		SearchQuery foundQuery = searchService.findQueryById(sq.getSearchQueryId());
		System.out.println(foundQuery.generateFullQuery());
		assertEquals(expectedQuery, foundQuery.generateFullQuery());
	}
	
	@Test
	public void storeSearchQueryConditionedToOtherSearchTest() 
			throws NonExistingFieldException, IncorrectFieldFormatException, NoLogicalOperatorException, 
				   ExistingConditionGroupException, ConditionMaxLevelException, DuplicateRecordException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFields("User");
		Resource contactResource = setupService.findResourceByNameWithFields("Contact");
		Resource roleResource = setupService.findResourceByNameWithFields("Role");
		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "jdoe@example.com"));
		
		SearchQuery setSearch = new SearchQuery(contactResource);
		setSearch.assignSingleResultField("root", "firstName");
		setSearch.where("root.contactId", COperator.EQUALS, contact.getContactId().toString());
		searchService.storeSearchQuery(setSearch);
		
		SearchQuery sq = new SearchQuery(userResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact", contactResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("roles"), "role", roleResource);
		sq.assignSingleResultField("contact", "firstName");
		sq.where("role.roleName", COperator.EQUALS, "Administrator")
			.and("contact.firstName", COperator.NOT_EQUAL, setSearch)
			.or("contact.firstName", COperator.EQUALS, "Administrator")
			.or("root.username", COperator.EQUALS, "Administrator")
			.and("contact.email", COperator.EQUALS, "uec_ops_support@unlimitedcompanies.com");

		searchService.storeSearchQuery(sq);
		
		String expectedQuery = "select contact.firstName from User as root "
				+ "left join fetch root.contact as contact "
				+ "left join fetch root.roles as role "
				+ "where role.roleName = Administrator "
				+ "and contact.firstName != (select root.firstName from Contact as root where root.contactId = " + contact.getContactId() + ") "
				+ "and (contact.firstName = Administrator "
				+ "or root.username = Administrator "
				+ "or (contact.email = uec_ops_support@unlimitedcompanies.com))";
		
		SearchQuery foundQuery = searchService.findQueryById(sq.getSearchQueryId());
		System.out.println(foundQuery.generateFullQuery());
		assertEquals(expectedQuery, foundQuery.generateFullQuery());
	}
	
	@Test
	public void deleteObjectQueryWithMultiLevelConditionsTest()
			throws NonExistingFieldException, IncorrectFieldFormatException, ExistingConditionGroupException, 
				   NoLogicalOperatorException, ConditionMaxLevelException, DuplicateRecordException
	{
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFields("User");
		Resource contactResource = setupService.findResourceByNameWithFields("Contact");
		Resource roleResource = setupService.findResourceByNameWithFields("Role");
		Contact contact = contactService.saveContact(new Contact("John", null, "Doe", "jdoe@example.com"));
		
		SearchQuery setSearch = new SearchQuery(contactResource);
		setSearch.assignSingleResultField("root", "firstName");
		setSearch.where("root.contactId", COperator.EQUALS, contact.getContactId().toString());
		searchService.storeSearchQuery(setSearch);
		
		SearchQuery sq = new SearchQuery(userResource);
		String sqId = sq.getSearchQueryId();
		sq.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact", contactResource);
		sq.leftJoinFetch(userResource.getResourceFieldByName("roles"), "role", roleResource);
		sq.assignSingleResultField("contact", "firstName");
		sq.where("role.roleName", COperator.EQUALS, "Administrator")
			.and("contact.firstName", COperator.NOT_EQUAL, setSearch)
			.or("contact.firstName", COperator.EQUALS, "Administrator")
			.or("root.username", COperator.EQUALS, "Administrator")
			.and("contact.email", COperator.EQUALS, "uec_ops_support@unlimitedcompanies.com");

		searchService.storeSearchQuery(sq);
		
		searchService.deleteSearchQuery(sqId);
		
		assertThrows(RecordNotFoundException.class, () -> searchService.findQueryById(sqId));
		assertEquals(0, searchService.storedSearchQueriesNum());
		assertEquals(0, searchService.storedSearchQueriesPathNum());
	}
}
