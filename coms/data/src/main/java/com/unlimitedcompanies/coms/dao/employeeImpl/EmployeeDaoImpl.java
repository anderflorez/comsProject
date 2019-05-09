package com.unlimitedcompanies.coms.dao.employeeImpl;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unlimitedcompanies.coms.dao.employee.EmployeeDao;
import com.unlimitedcompanies.coms.domain.employee.Employee;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class EmployeeDaoImpl implements EmployeeDao
{
	@PersistenceContext
	private EntityManager em;

	@Override
	public void saveEmployee(Employee employee)
	{
		em.persist(employee);
	}

	@Override
	public int getNumberOfEmployees()
	{
		BigInteger bigInt = (BigInteger) em.createNativeQuery("SELECT COUNT(employeeId) FROM employees").getSingleResult();
		return bigInt.intValue();
	}

}
