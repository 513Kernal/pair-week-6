package com.techelevator.projects.model.jdbc;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.ProjectDAO;
import org.springframework.jdbc.support.rowset.SqlRowSet;
public class JDBCProjectDAO implements ProjectDAO {
	private JdbcTemplate jdbcTemplate;
	public JDBCProjectDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	@Override
	public List<Project> getAllActiveProjects() {
		List<Project> allProjects = new ArrayList<>();
		String sql = "SELECT *\n" +
				"FROM project\n" +
				"WHERE from_date IS NOT NULL AND ((current_date BETWEEN from_date AND to_date) OR to_date IS NULL)";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while (results.next()){
			Project projectResult = mapRowToProject(results);
			allProjects.add(projectResult);
		}
		return allProjects;
	}
	@Override
	public void removeEmployeeFromProject(Long projectId, Long employeeId) {
		String sql = "DELETE FROM project_employee WHERE project_id=? AND employee_id=?";
		jdbcTemplate.update(sql, projectId, employeeId);
	}
	@Override
	public void addEmployeeToProject(Long projectId, Long employeeId) {
		String sql = "INSERT INTO project_employee (project_id, employee_id) VALUES (?, ?)";
		jdbcTemplate.update(sql, projectId, employeeId);
	}
	private Project mapRowToProject(SqlRowSet results) {
		Project project = new Project();
		project.setId(results.getLong("project_id"));
		project.setName(results.getString("name"));
		if (results.getDate("from_date") != null){
			project.setStartDate(results.getDate("from_date").toLocalDate());
		}
		if (results.getDate("to_date") != null) {
			project.setEndDate(results.getDate("to_date").toLocalDate());
		}
		return project;
	}
}