package com.anthonymace.contacttasks.api.v1.resource;

import com.anthonymace.contacttasks.services.TasksService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class TaskResource {

    private TasksService tasksService = new TasksService();

    @RequestMapping(value = "contact/{contactId}/tasks/incomplete", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getTasks(
            @PathVariable(value="contactId") int contactId,
            @RequestHeader(value="access-token") String accessToken
    ) {
        tasksService.setAccessToken(accessToken);

        if (!tasksService.contactExists(contactId)) {
            return contactDoesNotExist();
        }

        JSONArray tasks = tasksService.getTasks(contactId);
        List<JSONObject> incompleteTasks = StreamSupport.stream(tasks.spliterator(), false)
                .map(JSONObject.class::cast)
                .filter(task -> !task.getBoolean("completed"))
                .collect(Collectors.toList());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(incompleteTasks.toString());
    }

    @RequestMapping(
            value = "contact/{contactId}/task",
            method = RequestMethod.POST,
            consumes="application/json",
            produces = "application/json"
    )
    public ResponseEntity createTask(
            @PathVariable(value="contactId") int contactId,
            @RequestHeader(value="access-token") String accessToken,
            @RequestBody Map<String, Object> taskInfo
    ) {
        tasksService.setAccessToken(accessToken);

        if (!tasksService.contactExists(contactId)) {
            return contactDoesNotExist();
        }

        JSONObject createdTask = tasksService.createTask(contactId, new JSONObject(taskInfo));
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON_UTF8).body(createdTask.toString());
    }

    private ResponseEntity contactDoesNotExist() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Contact does not exist");
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON_UTF8).body(response);
    }
}
