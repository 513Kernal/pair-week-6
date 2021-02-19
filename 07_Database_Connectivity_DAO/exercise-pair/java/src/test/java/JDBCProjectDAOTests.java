import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.EmployeeDAO;
import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.jdbc.JDBCEmployeeDAO;
import com.techelevator.projects.model.jdbc.JDBCProjectDAO;
import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JDBCProjectDAOTests {
    private static final String TEST_PROJECT_NAME_1 = "xyzxyz";
    private static final String TEST_PROJECT_NAME_2 = "abcabc";
    private static final String TEST_PROJECT_NAME_3 = "XDXDXD";
    private static final String TEST_PROJECT_NAME_4 = "qWeRtY";
    private static final String TEST_PROJECT_NAME_5 = "asdfghjk";
    private static final Long TEST_LONG = 999L;
    private static final LocalDate TEST_DATE = LocalDate.now();
    private static SingleConnectionDataSource dataSource;
    private JDBCProjectDAO dao;
    private JDBCEmployeeDAO eDao; //project DAO to be tested AFTER employee DAO

    @BeforeClass
    public static void setupDataSource(){
        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/projects");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        dataSource.setAutoCommit(false);
        //lines 18-20 are loggine into the database
        //21 could be a way to put rollback up front (look into more)
    }
    @AfterClass
    public static void closeDataSource()throws SQLException {
        dataSource.destroy();
    }
    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        dao = new JDBCProjectDAO(dataSource);

        //create one project with dates in the future, one with dates in the past, one with null
        //start_date, one with null end_date and one with a past start date and future end date
        String sql = "INSERT INTO project (project_id, name, from_date, to_date)" +
                "VALUES (?,?,?,?), (?,?,?,?), (?,?,?,?), (?,?,?,?), (?,?,?,?);";
        jdbcTemplate.update(sql, TEST_LONG, TEST_PROJECT_NAME_1, TEST_DATE.plusDays(3), TEST_DATE.plusDays(5),
                TEST_LONG + 1, TEST_PROJECT_NAME_2, TEST_DATE.minusDays(3), TEST_DATE.minusDays(5),
                TEST_LONG + 2, TEST_PROJECT_NAME_3, null, TEST_DATE,
                TEST_LONG + 3, TEST_PROJECT_NAME_4, TEST_DATE, null,
                TEST_LONG + 4, TEST_PROJECT_NAME_5, TEST_DATE.minusDays(3), TEST_DATE.plusDays(3));

    }
    @After
    public void rollback () throws SQLException {
        dataSource.getConnection().rollback();
    }

    //for the purpose of testing, projects are equivalent if they have a matching ID
    public Boolean projectsAreEqual(Project project, Project otherProject){
       return project.getId().equals(otherProject.getId());
    }
    //iterates through a list to see if a project matches the provided id
    public Boolean listContainsProject(Project project, List<Project> listToCheck){
        boolean result = false;
        for (Project projectInList : listToCheck){
            if (projectInList.getId().equals(project.getId())) {
                result = true;
                break;
            }
        }
        return result;
    }
    //create local Projects that mirror the ones added the the db. Store them in an Arraylist to reference their order
    public ArrayList<Project> makeLocalProjectObects() {
        ArrayList<Project> localProjects = new ArrayList<>();
        //dates in future
        Project p1 = new Project();
        p1.setId(TEST_LONG);
        p1.setName(TEST_PROJECT_NAME_1);
        p1.setStartDate(TEST_DATE.plusDays(3));
        p1.setEndDate(TEST_DATE.plusDays(5));
        //dates in past
        Project p2 = new Project();
        p2.setId(TEST_LONG + 1);
        p2.setName(TEST_PROJECT_NAME_2);
        p2.setStartDate(TEST_DATE.minusDays(3));
        p2.setEndDate(TEST_DATE.minusDays(5));
        //null start date
        Project p3 = new Project();
        p3.setId(TEST_LONG + 2);
        p3.setName(TEST_PROJECT_NAME_3);
        p3.setStartDate(null);
        p3.setEndDate(TEST_DATE.plusDays(1));
        //null end date
        Project p4 = new Project();
        p4.setId(TEST_LONG + 3);
        p4.setName(TEST_PROJECT_NAME_4);
        p4.setStartDate(TEST_DATE.minusDays(1));
        p4.setEndDate(null);
        //start date in past and end date in future
        Project p5 = new Project();
        p5.setId(TEST_LONG + 4);
        p5.setName(TEST_PROJECT_NAME_5);
        p5.setStartDate(TEST_DATE.minusDays(3));
        p5.setEndDate(TEST_DATE.plusDays(5));

        localProjects.add(p1);
        localProjects.add(p2);
        localProjects.add(p3);
        localProjects.add(p4);
        localProjects.add(p5);
        return localProjects;
    }

    //adds a fake employee to database whose id is TEST_LONG
    public void addFakeEmployeeToDatabase() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        eDao = new JDBCEmployeeDAO(dataSource);
        String sql = "INSERT INTO employee (employee_id, first_name," +
                "last_name, birth_date, gender, hire_date) VALUES (?,?,?,?,?,?);";
        jdbcTemplate.update(sql, TEST_LONG, TEST_PROJECT_NAME_1, TEST_PROJECT_NAME_1, TEST_DATE, 'M', TEST_DATE);
    }

    @Test
    public void getAllActiveProjects_returns_all_and_only_active_projects() {
        List<Project> projects = dao.getAllActiveProjects();
        ArrayList<Project> testProjects =  makeLocalProjectObects();
        Project p1 = testProjects.get(0);
        Project p2 = testProjects.get(1);
        Project p3 = testProjects.get(2);
        Project p4 = testProjects.get(3);
        Project p5 = testProjects.get(4);

        //ensure currently active and ongoing project are returned, and no others are returned
        Assert.assertFalse(listContainsProject(p1, projects));
        Assert.assertFalse(listContainsProject(p2, projects));
        Assert.assertFalse(listContainsProject(p3, projects));
        Assert.assertTrue(listContainsProject(p4, projects));
        Assert.assertTrue(listContainsProject(p5, projects));

    }

    @Test
    public void addEmployeeToProject_adds_single_employee_to_project(){
        ArrayList<Project> testProjects =  makeLocalProjectObects();
        Project p5 = testProjects.get(4);
        //add the fake employee and add him to the new project
        addFakeEmployeeToDatabase();
        dao.addEmployeeToProject(TEST_LONG + 4, TEST_LONG);
        //ensure there is exactly one employee on the new project
        List<Employee> employeesOnProject = eDao.getEmployeesByProjectId(TEST_LONG + 4);
        Assert.assertEquals(1, employeesOnProject.size());
    }

    @Test
    public void removeEmployeeFromProject_removes_that_and_only_that_employee_from_project(){
        ArrayList<Project> testProjects =  makeLocalProjectObects();
        Project p5 = testProjects.get(4);
        //add two fake employees and add them to the new project
        addFakeEmployeeToDatabase();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        eDao = new JDBCEmployeeDAO(dataSource);
        String sql = "INSERT INTO employee (employee_id, first_name," +
                "last_name, birth_date, gender, hire_date) VALUES (?,?,?,?,?,?);";
        jdbcTemplate.update(sql, TEST_LONG + 1, TEST_PROJECT_NAME_1, TEST_PROJECT_NAME_1, TEST_DATE, 'M', TEST_DATE);
        dao.addEmployeeToProject(TEST_LONG + 4, TEST_LONG);
        dao.addEmployeeToProject(TEST_LONG + 4, TEST_LONG + 1);
        //ensure there are exactly two employees on the new project
        List<Employee> employeesOnProject = eDao.getEmployeesByProjectId(TEST_LONG + 4);
        Assert.assertEquals(2, employeesOnProject.size());
        //remove an employee then make sure there is exactly one employee on the new project
        dao.removeEmployeeFromProject(TEST_LONG + 4, TEST_LONG);
        List<Employee> employeesOnProjectModified = eDao.getEmployeesByProjectId(TEST_LONG + 4);
        Assert.assertEquals(1, employeesOnProjectModified.size());
    }





}
