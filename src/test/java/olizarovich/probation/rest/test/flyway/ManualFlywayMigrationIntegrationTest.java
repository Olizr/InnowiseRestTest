package olizarovich.probation.rest.test.flyway;

import org.flywaydb.test.FlywayTestExecutionListener;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = FlywayPostgresqlTestConfig.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        FlywayTestExecutionListener.class})
@FlywayTest
public class ManualFlywayMigrationIntegrationTest {
    @Autowired
    private ApplicationContext context;

    private Connection con;

    /**
     * Normal test method nothing done per startup
     */
    @Test
    public void testNoLoad() throws Exception {
        int res = countCustomer();

        assertTrue("This test must runs without an error, because we can not guarantee that this test method run as first. " + res, true);
    }

    /**
     * Made a clean init migrate usage before execution of test methods
     * <p>
     * Expected error due to missing table
     */
    @Test(expected = PSQLException.class)
    @FlywayTest
    public void testMethodLoadWithoutMigration() throws Exception {
        int res = countCustomer();

        assertEquals("Count of customer", 0, res);
    }

    /**
     * Migration with empty tables
     */
    @Test
    @FlywayTest(locationsForMigrate = "migration")
    public void testMethodLoadEmptyTable() throws Exception {
        int res = countCustomer();

        assertEquals("Count of customer", 0, res);
    }

    /**
     * Migration with table 'Person' loaded with data
     */
    @Test
    @FlywayTest(locationsForMigrate = "migrationWithEntity")
    public void testMethodLoadTable() throws Exception {
        int res = countCustomer();

        assertEquals("Count of customer", 3, res);
    }

    /**
     * Open a connection to database for test execution statements
     *
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {

        DataSource ds = (DataSource) context.getBean("dataSource");

        con = ds.getConnection();
        con.setAutoCommit(false);
    }

    /**
     * Close the connection
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        if (con != null) {
            if (!con.isClosed()) {
                con.rollback();
                con.close();
            }
        }
        con = null;
    }

    /**
     * Simple counter query to have simple test inside test methods.
     *
     * @return number of entity in database
     * @throws Exception
     */
    public int countCustomer() throws Exception {
        int result = -1;

        Statement stmt = con.createStatement();
        String query = "select count(*) from Person";

        ResultSet rs = stmt.executeQuery(query);
        rs.next();
        Long cnt = rs.getLong(1);
        result = cnt.intValue();

        rs.close();
        stmt.close();

        return result;
    }

}
