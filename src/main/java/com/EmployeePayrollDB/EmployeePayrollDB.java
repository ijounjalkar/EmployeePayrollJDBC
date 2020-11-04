package com.EmployeePayrollDB;

import java.sql.Connection;
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
import java.util.List;
import java.util.stream.Collectors;

public class EmployeePayrollDB {
	
	private PreparedStatement employeeStatement;
	private static EmployeePayrollDB employeePayrollDB;

	private EmployeePayrollDB() {
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
}
	
