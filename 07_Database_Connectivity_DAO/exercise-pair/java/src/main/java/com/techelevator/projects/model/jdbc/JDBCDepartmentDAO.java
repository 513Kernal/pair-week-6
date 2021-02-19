package com.techelevator.projects.model.jdbc;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.DepartmentDAO;
import org.springframework.jdbc.support.rowset.SqlRowSet;
public class JDBCDepartmentDAO implements DepartmentDAO {
	private JdbcTemplate jdbcTemplate;
	public JDBCDepartmentDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	@Override
	public List<Department> getAllDepartments() {
		ArrayList<Department> departments = new ArrayList<>();
		String sql = "SELECT department_id, name FROM department";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while (results.next()) {
			Department departmentResults = mapRowToDepartment(results);
			departments.add(departmentResults);
		}
		return departments;
	}
	@Override
	public List<Department> searchDepartmentsByName(String nameSearch) {
		ArrayList<Department> departmentName = new ArrayList<>();
		nameSearch = "%" + nameSearch + "%";
		String sql = "SELECT department_id, name FROM department WHERE name ILIKE ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, nameSearch);
		while (results.next()) {
			Department departmentResults = mapRowToDepartment(results);
			departmentName.add(departmentResults);
		}
		return departmentName;
	}
	@Override
	public void saveDepartment(Department updatedDepartment) {
		String sql = "UPDATE department SET name=? WHERE department_id=?";
		jdbcTemplate.update(sql, updatedDepartment.getName(), updatedDepartment.getId());
	}
	@Override
	public Department createDepartment(Department newDepartment) {
		String sql = "INSERT INTO department (name, department_id) VALUES(?, ?) ";
		newDepartment.setId(getNextDepartmentID());
		jdbcTemplate.update(sql, newDepartment.getName(), newDepartment.getId());
		return newDepartment;
	}
	@Override
	public Department getDepartmentById(Long id) {
		String sql = "SELECT department_id, name FROM department WHERE department_id=?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
		if (results.next()){
			return mapRowToDepartment(results);
		} else {
			return null;
		}
	}
	private Department mapRowToDepartment (SqlRowSet results){
		Department theDepartment;
		theDepartment = new Department();
		theDepartment.setName(results.getString("name"));
		theDepartment.setId(results.getLong("department_id"));
		return theDepartment;
	}
	private long getNextDepartmentID(){
		SqlRowSet nextID = jdbcTemplate.queryForRowSet("SELECT nextval('seq_department_id')");
		if (nextID.next()){
			return nextID.getLong(1);
		} else {
			throw new RuntimeException("Something went wrong while getting an ID for the new department");
		}
	}
}