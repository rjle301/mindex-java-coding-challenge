package com.mindex.challenge.data;

public class ReportingStructure {
    private Employee employee;

    // numberOfReports is the number of directReports for an employee
    // and all of their distinct reports.
    private int numberOfReports;

    public ReportingStructure() {
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getNumberOfReports() {
        return numberOfReports;
    }

    public void setNumberOfReports(int numberOfReports) {
        this.numberOfReports = numberOfReports;
    }
}
