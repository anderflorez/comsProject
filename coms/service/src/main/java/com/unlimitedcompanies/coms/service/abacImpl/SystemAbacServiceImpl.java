package com.unlimitedcompanies.coms.service.abacImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.ABACDao;
import com.unlimitedcompanies.coms.domain.abac.ABACPolicy;
import com.unlimitedcompanies.coms.domain.abac.PolicyType;
import com.unlimitedcompanies.coms.domain.abac.UserAttribs;
import com.unlimitedcompanies.coms.domain.employee.Employee;
import com.unlimitedcompanies.coms.domain.security.Resource;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.service.abac.SystemAbacService;

@Service
@Transactional
public class SystemAbacServiceImpl implements SystemAbacService
{
	
	@Autowired
	private ABACDao abacDao;
	
	@Override
	public ABACPolicy findPolicy(Resource resource, PolicyType policyType)
	{
		return abacDao.findPolicy(resource, policyType, null);
	}

	@Override
	public UserAttribs getUserAttribs(int userId)
	{
		User user = abacDao.getFullUserWithAttribs(userId);

		UserAttribs userAttribs = new UserAttribs(user.getUsername());
		
		Employee employee = user.getContact().getEmployee();
		List<String> projectNames = new ArrayList<>();
		if (employee != null)
		{
			projectNames = employee.getAssociatedProjectNames();
		}

		userAttribs.setProjects(projectNames);
		userAttribs.setRoles(user.getRoleNames());
		return userAttribs;
	}

}
