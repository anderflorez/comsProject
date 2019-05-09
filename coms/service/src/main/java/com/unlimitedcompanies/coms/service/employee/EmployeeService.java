package com.unlimitedcompanies.coms.service.employee;

import com.unlimitedcompanies.coms.domain.employee.Employee;

public interface EmployeeService
{
	public void saveEmployee(Employee employee);
	
	public int getNumberOfEmployees();
}
