package com.unlimitedcompanies.coms.dao.employee;

import com.unlimitedcompanies.coms.domain.employee.Employee;

public interface EmployeeDao
{
	public void saveEmployee(Employee employee);

	public int getNumberOfEmployees();
}
