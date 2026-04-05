package edu.marcio.todo_app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.marcio.todo_app.dto.filter.TaskPageFilters;
import edu.marcio.todo_app.dto.task.TaskRequest;
import edu.marcio.todo_app.dto.task.TaskRequestEdit;
import edu.marcio.todo_app.dto.task.TaskResponse;
import edu.marcio.todo_app.service.TaskService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

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

  @DeleteMapping("/{id}")
  public ResponseEntity<TaskResponse> deleteTask(@PathVariable Long id) {
    TaskResponse response = taskService.delete(id);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
    TaskResponse response = taskService.getTaskById(id);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping()
  public ResponseEntity<Page<TaskResponse>> getListOfTask(Pageable pageable,
      @ModelAttribute TaskPageFilters filters) {
    Page<TaskResponse> response = taskService.getAllTasks(pageable, filters);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

}
