package com.unlimitedcompanies.coms.webControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.webappSecurity.AuthenticatedUserDetail;

@Controller
public class DashboardController
{	
	@Autowired
	AuthenticatedUserDetail authenticatedUser;
	
	@RequestMapping("/")
	public ModelAndView showDashboard()
	{
		User loggedUser = authenticatedUser.getUser();
		return new ModelAndView("/pages/dashboard/dashboard.jsp", "user", loggedUser);
	}
}
