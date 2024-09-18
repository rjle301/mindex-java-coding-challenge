package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Stack;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Reading employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    /**
     * Computes a ReportingStructure for an emplyoee on the fly.
     */
    @Override
    public ReportingStructure compute(String id) {
        LOG.debug("Computing a ReportingStructure for employee [{}]", id);

        ReportingStructure reportingStructure = new ReportingStructure();

        // An invalid ID will be handled inside of read
        Employee employee = read(id);   
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
                    employee = read(employee.getEmployeeId());
                    employeeStack.push(employee);
                    numberOfReports++;
                }
            }
        }
        
        return numberOfReports;
    }
}
