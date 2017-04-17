package com.employee.controller;

import com.employee.EmployeeApplication;
import com.employee.entity.Meeting;
import com.employee.repository.DepartmentRepository;
import com.employee.repository.MeetingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmployeeApplication.class)
@WebAppConfiguration
public class MeetingControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    protected String json(Object o) throws IOException {
        String jsonInString = mapper.writeValueAsString(o);
        return jsonInString;
    }

    @Before
    public void setUp() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }




    @Test
    public void shouldSaveMeeting() throws Exception {
        Meeting meeting = new Meeting();
        meeting.setName("name");
        meeting.setDescription("desc");

        this.mockMvc.perform(
                post("/meeting/save/")
                        .content(this.json(meeting))
                        .contentType(contentType)
        ).andExpect(status().isOk());
    }

    @Test
    public void shouldThrowContentNotFoundExceptionWhenDeleteMeetingIdIsWrong() throws Exception {
        Meeting meeting = new Meeting();

        meeting.setName("name");
        meeting.setDescription("desc");
        this.meetingRepository.save(meeting);
        meeting.setId(3453453L);
        this.mockMvc.perform(
                delete("/meeting/delete")
                        .content(this.json(meeting))
                        .contentType(contentType)
        ).andExpect(status().isNotFound());
    }
}