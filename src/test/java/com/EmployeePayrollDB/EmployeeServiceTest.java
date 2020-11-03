package com.EmployeePayrollDB;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;
import com.EmployeePayrollDB.Employee;
import com.EmployeePayrollDB.EmployeePayrollService;
import com.EmployeePayrollDB.EmployeePayrollService.IOService;

import java.sql.SQLException;
import java.util.*;

public class EmployeeServiceTest {

	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() throws DatabaseException, SQLException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<Employee> employeePayrollData = employeePayrollService.readEmployeePayrollDBData(IOService.DB_IO);
		assertEquals(5, employeePayrollData.size());
	}
	
	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldBeInSync() throws DatabaseException, SQLException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<Employee> employeePayrollData = employeePayrollService.readEmployeePayrollDBData(IOService.DB_IO);
		employeePayrollService.updateEmployeeSalary("Terisa", 5000000);
		employeePayrollService.readEmployeePayrollDBData(EmployeePayrollService.IOService.DB_IO);
		boolean result = employeePayrollService.checkEmployeeDataSync("Terisa");
		assertEquals(true,result);
	}
}
