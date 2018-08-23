package com.unlimitedcompanies.coms.webControllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController
{	
	@RequestMapping("/")
	public ModelAndView AddContact()
	{
		return new ModelAndView("/app/dashboard.jsp");
	}
}
