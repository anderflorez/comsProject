package com.unlimitedcompanies.coms.service.securityImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.security.ABACDao;
import com.unlimitedcompanies.coms.data.abac.ABACPolicy;
import com.unlimitedcompanies.coms.service.security.ABACService;

@Service
@Transactional
public class ABACServiceImpl implements ABACService
{
	@Autowired
	private ABACDao abacDao;

	@Override
	public int getNumberOfPolicies()
	{
		return abacDao.getNumberOfPolicies();
	}

	@Override
	public void savePolicy(ABACPolicy policy)
	{
		abacDao.savePolicy(policy);
//		return this.findPolicyByName(policy.getPolicyName());
	}

	@Override
	public ABACPolicy findPolicyByName(String policyName)
	{
		return abacDao.findPolicyByName(policyName);
	}
	
}
