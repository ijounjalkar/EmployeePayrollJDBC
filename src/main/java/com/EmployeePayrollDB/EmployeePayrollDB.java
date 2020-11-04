package com.EmployeePayrollDB;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeePayrollDB {
	
	private PreparedStatement employeeStatement;
	private static EmployeePayrollDB employeePayrollDB;

	EmployeePayrollDB() {
	}

	public static EmployeePayrollDB getInstance() {
		if (employeePayrollDB == null) {
			employeePayrollDB = new EmployeePayrollDB();
		}
		return employeePayrollDB;
	}
	

	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/employee_payroll_service?useSSL=false";
		String userName = "root";
		String password = "Isha1998@";
		Connection connection;
		try {	
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(jdbcURL, userName, password);
		} catch (Exception e) {
			throw new SQLException("Connection was unsuccessful");
		}
		return connection;

	}
	/**
	 * Usecase2: Reading data from the employee_payroll_service
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<Employee> readData() throws SQLException {
		String sql = "Select * from employee_payroll_service; ";
		List<Employee> employeeData = new ArrayList<>();
		try (Connection connection = this.getConnection();) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				double salary = result.getDouble("salary");
				LocalDate start = result.getDate("start").toLocalDate();
				employeeData.add(new Employee(id, name, salary, start));
			}
		} catch (SQLException exception) {
			System.out.println(exception);
		} catch (Exception exception) {
			throw new SQLException("Unable to execute query");
		}
		return employeeData;
	}
	
	/**
	 * Usecase3: Function to update salary in the table for a particular person
	 * 
	 * @param name
	 * @param salary
	 * @return
	 * @throws DatabaseException 
	 */
	private int updateEmployeeUsingStatement(String name, double salary) throws DatabaseException {
		String sql = String.format("Update employee_payroll_service set salary = %.2f where name = '%s';", salary,
				name);
		int result = 0;
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			result =  statement.executeUpdate(sql);
		}
		catch(SQLException e) {
			throw new DatabaseException("Unable to update");
		}
		return result;
	}

	public List<Employee> getEmployeeData(String name) throws  DatabaseException, SQLException {
		return readData().stream().filter(employee -> employee.name.equals(name)).collect(Collectors.toList());
		
	}

	public int updateEmployeeData(String name, double salary) throws DatabaseException {
		return this.updateEmployeeUsingStatement(name, salary);
	}
	public List<Employee> getEmployeePayrollData(String name) {
		List<Employee> employeePayrollList = null;
		if (this.employeeStatement == null)
			this.preparedStatementForEmployeeData();
		try {
			employeeStatement.setString(1, name);
			ResultSet resultSet = employeeStatement.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private List<Employee> getEmployeePayrollData(ResultSet resultSet) {
		List<Employee> employeePayrollList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				LocalDate start = resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new Employee(id, name, salary, start));
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private void preparedStatementForEmployeeData() {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM employee_payroll_service WHERE name = ?";
			employeeStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Usecase4: Prepared Statement for the payroll database
	 */
	private void preparedStatementForEmployeeData1() {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM employee_payroll_service WHERE name = ?";
			employeeStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Usecase5: Implementing query to find employees joined between the particular
	 * dates
	 * 
	 * @param start
	 * @param end
	 * @return
	 * @throws DatabaseException
	 */
	public int getEmployeeForDateRange(LocalDate start, LocalDate end) throws DatabaseException {
		String sql = String.format("Select * from employee_payroll_service where start between '%s' and '%s' ;",Date.valueOf(start), Date.valueOf(end));
		List<Employee> employeeData = new ArrayList<>();
		try (Connection connection = this.getConnection();) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			employeeData = this.getEmployeePayrollData(resultSet);
		} catch (Exception exception) {
			throw new DatabaseException("Unable to execute query");
		}
		return employeeData.size();
	}
	
	/**
	 * Usecase6: performing Aggregate functions query on the employee table
	 * 
	 * @param function
	 * @return
	 * @throws DatabaseException
	 */
	public Map<String, Double> getEmployeesByFunction(String function) throws DatabaseException {
		Map<String, Double> aggregateFunctionMap = new HashMap<>();
		String sql = String.format("Select gender, %s(salary) from employee_payroll_service group by gender ; ",
				function);
		try (Connection connection = this.getConnection()) {
			Statement statement = (Statement) connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				String gender = resultSet.getString(1);
				Double salary = resultSet.getDouble(2);
				aggregateFunctionMap.put(gender, salary);
			}
		} catch (SQLException exception) {
			throw new DatabaseException("Unable to execute " + function);
		}
		return aggregateFunctionMap;
	}
	/**
	 * Usecase7: Inserting new employee into the table using JDBC transaction
	 * Usecase8: Inserting employee data in employee as well as payroll table
	 * 
	 * @param name
	 * @param gender
	 * @param salary
	 * @param start
	 * @return
	 * @throws SQLException
	 * @throws DatabaseException
	 */
	public Employee addEmployeeToPayroll(String name, String gender, double salary, LocalDate start)
			throws SQLException, DatabaseException {
		int employeeId = -1;
		Connection connection = null;
		Employee employee = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format("INSERT INTO employee_payroll_service (name, gender, salary, start) "
					+ "VALUES ('%s','%s','%s','%s')", name, gender, salary, Date.valueOf(start));
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
			throw new DatabaseException("Unable to add new employee");
		}
		try (Statement statement = connection.createStatement()) {
			double deductions = salary * 0.2;
			double taxable_pay = salary - deductions;
			double tax = taxable_pay * 0.1;
			double netPay = salary - tax;
			String sql = String.format(
					"INSERT INTO payroll_details (employee_id, basic_pay, deductions, taxable_pay, tax, net_pay) "
							+ "VALUES ('%s','%s','%s','%s','%s','%s')",
					employeeId, salary, deductions, taxable_pay, tax, netPay);
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 1) {
				employee = new Employee(employeeId, name, salary, start, gender);
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
			throw new DatabaseException("Unable to add payroll details of  employee");
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return employee;
	}
	/**
	 * Usecase8: Performing the cascading delete on the employee table
	 * 
	 * @param name
	 * @throws DatabaseException
	 */
	public void deleteEmployee(String name) throws DatabaseException {
		String sql = String.format("DELETE from employee_payroll_service where name = '%s';", name);
		try {
			Connection connection = this.getConnection();
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);
		} catch (SQLException exception) {
			throw new DatabaseException("Unable to delete data");
		}
	}

	/**
	 * Usecase10: Adding the employee to the given department
	 * 
	 * @param name
	 * @param gender
	 * @param salary
	 * @param start
	 * @param department
	 * @return
	 * @throws DatabaseException
	 * @throws SQLException 
	 */
	public Employee addEmployeeToDepartment(String name, String gender, double salary, LocalDate start,
			String department) throws DatabaseException, SQLException {
		Employee employee = addEmployeeToPayroll(name, gender, salary, start);
		String sql = String.format(
				"INSERT INTO department (employee_id,department_id, department_name) " + "VALUES ('%s','%s','%s')",
				employee.id, 1, department);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 1) {
				employee = new Employee(employee.id, name, salary, start, department);
			}
		} catch (SQLException e) {
			throw new DatabaseException("Unable to add department details of  employee");
		}
		return employee;
	}
	
	/**
	 * Usecase7: Inserting new employee into the table using JDBC transaction
	 * Usecase8: Inserting employee data in employee as well as payroll table
	 * Usecase9: Adding the employee to the given department
	 * Usecase11: Making all insertion as a single transaction
	 */
	 public Employee addEmployeeToPayrollAndDepartment(String name, String gender, double salary, LocalDate start,String department)
			throws SQLException, DatabaseException {
		int employeeId = -1;
		Connection connection = null;
		Employee employee = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format("INSERT INTO employee_payroll_service (name, gender, salary, start) "
					+ "VALUES ('%s','%s','%s','%s')", name, gender, salary, Date.valueOf(start));
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
			throw new DatabaseException("Unable to add new employee");
		}
		try (Statement statement = connection.createStatement()) {
			double deductions = salary * 0.2;
			double taxable_pay = salary - deductions;
			double tax = taxable_pay * 0.1;
			double netPay = salary - tax;
			String sql = String.format(
					"INSERT INTO payroll_details (employee_id, basic_pay, deductions, taxable_pay, tax, net_pay) "
							+ "VALUES ('%s','%s','%s','%s','%s','%s')",
					employeeId, salary, deductions, taxable_pay, tax, netPay);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
			throw new DatabaseException("Unable to add payroll details of  employee");
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"INSERT INTO department (employee_id,department_id, department_name) "
							+ "VALUES ('%s','%s','%s')",
					employeeId,1, department);
			int rowAffected = statement.executeUpdate(sql);
			if(rowAffected == 1) {
				employee = new Employee(employeeId,name, salary, gender,start, department);
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
			throw new DatabaseException("Unable to add department details of  employee");
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return employee;
	}
}

