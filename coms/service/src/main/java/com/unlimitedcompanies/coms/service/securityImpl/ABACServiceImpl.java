package com.unlimitedcompanies.coms.service.securityImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.ABACDao;
import com.unlimitedcompanies.coms.domain.security.User;
import com.unlimitedcompanies.coms.domain.security.UserAttributes;
import com.unlimitedcompanies.coms.service.security.ABACService;

@Service
@Transactional
public class ABACServiceImpl implements ABACService
{
	@Autowired
	private ABACDao abacDao;
	
	@Override
	public UserAttributes getUserAttributes()
	{
		User user = abacDao.getUserWithAttributes();
		UserAttributes userAttributes = new UserAttributes(user);
		return userAttributes;
	}

}
