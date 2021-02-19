
import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.jdbc.JDBCEmployeeDAO;
import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
public class JDBCEmployeeDAOTests {
    private static final String TEST_EMPLOYEE_1_NAME = "xyzxyz";
    private static final String TEST_EMPLOYEE_2_NAME = "abcabc";
    private static final String TEST_DEPARTMENT_1_NAME = "xyzxyz";
    private static final LocalDate TEST_DATE = LocalDate.now();
    private static final Character TEST_GENDER = 'M';
    private static final String TEST_PROJECT = "qwerty";
    private static final Long TEST_LONG = 9999L; //for companies who have more employees (or anything else) than this,
    private static final Long TEST_LONG_2 = 9998L;//at least they only need to change it in one place
    private static SingleConnectionDataSource dataSource;
    private JDBCEmployeeDAO dao;

    @BeforeClass
    public static void setupDataSource() {
        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/projects");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        dataSource.setAutoCommit(false);
        //lines 18-20 are loggine into the database
        //21 could be a way to put rollback up front (look into more)
    }

    @AfterClass
    public static void closeDataSource() throws SQLException {
        dataSource.destroy();
    }

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        dao = new JDBCEmployeeDAO(dataSource);
        String sql = "INSERT INTO Department(department_id, name) VALUES (?, ?)";
        jdbcTemplate.update(sql, TEST_LONG, TEST_DEPARTMENT_1_NAME);
        String sql2 = "INSERT INTO employee (employee_id, department_id, first_name, last_name, birth_date, gender, hire_date) VALUES (?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql2, TEST_LONG, TEST_LONG, TEST_EMPLOYEE_1_NAME, TEST_EMPLOYEE_1_NAME, TEST_DATE, TEST_GENDER, TEST_DATE);
        String sql3 = "INSERT INTO project (project_id, name) VALUES (?, ?)";
        jdbcTemplate.update(sql3, TEST_LONG, TEST_PROJECT);
    }

    @After
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }

    private Employee makeLocalEmployeeObject() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName(TEST_EMPLOYEE_1_NAME);
        testEmployee.setLastName(TEST_EMPLOYEE_1_NAME);
        testEmployee.setBirthDay(TEST_DATE);
        testEmployee.setGender(TEST_GENDER);
        testEmployee.setHireDate(TEST_DATE);
        return testEmployee;
    }

    //adds additional employee to the db with a different first and last name, for the purpose of comparison
    public Employee addExtraEmployeeTodb() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        dao = new JDBCEmployeeDAO(dataSource);
        String sql2 = "INSERT INTO employee (department_id, first_name, last_name, birth_date, gender, hire_date) VALUES (?,?,?,?,?,?)";
        jdbcTemplate.update(sql2, TEST_LONG, TEST_EMPLOYEE_2_NAME, TEST_EMPLOYEE_2_NAME, TEST_DATE, TEST_GENDER, TEST_DATE);
        Employee testEmployee = new Employee();
        testEmployee.setFirstName(TEST_EMPLOYEE_2_NAME);
        testEmployee.setLastName(TEST_EMPLOYEE_2_NAME);
        testEmployee.setBirthDay(TEST_DATE);
        testEmployee.setGender(TEST_GENDER);
        testEmployee.setHireDate(TEST_DATE);
        return testEmployee;
    }

    //for purpose of testing, Employees considered the same if first and last names match
    public Boolean employee_objects_are_same(Employee testEmployee, Employee otherTestEmployee) {
        return testEmployee.getFirstName().equals(otherTestEmployee.getFirstName()) &&
                testEmployee.getLastName().equals(otherTestEmployee.getLastName());
    }

    //iterates through a list to see if first and last names match any of those for the provided employee
    public Boolean listContainsEmployee(Employee employee, List<Employee> listToCheck) {
        boolean result = false;
        for (Employee employeeSearched : listToCheck) {
            if (employee_objects_are_same(employee, employeeSearched)) {
                result = true;
            }
        }
        return result;
    }

    @Test
    public void getAllEmployeesReturnsAllEmployees() {
        List<Employee> currentEmployees = dao.getAllEmployees();
        int number_ofEmployees = currentEmployees.size();
        Employee testEmployee = addExtraEmployeeTodb();
        List<Employee> currentEmployees_plus_one = dao.getAllEmployees();
        Assert.assertEquals(number_ofEmployees + 1, currentEmployees_plus_one.size());
    }

    @Test
    public void searchEmployeesByNameReturnsEmployeesContainingString() {
        Employee testEmployee = makeLocalEmployeeObject();
        //make sure function returns employee with provided name
        List<Employee> listToCheck = dao.searchEmployeesByName(TEST_EMPLOYEE_1_NAME, TEST_EMPLOYEE_1_NAME);
        Assert.assertEquals(1, listToCheck.size());
        //make sure function ONLY returns employees with provided name
        addExtraEmployeeTodb();
        List<Employee> secondListToCheck = dao.searchEmployeesByName(TEST_EMPLOYEE_1_NAME, TEST_EMPLOYEE_1_NAME);
        Assert.assertEquals(1, listToCheck.size());
    }

    @Test
    public void getAllEmployeesByDepartmentReturnsEmployees() {
        List<Employee> employeeTest = dao.getEmployeesByDepartmentId(TEST_LONG);
        Assert.assertEquals(1, employeeTest.size());
    }

    @Test
    public void getEmployeesWithoutProjectsReturnsEmployeesWithoutProjects() {
        //make sure function returns an employee without a project
        Employee testEmployee = makeLocalEmployeeObject();
        List<Employee> currentEmployees = dao.getEmployeesWithoutProjects();
        Assert.assertTrue(listContainsEmployee(testEmployee, currentEmployees));
        //make sure function doesn't return employees who have projects
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String sql = "INSERT into project_employee (employee_id, project_id) VALUES (?, ?);";
        jdbcTemplate.update(sql, TEST_LONG, TEST_LONG);
        List<Employee> currentEmployeesModified = dao.getEmployeesWithoutProjects();
        Assert.assertFalse(listContainsEmployee(testEmployee, currentEmployeesModified));

    }

    @Test
    public void getEmployeesByProjectId_returns_correct_employees() {
        //make sure function doesn't return employees not on the test project (and that there are no employees in test project)
        List<Employee> employeesOnProject = dao.getEmployeesByProjectId(TEST_LONG);
        Assert.assertEquals(0, employeesOnProject.size());
        //make sure function returns employees on project once added
        Employee testEmployee = makeLocalEmployeeObject();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String sql = "INSERT into project_employee (employee_id, project_id) VALUES (?, ?);";
        jdbcTemplate.update(sql, TEST_LONG, TEST_LONG);
        List<Employee> employeesOnProjectModified = dao.getEmployeesByProjectId(TEST_LONG);
        Assert.assertEquals(1, employeesOnProjectModified.size());
    }

    @Test
    public void changeEmployeeDepartment_reassigns_employee_to_new_dept() {
        //create extra test dept to move fake employee to
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String sql = "INSERT INTO department (department_id, name) VALUES (?,?);";
        jdbcTemplate.update(sql, TEST_LONG_2, "zxcvbxd");
        //make sure function assigns employee to new dept
        dao.changeEmployeeDepartment(TEST_LONG, TEST_LONG_2);
        List<Employee> employeesInDept = dao.getEmployeesByDepartmentId(TEST_LONG_2);
        Assert.assertEquals(1, employeesInDept.size());
    }

}


