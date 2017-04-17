package com.employee.controller;

import com.employee.entity.Department;
import com.employee.entity.Employee;
import com.employee.entity.Meeting;
import com.employee.exception.AssociationException;
import com.employee.exception.ContentNotFoundException;
import com.employee.repository.DepartmentRepository;
import com.employee.repository.EmployeeRepository;
import com.employee.repository.MeetingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;

@RestController
@RequestMapping(value = "department")
public class DepartmentController {
    private static Logger logger = LoggerFactory.getLogger(DepartmentController.class.getName());

    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @RequestMapping(value = "findAll")
    public Iterable<Department> findAll() {
        logger.info("Fetching All Departments..");
        return departmentRepository.findAll();
    }

    @RequestMapping(value = "find/{id}")
    public Department findOne(Long id) {
        try {
            logger.info("Fetching department.. ID ({})", id);
            Department department = departmentRepository.findOne(id);
            return department;
        } catch (Exception e) {
            logger.info("Department does not exist!!");
            throw new ContentNotFoundException(String.format("Department not found with Department id : %s", id));
        }
    }


    @RequestMapping(value = "save", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Department save(@RequestBody Department department) {
        logger.info("Inserting New Department {}", department);
        return departmentRepository.save(department);
    }


    @RequestMapping(value = "save/{id}", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Department save(@RequestBody Department department, @PathVariable("id") ArrayList<Long> meetingIds) {
        try {
            HashSet<Meeting> meetingHashSet = new HashSet<>();
            for (Long meetingId : meetingIds) {

                Meeting meeting = meetingRepository.findOne(meetingId);
                meetingHashSet.add(meeting);
                meeting.getDepartments().add(department);
            }
            department.setMeetings(meetingHashSet);
        } catch (Exception e) {
            logger.info("Meeting does not exist!!");
            throw new ContentNotFoundException(String.format("Meeting does not found!! "));
        }
        logger.info("Inserting New Department {}", department);
        return departmentRepository.save(department);
    }


    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    public void delete(@RequestBody Department department) {
        boolean exists = departmentRepository.exists(department.getId());
        if (exists) {
            for (Meeting m : department.getMeetings()) {
                Meeting meet = meetingRepository.findOne(m.getId());
                meet.getDepartments().remove(department);
            }
            Iterable<Employee> employees = employeeRepository.findAll();
            for (Employee m : employees) {
                if (m.getDepartment().getId() == department.getId()) {
                    logger.error("Department found association with Meeting id {}" + m.getId());
                    throw new AssociationException(String.format("Department found association with Meeting id : %s", m.getId()));
                }
            }
            meetingRepository.flush();
            logger.info("Department will be remove {}", department);
            departmentRepository.delete(department);
        } else {
            logger.info("Department does not exist!!");
            throw new ContentNotFoundException(String.format("Department not found with Department id : %s", department.getId()));
        }
    }

    @RequestMapping(value = "update/{id}", method = RequestMethod.PUT)
    public Department update(@RequestBody Department department, @PathVariable("id") ArrayList<Long> meetingIds) {
        Department departWillUpdate = departmentRepository.findOne(department.getId());
        try {
            for (Meeting meeting : departWillUpdate.getMeetings()) {
                meeting.getDepartments().remove(department);
            }
            HashSet<Meeting> meetingHashSet = new HashSet<>();
            for (Long meetingId : meetingIds) {
                Meeting meeting = meetingRepository.findOne(meetingId);
                meetingHashSet.add(meeting);
                meeting.getDepartments().add(departWillUpdate);
            }
            departWillUpdate.getMeetings().clear();
            departWillUpdate.getMeetings().addAll(meetingHashSet);

        } catch (Exception e) {
            logger.info("Meeting does not exist!!");
            throw new ContentNotFoundException(String.format("Meeting does not found!! "));
        }
        logger.info("Department will be updating {}", departWillUpdate);
        return departmentRepository.save(departWillUpdate);
    }
}
