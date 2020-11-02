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
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() throws SQLException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<Employee> employeePayrollData = employeePayrollService.readEmployeePayrollDBData(IOService.DB_IO);
		assertEquals(5, employeePayrollData.size());
	}
}
