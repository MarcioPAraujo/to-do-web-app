package edu.marcio.todo_app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.marcio.todo_app.dto.task.TaskRequest;
import edu.marcio.todo_app.dto.task.TaskResponse;
import edu.marcio.todo_app.service.TaskService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

  private final TaskService taskService;

  @PostMapping()
  public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
    TaskResponse response = taskService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

}
