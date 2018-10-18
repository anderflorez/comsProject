package com.unlimitedcompanies.coms.webservice.test;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class TestController
{
	@RequestMapping("/webServiceTest")
	public ModelAndView displayTest()
	{
		return new ModelAndView("test.jsp");
	}
}
