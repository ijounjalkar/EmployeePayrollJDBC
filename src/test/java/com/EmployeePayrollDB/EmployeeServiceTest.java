package com.EmployeePayrollDB;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;
import com.EmployeePayrollDB.Employee;
import com.EmployeePayrollDB.EmployeePayrollService;
import com.EmployeePayrollDB.EmployeePayrollService.IOService;

import java.sql.SQLException;
import java.time.LocalDate;
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
	
	/**
	 * Usecase5: to retrieve employees in the particular dates
	 * 
	 * @throws DatabaseException
	 * @throws SQLException 
	 */
	@Test
	public void givenEmployeePayrollInDB_WhenRetrievedForDateRange_ShouldMatchEmployeeCount() throws DatabaseException, SQLException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<Employee> employeePayrollData = employeePayrollService.readEmployeePayrollDBData(IOService.DB_IO);
		int result = employeePayrollService.getEmployeeForDateRange(LocalDate.of(2019, 01, 01),
				LocalDate.of(2020, 01, 01));
		assertEquals(3, result);
	}
}
