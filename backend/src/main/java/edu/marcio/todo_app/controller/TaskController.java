package edu.marcio.todo_app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.marcio.todo_app.dto.task.TaskRequest;
import edu.marcio.todo_app.dto.task.TaskRequestEdit;
import edu.marcio.todo_app.dto.task.TaskResponse;
import edu.marcio.todo_app.service.TaskService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

  @PutMapping("/{id}")
  public ResponseEntity<TaskResponse> editTask(@PathVariable String id, @RequestBody TaskRequestEdit editRequest) {
    TaskResponse response = taskService.edit(editRequest);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PutMapping("/toggleTask/{id}")
  public ResponseEntity<TaskResponse> toggleTask(@PathVariable Long id) {
    TaskResponse response = taskService.toggleCompleted(id);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

}
