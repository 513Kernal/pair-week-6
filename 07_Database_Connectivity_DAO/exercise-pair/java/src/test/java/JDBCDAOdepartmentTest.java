import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.jdbc.JDBCDepartmentDAO;
import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.SQLException;
import java.util.List;

public class JDBCDAOdepartmentTest {

    private static final String TEST_DEPARTMENT_1 = "departmentTest1";
    private static SingleConnectionDataSource dataSource;
    private JDBCDepartmentDAO dao;
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
        dao = new JDBCDepartmentDAO(dataSource);
    }
    @After
    public void rollback () throws SQLException {
        dataSource.getConnection().rollback();
    }
    private Department makeLocalDepartmentObject(){
        Department testStep = new Department();
        testStep.setName(TEST_DEPARTMENT_1);
        return testStep;
    }
    private void assertDepartmentsAreEqual(Department expected, Department actual){
        Assert.assertEquals(expected.getId(),actual.getId());
        Assert.assertEquals(expected.getName(),actual.getName());
    }
    @Test
    public void saveDepartmentAndReadItBack(){
        Department testStep = makeLocalDepartmentObject();
        dao.createDepartment(testStep);
        Department saveToDepartment = dao.getDepartmentById(testStep.getId());
        Assert.assertNotNull(testStep.getId());
        assertDepartmentsAreEqual(testStep, saveToDepartment);
    }
    @Test
    public void getAllDepartmentsPullsCorrectNumberOfDepartments(){
        Department testStep = makeLocalDepartmentObject();
        List<Department> currentDepartments = dao.getAllDepartments();
        int currentNumberOfDepartments = currentDepartments.size();
        dao.createDepartment(testStep);
        List<Department> departmentsPlusOne = dao.getAllDepartments();
        Assert.assertEquals(currentNumberOfDepartments+1, departmentsPlusOne.size());
    }
    @Test
    public void searchDepartmentByNameReturnsCorrectDepartment(){
        Department testStep = makeLocalDepartmentObject();
        dao.createDepartment(testStep);
        List<Department> departmentNameSomething = dao.searchDepartmentsByName(TEST_DEPARTMENT_1);
        Assert.assertEquals(1, departmentNameSomething.size());
    }
    @Test
    public void saveDepartment_changesDeptCorrectly(){
        Department testDept = makeLocalDepartmentObject();
        dao.createDepartment(testDept);
        List<Department> preList = dao.searchDepartmentsByName(TEST_DEPARTMENT_1 + "a");
        Assert.assertEquals(0, preList.size());
        testDept.setName(TEST_DEPARTMENT_1 + "a");
        dao.saveDepartment(testDept);
        List<Department> depts = dao.searchDepartmentsByName(TEST_DEPARTMENT_1 + "a");
        Assert.assertEquals(1, depts.size());
    }

}
