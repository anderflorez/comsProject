
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
	public ModelAndView showRoleDetails(@RequestParam(name = "r", required = false) String rId)
	{		
		// TODO: check for errors if the id is null or invalid
		ModelAndView mv = new ModelAndView("/pages/security/roleManagement.jsp");
		Role role;

		if (rId == null || rId.isEmpty())
		{
			// This is a request for the form to create a new role
			role = new Role(null);
			role.setRoleId(null);
			mv.addObject("role", role);
		}
		else 
		{
			// This is a request for the form to update an existing role

			try
			{
				role = authService.searchRoleById(rId);
				mv.addObject("role", role);
			}
			catch (NoResultException e)
			{
				mv.setViewName("/roles");
				mv.addObject("error", "Error: The role provided for updating could not be found");
			}
		}
		
		mv.addObject("user", authUser.getUser());
		return mv;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processRole(Role role)
	{	
		System.out.println("=====> role id received: " + role.getRoleId());
		ModelAndView mv = new ModelAndView("redirect:/roleDetail");
		if (role.getRoleId() == null || role.getRoleId().isEmpty())
		{
			System.out.println("=====> role name received: " + role.getRoleName());
			// This is a request to create a new role
			// TODO: Create some validation for the role name
			if (role.getRoleName() != null && !role.getRoleName().isEmpty())
			{
				role = authService.saveRole(role);
				mv.addObject("r", role.getRoleId());
			}
			else 
			{
				mv.setViewName("/pages/security/roleManagement.jsp");
				Role emptyRole = new Role(null);
				emptyRole.setRoleId(null);
				mv.addObject("role", role);
				mv.addObject("error", "Error: Invalid role name");
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
