package com.anthonymace.contacttasks.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Map;

@Service
public class TasksService {

    @Value("${api.url}")
    private String API_URL;

    @NotNull
    private String accessToken;

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public JSONArray getTasks(int contactId) {
        RestTemplate tasksRequest = new RestTemplate();
        String contactTasksUrl = String.format("tasks?contact_id=%d", contactId);
        String tasksResourceUrl = String.format(API_URL, contactTasksUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
        headers.setBearerAuth(this.accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> tasksResponse =
                tasksRequest.exchange(tasksResourceUrl, HttpMethod.GET, request, String.class);

        JSONObject tasks = new JSONObject(tasksResponse.getBody());
        return tasks.getJSONArray("tasks");
    }

    public JSONObject createTask(Map<String, Object> task) {
        RestTemplate tasksRequest = new RestTemplate();
        String createTaskResourceUrl = String.format(API_URL, "tasks");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
        headers.setBearerAuth(this.accessToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(task, headers);
        ResponseEntity<String> createTaskResponse =
            tasksRequest.exchange(createTaskResourceUrl, HttpMethod.POST, request, String.class);

        return new JSONObject(createTaskResponse.getBody());
    }

    public int contactExists(int contactId) {
        RestTemplate tasksRequest = new RestTemplate();
        int statusCode = 200;
        String contactTasksUrl = String.format("contacts/%d", contactId);
        String contactResourceUrl = String.format(API_URL, contactTasksUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
        headers.setBearerAuth(this.accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            statusCode = tasksRequest.exchange(contactResourceUrl, HttpMethod.GET, request, String.class).getStatusCode().value();
        } catch (HttpClientErrorException e) {
            statusCode = e.getRawStatusCode();
        }
        return statusCode;
    }
}
