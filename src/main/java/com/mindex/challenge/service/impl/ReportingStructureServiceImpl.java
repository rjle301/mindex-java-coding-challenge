package com.mindex.challenge.service.impl;

import java.util.Stack;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;

// ReportingStructureSercice now depends on Employee classes
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.EmployeeService;

/**
 * ReportingStructureServiceImpl is an implementation of 
 * the ReportingStructureService interface.
 * 
 * It handles the business logic for a reporting structure 
 * when a corresponding http request is receieved.
 * 
 * ReportingStructures are not persistent. They are used exclusively for computing
 * data about an employee on the fly.
 */
@Service
public class ReportingStructureServiceImpl implements ReportingStructureService{
    private final static Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeService employeeService;

    @Override
    public ReportingStructure read(String id) {
        LOG.debug("Computing a ReportingStructure for employee with id [{}]", id);

        ReportingStructure reportingStructure = new ReportingStructure();

        Employee employee = employeeService.read(id);
        reportingStructure.setEmployee(employee);
        
        int numberOfReports = calculateNumberOfReports(employee);
        reportingStructure.setNumberOfReports(numberOfReports);

        return reportingStructure;
    }

    /**
     * Iteratively finds the number of reports belonging to startEmployee
     * @param startEmployee The "root" employee
     * @return The total number of reports under a given employee
     */
    private int calculateNumberOfReports(Employee startEmployee) {
        int numberOfReports = 0;
        Stack<Employee> employeeStack = new Stack<Employee>();
        employeeStack.push(startEmployee);

        while(!employeeStack.isEmpty()) {
            Employee curEmployee = employeeStack.pop();
            List<Employee> directReports = curEmployee.getDirectReports();

            // It's possible for an employee to not have any direct reports.
            if(directReports != null) {
                for(Employee employee : directReports) {
                    employee = employeeService.read(employee.getEmployeeId());
                    employeeStack.push(employee);
                    numberOfReports++;
                }
            }
        }
        
        return numberOfReports;
    }
}
