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
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotDeletedException;
import com.unlimitedcompanies.coms.service.exceptions.RecordNotFoundException;
import com.unlimitedcompanies.coms.service.security.AuthService;
import com.unlimitedcompanies.coms.service.security.ContactService;
import com.unlimitedcompanies.coms.webFormObjects.UserForm;
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
	
	@RequestMapping("/users")
	public ModelAndView showUsers(@RequestParam(name = "error", required = false) String error)
	{
		List<User> allUsers = authService.searchAllUsers();
		ModelAndView mv = new ModelAndView("/pages/security/userView.jsp");
		mv.addObject("users", allUsers);
		mv.addObject("user", authUser.getUser());
		if (error != null)
		{
			mv.addObject("error", error);
		}
		return mv;
	}
	
	@RequestMapping("/userDetail")
	public ModelAndView showUserDetails(@RequestParam("uid") String id) throws RecordNotFoundException
	{		
		ModelAndView mv = new ModelAndView("/pages/security/userDetails.jsp");
		UserForm userForm;
		try
		{
			int userId = Integer.valueOf(id);
			userForm = new UserForm(authService.searchUserByUserId(userId));
		} 
		catch (NoResultException e)
		{
			userForm = new UserForm();
			mv.addObject("error", "Error: The user could not be found");
		}
		catch (NumberFormatException e)
		{
			userForm = new UserForm();
			mv.addObject("error", "Error: The user information provided is invalid");
		}
		mv.addObject("userForm", userForm);
		mv.addObject("user", authUser.getUser());
		return mv;
	}
	
	@RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
	public ModelAndView deleteUser(int userId) throws RecordNotFoundException, RecordNotDeletedException
	{
		ModelAndView mv = new ModelAndView("/users");
		try
		{
			User user = authService.searchUserByUserId(userId);
			Role adminRole = authService.searchRoleByIdWithMembers("1");
			if (adminRole.getMembers().size() == 1 && adminRole.getMembers().contains(user))
			{
				mv.addObject("error", "Error: The last administrator user cannot be deleted");
			}
			else 
			{
				authService.deleteUser(userId);				
			}
			
		} 
		catch (NoResultException e)
		{
			mv.addObject("error", "Error: The user to be deleted could not be found");
		}
		return mv;
	}
	
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
