package com.unlimitedcompanies.coms.ws.controllers.abac;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.unlimitedcompanies.coms.data.exceptions.InvalidPolicyException;
import com.unlimitedcompanies.coms.domain.abac.AbacPolicy;
import com.unlimitedcompanies.coms.domain.abac.Resource;
import com.unlimitedcompanies.coms.service.exceptions.NoResourceAccessException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotCreatedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.AbacService;
import com.unlimitedcompanies.coms.ws.config.RestLinks;
import com.unlimitedcompanies.coms.ws.reps.ErrorRep;
import com.unlimitedcompanies.coms.ws.reps.abac.PolicyCollectionResponse;
import com.unlimitedcompanies.coms.ws.reps.abac.PolicyDTO;

@RestController
public class PolicyRestController
{
	@Autowired
	AbacService abacService;
	
	@RequestMapping(value = RestLinks.URI_BASE + "policies", method = RequestMethod.GET)
	public PolicyCollectionResponse getAllPolicies(@RequestParam(name = "epp", required = false) Integer epp,
									 @RequestParam(name = "pag", required = false) Integer pag) throws NoResourceAccessException, RecordNotFoundException
	{
		if (epp == null) epp = 10;
		if (pag == null) pag = 1;
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		
		List<AbacPolicy> policies = abacService.searchPoliciesByRange(epp, pag, userDetails.getUsername());
		PolicyCollectionResponse policyCollection = new PolicyCollectionResponse(policies);
		
		// TODO: Add support for links as well as for previous and next page links
		Link selfLink = linkTo(methodOn(PolicyRestController.class).getAllPolicies(epp, pag)).withSelfRel();
		policyCollection.add(selfLink);
		
		return policyCollection;
	}
	
	@RequestMapping(value = RestLinks.URI_BASE + "policy/{id}", method = RequestMethod.GET)
	public PolicyDTO getPolicyById(@PathVariable String id) throws NoResourceAccessException, RecordNotFoundException
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		
		AbacPolicy abacPolicy = abacService.searchPolicyById(id, userDetails.getUsername());
		
		PolicyDTO policyDTO = new PolicyDTO(abacPolicy);
		
		return policyDTO;
	}
	
	@RequestMapping(value = RestLinks.URI_BASE + "policy", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public void saveNewPolicy(@RequestBody PolicyDTO policyDTO) throws RecordNotFoundException, InvalidPolicyException, NoResourceAccessException
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		
		if(policyDTO.getResource() == null)
		{
			throw new RecordNotFoundException("The policy resource could not be found");
		}
		Resource resource = abacService.searchResourceByNameWithFields(policyDTO.getResource().getResourceName());
		AbacPolicy abacPolicy = policyDTO.getAbacPolicy(resource);
		abacService.savePolicy(abacPolicy, userDetails.getUsername());
	}
	
	@RequestMapping(value = RestLinks.URI_BASE + "policy", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.OK)
	public void updatePolicy(@RequestBody PolicyDTO policyDTO) 
			throws RecordNotFoundException, InvalidPolicyException, NoResourceAccessException, RecordNotCreatedException
	{		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		
		if(policyDTO.getResource() == null)
		{
			throw new RecordNotFoundException("The policy resource could not be found");
		}
		Resource resource = abacService.searchResourceByNameWithFields(policyDTO.getResource().getResourceName());
		AbacPolicy abacPolicy = policyDTO.getAbacPolicy(resource);
		abacService.updatePolicy(policyDTO.getAbacPolicyId(), abacPolicy, userDetails.getUsername());
	}
	
	@RequestMapping(value = RestLinks.URI_BASE + "policy/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void deletePolicy(@PathVariable String id) throws NoResourceAccessException, RecordNotFoundException
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		
		abacService.deletePolicy(id, userDetails.getUsername());
	}
	
	@ExceptionHandler(NoResourceAccessException.class)
	public ResponseEntity<ErrorRep> noResourceAccessExceptionHandler(NoResourceAccessException e)
	{
		ErrorRep errorResponse = new ErrorRep();
		errorResponse.setStatusCode(HttpStatus.FORBIDDEN.value());
		errorResponse.addError("You are not authorized to access this resource. Please contact your system administrator");
		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<ErrorRep> recordNotFoundExceptionHandler(RecordNotFoundException e) 
	{
		ErrorRep errorResponse = new ErrorRep();
		errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
		errorResponse.addError(e.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(RecordNotCreatedException.class)
	public ResponseEntity<ErrorRep> recordNotCreatedExceptionHandler(RecordNotCreatedException e) 
	{
		ErrorRep errorResponse = new ErrorRep();
		errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		errorResponse.addError("The new record was not successfully saved");
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(InvalidPolicyException.class)
	public ResponseEntity<ErrorRep> InvalidPolicyExceptionHandler(InvalidPolicyException e) 
	{
		ErrorRep errorResponse = new ErrorRep();
		errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
		errorResponse.addError(e.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
}
