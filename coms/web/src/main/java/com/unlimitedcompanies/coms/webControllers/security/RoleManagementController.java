package com.unlimitedcompanies.coms.webControllers.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.securityService.AuthenticationService;

@Controller
@RequestMapping("/manageRole")
public class RoleManagementController
{	
	@Autowired
	AuthenticationService authenticationService;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showContactDetails(@RequestParam("r") String rId)
	{		
		// TODO: check for errors if the id is null or invalid
		Integer id = Integer.valueOf(rId);
		Role role;
		
		if (id > 0)
		{
			// TODO: show error if object is not found
			role = authenticationService.findRoleById(id);
			
		} else
		{
			role = new Role(null);
		}
		
		ModelAndView mv = new ModelAndView("/pages/security/roleManagement.jsp", "role", role);
		return mv;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processContact(Role role)
	{	
		if (role.getRoleId() != null)
		{
			System.out.println("=========found role id: " + role.getRoleId());
			authenticationService.updateRole(role.getRoleId(), role);
			return new ModelAndView("/pages/security/roleManagement.jsp", "role", role);
		} else
		{
			System.out.println("=========found role id: " + role.getRoleId());
			role = authenticationService.saveRole(role);
			return new ModelAndView("/pages/security/roleManagement.jsp", "role", role);
		}
	}
}
