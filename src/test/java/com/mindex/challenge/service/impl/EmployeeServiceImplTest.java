package com.mindex.challenge.service.impl;

import java.util.List;
import java.util.ArrayList;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.data.ReportingStructure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String employeeIdReportURL;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        employeeIdReportURL = "http://localhost:" + port + "/employee/{id}/report";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    @Test
    public void testComputeHasDirectReports() {

        // Using employee data that exists in the database snapshot because we don't
        // need to test create and read to test compute. They have their own unit test.
        Employee testEmployee1 = new Employee();
        testEmployee1.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        testEmployee1.setFirstName("John");
        testEmployee1.setLastName("Lennon");
        testEmployee1.setPosition("Development Manager");
        testEmployee1.setDepartment("Engineering");

        Employee testEmployee2 = new Employee();
        testEmployee2.setEmployeeId("b7839309-3348-463b-a7e3-5de1c168beb3");
        Employee testEmployee3 = new Employee();
        testEmployee3.setEmployeeId("03aa1462-ffa9-4978-901b-7c001562cf6f");
        List<Employee> directReports = new ArrayList<Employee>();
        directReports.add(testEmployee2);
        directReports.add(testEmployee3);
        testEmployee1.setDirectReports(directReports);
        
        int expected = 4;
        ReportingStructure testReportingStructure = new ReportingStructure();
        testReportingStructure.setEmployee(testEmployee1);
        testReportingStructure.setNumberOfReports(expected);


        // Compute check 1
        ReportingStructure createdReportingStructure = restTemplate.getForEntity(employeeIdReportURL, ReportingStructure.class, testEmployee1.getEmployeeId()).getBody();
        assertReportingStructureEquivalence(testReportingStructure, createdReportingStructure);
    }

    @Test
    public void testComputeNoDirectReports() {
        int expected = 0;
        Employee testEmployee = new Employee();
        testEmployee.setEmployeeId("62c1084e-6e34-4630-93fd-9153afb65309");
        testEmployee.setFirstName("Pete");
        testEmployee.setLastName("Best");
        testEmployee.setPosition("Developer II");
        testEmployee.setDepartment("Engineering");

        ReportingStructure testReportingStructure = new ReportingStructure();
        testReportingStructure.setEmployee(testEmployee);
        testReportingStructure.setNumberOfReports(expected);

        // Compute Check 2
        ReportingStructure createdReportingStructure = restTemplate.getForEntity(employeeIdReportURL, ReportingStructure.class, testEmployee.getEmployeeId()).getBody();
        assertReportingStructureEquivalence(testReportingStructure, createdReportingStructure);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    private static void assertReportingStructureEquivalence(ReportingStructure expected, ReportingStructure actual) {
        assertEmployeeEquivalence(expected.getEmployee(), actual.getEmployee());
        assertEquals(expected.getNumberOfReports(), actual.getNumberOfReports());
    }
}
