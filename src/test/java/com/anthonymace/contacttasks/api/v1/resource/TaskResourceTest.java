package com.anthonymace.contacttasks.api.v1.resource;

import com.anthonymace.contacttasks.services.TasksService;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebMvcTest(TaskResource.class)
public class TaskResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TasksService tasksService;
    @InjectMocks
    private TaskResource taskResource;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(taskResource).build();
    }

    @Test
    public void shouldReturn400ForGetTasksNonIntegerContactId() throws Exception {
        mockMvc.perform(get("/contact/hello/tasks/incomplete")).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400ForCreateTaskNonIntegerContactId() throws Exception {
        mockMvc.perform(post("/contact/hello/task").contentType(MediaType.APPLICATION_JSON_UTF8).content("{}")).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn415ForCreateTaskWithoutContentTypeSet() throws Exception {
        mockMvc.perform(post("/contact/hello/task").content("{}")).andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void shouldReturn200ForGetTasksWithIntegerContactId() throws Exception {
        when(tasksService.contactExists(anyInt())).thenReturn(true);
        when(tasksService.getTasks(anyInt())).thenReturn(new JSONArray());

        MockHttpServletResponse response = mockMvc.perform(get("/contact/1/tasks/incomplete"))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void shouldReturnJsonArrayForGetTasksWithIntegerContactId() throws Exception {
        JSONArray expectedResponse = new JSONObject(
                readFile("src/test/resources/get-tasks-response.json")
        ).getJSONArray("tasks");

        when(tasksService.contactExists(anyInt())).thenReturn(true);
        when(tasksService.getTasks(anyInt())).thenReturn(expectedResponse);

        MockHttpServletResponse response = mockMvc.perform(get("/contact/1/tasks/incomplete"))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertEquals(expectedResponse.length(), new JSONArray(response.getContentAsString()).length());
    }

    @Test
    public void shouldReturnCreatedTask() throws Exception {
        JSONObject expectedResponse = new JSONObject(
                readFile("src/test/resources/create-task-response.json")
        );

        String createTaskInput = expectedResponse.toString();

        when(tasksService.contactExists(anyInt())).thenReturn(true);
        when(tasksService.createTask(anyInt(), any())).thenReturn(expectedResponse);

        MockHttpServletResponse response = mockMvc.perform(post("/contact/1/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createTaskInput))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertFalse(expectedResponse.getBoolean("completed"));
    }

    @Test
    public void testGetTasks400ResponseForNonExistantContact() throws Exception {
        String expectedResponse = readFile("src/test/resources/contact-does-not-exist-response.json");

        when(tasksService.contactExists(anyInt())).thenReturn(false);

        MockHttpServletResponse response = mockMvc.perform(get("/contact/1/tasks/incomplete"))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());
        assertEquals(expectedResponse, response.getContentAsString());

    }

    @Test
    public void testCreateTask400ResponseForNonExistantContact() throws Exception {
        String createTaskInput = readFile("src/test/resources/create-task-response.json");
        String expectedResponse = readFile("src/test/resources/contact-does-not-exist-response.json");

        when(tasksService.contactExists(anyInt())).thenReturn(false);

        MockHttpServletResponse response = mockMvc.perform(post("/contact/1/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createTaskInput))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());
        assertEquals(expectedResponse, response.getContentAsString());

    }

    private String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }
}
