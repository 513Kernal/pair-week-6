package com.techelevator.projects.model.jdbc;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.EmployeeDAO;
import org.springframework.jdbc.support.rowset.SqlRowSet;
public class JDBCEmployeeDAO implements EmployeeDAO {
	private JdbcTemplate jdbcTemplate;
	public JDBCEmployeeDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	@Override
	public List<Employee> getAllEmployees() {
		List<Employee> allEmployees = new ArrayList<>();
		String sql = "SELECT employee_id, department_id, first_name, last_name, birth_date, gender, hire_date FROM employee";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while (results.next()){
			Employee employeeResult = mapRowToEmployee(results);
			allEmployees.add(employeeResult);
		}
		return allEmployees;
	}
	@Override
	public List<Employee> searchEmployeesByName(String firstNameSearch, String lastNameSearch) {
		List<Employee> allEmployees = new ArrayList<>();
		String sql = "SELECT employee_id, department_id, first_name, last_name, birth_date, gender, hire_date FROM employee " + "WHERE first_name ILIKE ? OR last_name ILIKE ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql,  firstNameSearch,  lastNameSearch );
		while (results.next()){
			Employee employeeResult = mapRowToEmployee(results);
			allEmployees.add(employeeResult);
		}
		return allEmployees;
	}
	@Override
	public List<Employee> getEmployeesByDepartmentId(long id) {
		List<Employee> allEmployees = new ArrayList<>();
		String sql = "SELECT employee_id, department_id, first_name, last_name, birth_date, gender, hire_date FROM employee " + "WHERE department_id= ? ";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
		while (results.next()){
			Employee employeeResult = mapRowToEmployee(results);
			allEmployees.add(employeeResult);
		}
		return allEmployees;
	}
	@Override
	public List<Employee> getEmployeesWithoutProjects() {
		List<Employee> allEmployeesWithoutProjects = new ArrayList<>();
		String sql = "SELECT employee.employee_id, employee.department_id, employee.first_name, employee.last_name, employee.birth_date, employee.gender, employee.hire_date, COUNT(project_employee.project_id)\n" +
				"FROM employee\n" +
				"LEFT JOIN project_employee ON project_employee.employee_id = employee.employee_id\n" +
				"GROUP BY employee.employee_id";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while (results.next()) {
			if (results.getLong("count") == 0) {
				Employee employeeResult = mapRowToEmployee(results);
				allEmployeesWithoutProjects.add(employeeResult);
			}
		}
		return allEmployeesWithoutProjects;
	}
	@Override
	public List<Employee> getEmployeesByProjectId(Long projectId) {
		List<Employee> allEmployees = new ArrayList<>();
		String sql = "SELECT employee.employee_id, employee.department_id, employee.first_name, employee.last_name, employee.birth_date, employee.gender, employee.hire_date FROM employee " +
				"LEFT JOIN project_employee ON project_employee.employee_id = employee.employee_id\n" +
				"WHERE project_employee.project_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, projectId);
		while (results.next()){
			Employee employeeResult = mapRowToEmployee(results);
			allEmployees.add(employeeResult);
		}
		return allEmployees;
	}
	@Override
	public void changeEmployeeDepartment(Long employeeId, Long departmentId) {
		String sql = "UPDATE employee SET department_id=? WHERE employee_id=?";
		jdbcTemplate.update(sql, departmentId, employeeId);
	}
	private Employee mapRowToEmployee(SqlRowSet result){
		Employee employee = new Employee();
		employee.setId(result.getLong("employee_id"));
		employee.setDepartmentId(result.getLong("department_id"));
		employee.setFirstName(result.getString("first_name"));
		employee.setLastName(result.getString("last_name"));
		employee.setBirthDay(result.getDate("birth_date").toLocalDate());
		employee.setGender(result.getString("gender").charAt(0));
		employee.setHireDate(result.getDate("hire_date").toLocalDate());
		return employee;
	}
}