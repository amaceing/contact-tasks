package com.anthonymace.contacttasks.services;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TasksServiceTest {

    @Autowired
    private TasksService tasksService;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    @Test
    public void testContactExistsReturnsFalseFor404() throws IOException {
        stubFor(get(urlPathEqualTo("/contacts/1"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody(readFile("src/test/resources/contact-does-not-exist-response.json"))));
        assertEquals(404, tasksService.contactExists(1));
    }

    @Test
    public void testContactExistsReturnsTrueFor200() throws IOException {
        stubFor(get(urlPathEqualTo("/contacts/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(readFile("src/test/resources/get-contact-response.json"))));
        assertEquals(200, tasksService.contactExists(1));
    }

    @Test
    public void testGetTasksSuccessfulResponse() throws IOException {
        stubFor(get(urlPathMatching("/tasks"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(readFile("src/test/resources/get-tasks-response.json"))));
        JSONArray getTasksResponse = tasksService.getTasks(1);
        assertEquals(4, getTasksResponse.length());
    }

    @Test
    public void testCreateTaskSuccessfulResponse() throws IOException {
        String createTaskJson = readFile("src/test/resources/create-task-response.json");
        JSONObject expectedCreateTaskResponse = new JSONObject(createTaskJson);
        JSONObject createTaskInput = expectedCreateTaskResponse;
        stubFor(post(urlPathMatching("/task"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createTaskJson)));
        JSONObject createTaskResponse = tasksService.createTask(createTaskInput.toMap());
        assertFalse(createTaskResponse.getBoolean("completed"));
        assertEquals(1, createTaskResponse.getJSONObject("contact").getInt("id"));
    }

    private String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }
}
