package com.employee.controller;

import com.employee.entity.Department;
import com.employee.entity.Employee;
import com.employee.exception.ContentNotFoundException;
import com.employee.repository.DepartmentRepository;
import com.employee.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "employee")
public class EmployeeController {

    private static Logger logger = LoggerFactory.getLogger(EmployeeController.class.getName());

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @RequestMapping(value = "findAll")
    public Iterable<Employee> findAll() {
        logger.info("Fetching All Employees..");
        return employeeRepository.findAll();
    }

    @RequestMapping(value = "save/{id}", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Employee save(@RequestBody Employee employee, @PathVariable("id") Long departmentId) {
        Department department = departmentRepository.findOne(departmentId);
        if (department == null) {
            throw new ContentNotFoundException(String.format("Department not found with department id : %s", departmentId));
        }
        employee.setDepartment(department);
        logger.info("Inserting New Employee {}",employee);
        return employeeRepository.save(employee);
    }

    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    public void delete(@RequestBody Employee employee) {
        boolean exists = employeeRepository.exists(employee.getId());
        if (exists){
            logger.info("Employee will be remove {}",employee);
            employeeRepository.delete(employee);
        }else{
            logger.info("Employee does not exist!!");
            throw new ContentNotFoundException(String.format("Employee not found with employee id : %s", employee.getId()));
        }
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public Employee update(@RequestBody Employee employee) {
        boolean exists = employeeRepository.exists(employee.getId());
        if (exists) {
            logger.info("Employee will be remove {}", employee);
            return employeeRepository.save(employee);
        }else{
            logger.info("Employee does not exist!!");
            throw new ContentNotFoundException(String.format("Employee not found with employee id : %s", employee.getId()));
        }
    }
}
