package com.unlimitedcompanies.coms.testClient;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.unlimitedcompanies.coms.data.config.ApplicationConfig;
import com.unlimitedcompanies.coms.data.exceptions.ConditionMaxLevelException;
import com.unlimitedcompanies.coms.data.exceptions.ExistingConditionGroupException;
import com.unlimitedcompanies.coms.data.exceptions.IncorrectFieldFormatException;
import com.unlimitedcompanies.coms.data.exceptions.NoLogicalOperatorException;
import com.unlimitedcompanies.coms.data.exceptions.NonExistingFieldException;
import com.unlimitedcompanies.coms.data.query.COperator;
import com.unlimitedcompanies.coms.data.query.SearchQuery;
import com.unlimitedcompanies.coms.domain.security.Contact;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.service.exceptions.DuplicateRecordException;
import com.unlimitedcompanies.coms.service.security.ContactService;
import com.unlimitedcompanies.coms.service.security.SearchQueryService;
import com.unlimitedcompanies.coms.service.security.SecuritySetupService;

public class SearchDBTest
{	
	public static void main(String[] args)
	{
		AnnotationConfigApplicationContext container = new AnnotationConfigApplicationContext();
		container.getEnvironment().setActiveProfiles("production");
		container.register(ApplicationConfig.class);
		container.refresh();
		
		SecuritySetupService setupService = container.getBean(SecuritySetupService.class);
		ContactService contactService = container.getBean(ContactService.class);
		SearchQueryService searchService = container.getBean(SearchQueryService.class);
		
		setupService.checkAllResources();
		Resource userResource = setupService.findResourceByNameWithFields("User");
		Resource contactResource = setupService.findResourceByNameWithFields("Contact");
		Resource roleResource = setupService.findResourceByNameWithFields("Role");
		Contact contact = contactService.searchContactByEmail("jdoe@example.com");
//		try
//		{
//			contact = contactService.saveContact(new Contact("John", null, "Doe", "jdoe@example.com"));
//		}
//		catch (DuplicateRecordException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		SearchQuery setSearch = new SearchQuery(contactResource);
		try
		{
			setSearch.assignSingleResultField("root", "firstName");
			setSearch.where("root.contactId", COperator.EQUALS, contact.getContactId().toString());
		}
		catch (NonExistingFieldException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IncorrectFieldFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ExistingConditionGroupException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoLogicalOperatorException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		searchService.storeSearchQuery(setSearch);
		
		SearchQuery sq = new SearchQuery(userResource);
		try
		{
			sq.leftJoinFetch(userResource.getResourceFieldByName("contact"), "contact", contactResource);
			sq.leftJoinFetch(userResource.getResourceFieldByName("roles"), "role", roleResource);
			sq.assignSingleResultField("contact", "firstName");
			sq.where("role.roleName", COperator.EQUALS, "Administrator")
			.and("contact.firstName", COperator.NOT_EQUAL, setSearch)
			.or("contact.firstName", COperator.EQUALS, "Administrator")
			.or("root.username", COperator.EQUALS, "Administrator")
			.and("contact.email", COperator.EQUALS, "uec_ops_support@unlimitedcompanies.com");
		}
		catch (NonExistingFieldException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IncorrectFieldFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoLogicalOperatorException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ExistingConditionGroupException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ConditionMaxLevelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		searchService.storeSearchQuery(sq);
		
		container.close();
	}

}
