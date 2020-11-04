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
	
	/**
	 * Usecase6: to perform aggregate functions on the employee table
	 * 
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void givenEmployeePayrollInDB_WhenRetrievedForAverage_ShouldMatchedAverageSalaryForGender()
			throws DatabaseException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		Map<String, Double> genderComputedMap = null;
		genderComputedMap = employeePayrollService.getSalaryAverageByGender();
		assertEquals(true, genderComputedMap.get("M") == 2000000);
		assertEquals(true, genderComputedMap.get("F") == 5000000);

	}

	@Test
	public void givenEmployeePayrollInDB_WhenRetrievedForSum_ShouldMatchedSumSalaryForGender()
			throws DatabaseException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		Map<String, Double> genderComputedMap = null;
		genderComputedMap = employeePayrollService.getSalarySumByGender();
		assertEquals(true, genderComputedMap.get("M") == 6000000);
		assertEquals(true, genderComputedMap.get("F") == 5000000);

	}

	@Test
	public void givenEmployees_WhenRetrievedForMin_ShouldMatchedMinSalaryForGender() throws DatabaseException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		Map<String, Double> genderComputedMap = null;
		genderComputedMap = employeePayrollService.getMinSalaryByGender();
		assertEquals(true, genderComputedMap.get("M") == 1000000);
		assertEquals(true, genderComputedMap.get("F") == 5000000);

	}

	@Test
	public void givenEmployees_WhenRetrievedForMax_ShouldMatchedMaxSalaryForGender() throws DatabaseException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		Map<String, Double> genderComputedMap = null;
		genderComputedMap = employeePayrollService.getMaxSalaryByGender();
		assertEquals(true, genderComputedMap.get("M") == 3000000);
		assertEquals(true, genderComputedMap.get("F") == 5000000);

	}

	@Test
	public void givenEmployees_WhenRetrievedForCount_ShouldMatchedCountForGender() throws DatabaseException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		Map<String, Double> genderComputedMap = null;
		genderComputedMap = employeePayrollService.getCountByGender();
		assertEquals(true, genderComputedMap.get("M") == 3);
		assertEquals(true, genderComputedMap.get("F") == 1);

	}
	
	/**
	 * Usecase7: To insert new Employee to the table
	 * 
	 * @throws SQLException
	 * @throws DatabaseException
	 */
	@Test
	public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() throws SQLException, DatabaseException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.addEmployeeToPayroll("Mark", "M", 5000000.0, LocalDate.now());
		boolean result = employeePayrollService.checkEmployeeDataSync("Mark");
		assertEquals(true, result);
	}
	
	/**
	 * Usecase8: performing the cascading delete operation on database
	 * 
	 * @throws DatabaseException
	 * @throws SQLException 
	 */
	@Test
	public void givenEmployeeDB_WhenAnEmployeeIsDeleted_ShouldSyncWithDB() throws DatabaseException, SQLException {
		EmployeePayrollService employeeService = new EmployeePayrollService();
		employeeService.readEmployeePayrollData(IOService.DB_IO);
		List<Employee> list = employeeService.deleteEmployee("Mark");
		assertEquals(3, list.size());
	}
	
	/*/**
	 * Usecase9: Inserting data according to new database structure Usecase11:
	 * Usecase11: Refactored for the single transaction
	 * 
	 * @throws SQLException
	 * @throws DatabaseException
	 */
	@Test
	public void givenNewEmployee_WhenAddedToPayroll_ShouldBeAddedToDepartment() throws SQLException, DatabaseException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.addEmployeeToPayrollAndDepartment("Mark", "M", 5000000.0, LocalDate.now(), "Sales");
		boolean result = employeePayrollService.checkEmployeeDataSync("Mark");
		assertEquals(true, result);
	}
	/**
	 * Usecase12: Remove employee from payroll
	 * 
	 * @throws DatabaseException
	 * @throws SQLException 
	 */
	@Test
	void givenEmployeeId_WhenRemoved_shouldReturnNumberOfActiveEmployees() throws DatabaseException, SQLException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<Employee> onlyActiveList = employeePayrollService.removeEmployeeFromPayroll(3);
		assertEquals(3, onlyActiveList.size());
	}
}
