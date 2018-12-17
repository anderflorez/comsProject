package com.unlimitedcompanies.coms.webControllers.security;

import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.unlimitedcompanies.coms.domain.security.Role;
import com.unlimitedcompanies.coms.service.security.AuthService;
import com.unlimitedcompanies.coms.service.security.ContactService;
import com.unlimitedcompanies.coms.webappSecurity.AuthenticatedUserDetail;

@Controller
public class ViewSecurityObjectController
{	
	@Autowired
	AuthenticatedUserDetail authUser;
	
	@Autowired
	ContactService contactService;
	
	@Autowired
	AuthService authService;
	
	@RequestMapping("/roles")
	public ModelAndView showRoles()
	{
		List<Role> allRoles = authService.searchAllRoles();
		ModelAndView mv = new ModelAndView("/pages/security/roleView.jsp");
		mv.addObject("roles", allRoles);
		mv.addObject("user", authUser.getUser());
		return mv;
	}
	
	@RequestMapping("/roleDetail")
	public ModelAndView showRoleDetails(@RequestParam("r") String id)
	{
		ModelAndView mv = new ModelAndView("/pages/security/roleDetails.jsp");
		Role role;
		try
		{
			role = authService.searchRoleByIdWithMembers(id);
			mv.addObject("role", role);
			if (role.getMembers().size() > 0)
			{
				mv.addObject("userMembers", role.getMembers());
			}
		} 
		catch (NoResultException e)
		{
			role = new Role(null);
			mv.addObject("role", role);
			mv.addObject("error", "Error: The role provided could not be found");
		}
		
		mv.addObject("user", authUser.getUser());
		return mv;
	}
	
	@RequestMapping(value = "/deleteRole", method = RequestMethod.POST)
	public ModelAndView deleteRole(String roleId)
	{
		ModelAndView mv = new ModelAndView("/roles");
		try
		{
			System.out.println("=====> Role id obtained: " + roleId);
			Role role = authService.searchRoleById(roleId);
			if (role.getRoleId().equals("1"))
			{
				mv.addObject("error", "Error: The administrator role cannot be deleted");
			}
			else 
			{
				authService.deleteRole(roleId);				
			}
		} 
		catch (NoResultException e)
		{
			mv.addObject("error", "Error: The role to be deleted could not be found");
		}
		return mv;
	}

}
