
package com.unlimitedcompanies.coms.webControllers.security;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.securityService.AuthService;
import com.unlimitedcompanies.coms.webappSecurity.AuthenticatedUserDetail;

@Controller
@RequestMapping("/manageRole")
public class RoleManagementController
{
	@Autowired
	AuthenticatedUserDetail authUser;
	
	@Autowired
	AuthService authService;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showContactDetails(@RequestParam(name = "r", required = false) String rId)
	{		
		// TODO: check for errors if the id is null or invalid
		ModelAndView mv = new ModelAndView("/pages/security/roleManagement.jsp");
		Role role;

		if (rId == null)
		{
			// This is a request for the form to create a new role
			role = new Role(null);
			mv.addObject("role", role);
		}
		else 
		{
			// This is a request for the form to update an existing role

			try
			{
				Integer id = Integer.valueOf(rId);
				role = authService.searchRoleById(id);
				mv.addObject("role", role);
			}
			catch (NoResultException e)
			{
				mv.setViewName("/roles");
				mv.addObject("error", "Error: The role provided for updating could not be found");
			}
			catch (NumberFormatException e)
			{
				mv.setViewName("/roles");
				mv.addObject("error", "Error: The role provided for updating is invalid");
			}
		}
		
		mv.addObject("user", authUser.getUser());
		return mv;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processContact(Role role)
	{	
		ModelAndView mv = new ModelAndView("redirect:/roleDetail");
		if (role.getRoleId() == null)
		{
			// This is a request to create a new role
			// TODO: Create some validation for the role name
			if (role.getRoleName() != null)
			{
				role = authService.saveRole(role);
				mv.addObject("r", role.getRoleId());
			}
			else 
			{
				mv.setViewName("/manageRole");
				mv.addObject("error", "Error: Invalid username");
			}
		} 
		else
		{
			// This is a request to edit an existing role
			try
			{
				// TODO: Create some validation for the role name
				role = authService.updateRole(role.getRoleId(), role);
				mv.addObject("r", role.getRoleId());
			} 
			catch (NoResultException e)
			{
				mv.setViewName("/roles");
				mv.addObject("error", "Error: The role being updated could not be found");
			}
		}
		
		mv.addObject("user", authUser.getUser());
		return mv;
	}
}
