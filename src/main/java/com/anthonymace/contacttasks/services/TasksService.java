package com.anthonymace.contacttasks.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.util.Arrays;

@Service
public class TasksService {

    RestTemplate tasksRequest = new RestTemplate();
    private String apiUrl = "https://api.infusionsoft.com/crm/rest/v1/%s";

    @NotNull
    private String accessToken;

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public JSONArray getTasks(int contactId) {
        String contactTasksUrl = String.format("tasks?contact_id=%d", contactId);
        String tasksResourceUrl = String.format(apiUrl, contactTasksUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
        headers.setBearerAuth(this.accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> tasksResponse =
                tasksRequest.exchange(tasksResourceUrl, HttpMethod.GET, request, String.class);

        JSONObject tasks = new JSONObject(tasksResponse.getBody());
        return tasks.getJSONArray("tasks");
    }

    public JSONObject createTask(int contactId, JSONObject task) {
        String createTaskResourceUrl = String.format(apiUrl, "tasks");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
        headers.setBearerAuth(this.accessToken);

        HttpEntity<JSONObject> request = new HttpEntity<>(task, headers);
        ResponseEntity<String> createTaskResponse =
                tasksRequest.exchange(createTaskResourceUrl, HttpMethod.POST, request, String.class);

        return new JSONObject(createTaskResponse.getBody());
    }

    public boolean contactExists(int contactId) {
        String contactTasksUrl = String.format("contacts/%d", contactId);
        String contactResourceUrl = String.format(apiUrl, contactTasksUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
        headers.setBearerAuth(this.accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            tasksRequest.exchange(contactResourceUrl, HttpMethod.GET, request, String.class);
        } catch (HttpClientErrorException e) {
            // .exchange throws an exception when it 404s (from the stub), hard to test
            // but this is gross.
            // REVISIT
            return false;
        }
        return true;

    }
}
