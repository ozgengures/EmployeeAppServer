package com.employee.controller;

import com.employee.entity.Department;
import com.employee.entity.Meeting;
import com.employee.exception.ContentNotFoundException;
import com.employee.repository.DepartmentRepository;
import com.employee.repository.MeetingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "meeting")
public class MeetingController {

    private static Logger logger = LoggerFactory.getLogger(MeetingController.class.getName());

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @RequestMapping(value = "findAll")
    public Iterable<Meeting> findAll() {
        logger.info("Fetching All Meetings..");
        return meetingRepository.findAll();
    }

    @RequestMapping(value = "find/{id}")
    public Meeting findOne(Long id) {
        try {
            logger.info("Fetching meeting.. ID ({})", id);
            Meeting meeting = meetingRepository.findOne(id);
            return meeting;
        } catch (Exception e) {
            throw new ContentNotFoundException(String.format("Meeting not found with meeting id : %s", id));
        }
    }

    @RequestMapping(value = "save/", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Meeting save(@RequestBody Meeting meeting) {

        logger.info("Inserting New Department {}", meeting);
        return meetingRepository.save(meeting);
    }

    @RequestMapping(value = "joinMeeting", method = RequestMethod.POST)
    public void joinMeeting(Long meetingId, Long departmentId) {
        Meeting meeting = meetingRepository.findOne(meetingId);
        Department department = departmentRepository.findOne(departmentId);
        if (department == null || meeting == null) {
            throw new ContentNotFoundException(String.format("Department or meeting not found with department id : %s meeting id : %s", departmentId, meetingId));
        }
        meeting.getDepartments().add(department);
        meetingRepository.save(meeting);
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public Meeting update(@RequestBody Meeting meeting) {
        logger.info("Meeting will be update {}", meeting);
        return meetingRepository.save(meeting);
    }

    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    public void delete(@RequestBody Meeting meeting) {
        boolean exists = meetingRepository.exists(meeting.getId());
        if (exists) {
            logger.info("Meeting will be remove {}", meeting);
            meeting.getDepartments().clear();
            meetingRepository.delete(meeting.getId());
        } else {
            logger.error("Meeting does not exist!!");
            throw new ContentNotFoundException(String.format("Meeting not found with Meeting id : %s", meeting.getId()));
        }

    }

}
