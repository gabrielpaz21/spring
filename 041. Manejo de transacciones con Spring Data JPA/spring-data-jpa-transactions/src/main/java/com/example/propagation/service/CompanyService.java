package com.example.propagation.service;

import com.example.propagation.model.Employee;
import com.example.propagation.model.EmployeeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService {

    private final EmployeeService employeeService;

    private final EmployeeAddressService employeeAddressService;

    public CompanyService(EmployeeService employeeService, EmployeeAddressService employeeAddressService) {
        this.employeeService = employeeService;
        this.employeeAddressService = employeeAddressService;
    }

    @Transactional
    public void joinCompany(Employee employee, EmployeeAddress employeeAddress) {
        employeeService.insert(employee);
        employeeAddressService.insert(employeeAddress);
    }

}
