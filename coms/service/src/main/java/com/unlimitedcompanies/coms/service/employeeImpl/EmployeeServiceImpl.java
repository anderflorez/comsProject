package com.unlimitedcompanies.coms.service.employeeImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.employee.EmployeeDao;
import com.unlimitedcompanies.coms.domain.employee.Employee;
import com.unlimitedcompanies.coms.service.employee.EmployeeService;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService
{
	
	@Autowired
	private EmployeeDao employeeDao;

	@Override
	public void saveEmployee(Employee employee)
	{
		employeeDao.saveEmployee(employee);		
	}

	@Override
	public int getNumberOfEmployees()
	{
		return employeeDao.getNumberOfEmployees();
	}

}
