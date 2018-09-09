package com.unlimitedcompanies.coms.webControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.webappSecurity.AuthenticatedUserFacade;

@Controller
public class DashboardController
{	
	@Autowired
	AuthenticatedUserFacade authenticatedUserFacade;
	
	@RequestMapping("/")
	public ModelAndView showDashboard()
	{
		User loggedUser = authenticatedUserFacade.authenticatedUser();
		return new ModelAndView("/pages/dashboard/dashboard.jsp", "loggedUser", loggedUser);
	}
}
