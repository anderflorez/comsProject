
package com.unlimitedcompanies.coms.webControllers.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.unlimitedcompanies.coms.securityService.AuthService;

@Controller
@RequestMapping("/manageRole")
public class RoleManagementController
{	
	@Autowired
	AuthService authService;
	
//	@RequestMapping(method = RequestMethod.GET)
//	public ModelAndView showContactDetails(@RequestParam("r") String rId)
//	{		
//		// TODO: check for errors if the id is null or invalid
//		Integer id = Integer.valueOf(rId);
//		Role role;
//		ModelAndView mv = new ModelAndView("/pages/security/roleManagement.jsp");
//		
//		if (id > 0)
//		{
//			// TODO: show error if object is not found
//			role = authService.findRoleByIdWithMembers(id);
//			
//			List<Contact> memberContacts = new ArrayList<>();
//			for(User user : role.getMembers())
//			{
//				memberContacts.add(user.getContact());
//			}
//			
//			mv.addObject("role", role);
//			mv.addObject("memberContacts", memberContacts);
//			
//		} else
//		{
//			role = new Role(null);
//			mv.addObject("role", role);
//			mv.addObject("memberContacts", null);
//		}
//		
//		return mv;
//	}
//	
//	@RequestMapping(method = RequestMethod.POST)
//	public ModelAndView processContact(Role role)
//	{	
//		if (role.getRoleId() != null)
//		{
//			authService.updateRole(role.getRoleId(), role);
//		} else
//		{
//			role = authService.saveRole(role);
//		}
//		return new ModelAndView("redirect:/manageRole?r=" + role.getRoleId());
//	}
}
