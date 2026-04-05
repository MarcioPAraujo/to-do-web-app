package edu.marcio.todo_app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import edu.marcio.todo_app.dto.filter.TaskPageFilters;
import edu.marcio.todo_app.dto.task.TaskRequest;
import edu.marcio.todo_app.dto.task.TaskResponse;
import edu.marcio.todo_app.exception.task.DuplicatedNameException;
import edu.marcio.todo_app.exception.task.NotFoundTaskException;
import edu.marcio.todo_app.model.Task;
import edu.marcio.todo_app.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

  @Mock
  TaskRepository taskRepository;

  @InjectMocks
  TaskService taskService;

  // The following tests are for the create method
  @Test
  void shouldCreateTaskAndReturnResponse() {
    String taskName = "watch movie";

    TaskRequest taskRequest = new TaskRequest(taskName);
    Task savedTask = new Task(1L, taskName, false);

    when(taskRepository.existsByName(taskName)).thenReturn(false);
    when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

    TaskResponse response = taskService.create(taskRequest);

    assertEquals(1L, response.getId());
    assertEquals(taskName, response.getName());
    assertEquals(false, response.isCompleted());
  }

  @Test
  void shouldThowExceptionWhenTitleIsBlank() {
    TaskRequest taskRequest = new TaskRequest("");
    assertThrows(IllegalArgumentException.class, () -> taskService.create(taskRequest));
  }

  @Test
  void shouldThowExceptionWhenTitleIsNull() {
    TaskRequest taskRequest = new TaskRequest(null);
    assertThrows(IllegalArgumentException.class, () -> taskService.create(taskRequest));
  }

  @Test
  void shouldthrowExceptionWhenTitleOnlyHaveSpaces() {
    TaskRequest taskRequest = new TaskRequest("   ");
    assertThrows(IllegalArgumentException.class, () -> taskService.create(taskRequest));
  }

  @Test
  void shouldThrowExceptionWhenTitleIsTooLong() {
    String longTitle = "a".repeat(256);
    TaskRequest taskRequest = new TaskRequest(longTitle);
    assertThrows(IllegalArgumentException.class, () -> taskService.create(taskRequest));
  }

  @Test
  void shouldThrowExceptionWhenTitleAlreadyExists() {
    String taskName = "watch movie";
    when(taskRepository.existsByName(taskName)).thenReturn(true);

    TaskRequest taskRequest = new TaskRequest(taskName);
    assertThrows(DuplicatedNameException.class, () -> taskService.create(taskRequest));
  }

  @Test
  void saveMusttBeCalledOnceOnSuccessfulCreate() {
    String taskName = "watch movie";

    TaskRequest taskRequest = new TaskRequest(taskName);
    Task savedTask = new Task(1L, taskName, false);

    when(taskRepository.existsByName(taskName)).thenReturn(false);
    when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

    taskService.create(taskRequest);

    // Verify that save was called once with a Task object that has the correct name
    // and completed status
    verify(taskRepository, times(1)).save(any(Task.class));
  }

  // The following tests are for the edit and delete methods
  @Test
  void shouldThrowExceptionWhenTaskNameIsNullOnEdit() {
    Task task = new Task(1L, "watch movie", false);
    when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));

    TaskRequest request = new TaskRequest(null);

    assertThrows(IllegalArgumentException.class, () -> taskService.edit(1L, request));
  }

  @Test
  void shouldThrowExceptionWhenTaskNameOnlyHaveSpacesOnEdit() {
    Task task = new Task(1L, "watch movie", false);
    when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));

    TaskRequest request = new TaskRequest("   ");

    assertThrows(IllegalArgumentException.class, () -> taskService.edit(1L, request));
  }

  @Test
  void shouldThrowExceptionWhenTaskNameIsTooLongOnEdit() {
    Task task = new Task(1L, "watch movie", false);
    when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));

    String longTitle = "a".repeat(256);
    TaskRequest request = new TaskRequest(longTitle);

    assertThrows(IllegalArgumentException.class, () -> taskService.edit(1L, request));
  }

  @Test
  void shouldThrowExceptionWhenTaskDoesNotExistOnEdit() {
    Task task = new Task(1L, "watch movie", false);

    taskRepository.save(task);

    TaskRequest request = new TaskRequest("watch movie");

    assertThrows(RuntimeException.class, () -> taskService.edit(2L, request));
  }

  @Test
  void shouldThrowExceptionWhenTitleIsBlankOnEdit() {
    Task task = new Task(1L, "watch movie", false);
    when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));

    TaskRequest request = new TaskRequest("");

    assertThrows(IllegalArgumentException.class, () -> taskService.edit(1L, request));
  }

  @Test
  void shouldThrowExceptionWhenTitleAlreadyExistsOnEdit() {
    Task task = new Task(1L, "watch movie", false);
    when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));
    when(taskRepository.existsByNameAndIdNot("watch movie", 1L)).thenReturn(true);

    assertThrows(DuplicatedNameException.class, () -> taskService.edit(1L, new TaskRequest("watch movie")));
  }

  @Test
  void shouldAllowTheSameTaskNameOnEdit() {
    Task task = new Task(1L, "watch movie", false);
    when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));
    when(taskRepository.existsByNameAndIdNot("watch movie", 1L)).thenReturn(false);
    when(taskRepository.save(any(Task.class))).thenReturn(task);

    TaskRequest request = new TaskRequest("watch movie");
    TaskResponse response = taskService.edit(1L, request);

    assertEquals(1L, response.getId());
    assertEquals("watch movie", response.getName());
    assertEquals(false, response.isCompleted());
  }

  @Test
  void shouldEditTaskAndReturnResponse() {
    Task task = new Task(1L, "watch movie", false);
    when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));
    when(taskRepository.existsByNameAndIdNot("watch movie", 1L)).thenReturn(false);
    when(taskRepository.save(any(Task.class))).thenReturn(task);

    TaskRequest request = new TaskRequest("watch movie");
    TaskResponse response = taskService.edit(1L, request);

    assertEquals(1L, response.getId());
    assertEquals("watch movie", response.getName());
    assertEquals(false, response.isCompleted());
  }

  // The following tests are for the delete method
  @Test
  void shouldReturnDeletedTaskResponse() {
    Task task = new Task(1L, "watch movie", false);
    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

    TaskResponse response = taskService.delete(1L);

    assertEquals(1L, response.getId());
    assertEquals("watch movie", response.getName());
    assertEquals(false, response.isCompleted());
  }

  @Test
  void shouldThrowExceptionWhenTaskDoesNotExistOnDelete() {
    when(taskRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(RuntimeException.class, () -> taskService.delete(1L));
  }

  // the following tests are for get method
  @Test
  void shouldReturnTaskResponseWhenTaskExists() {
    Task task = new Task(1L, "watch movie", false);
    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

    TaskResponse response = taskService.getTaskById(1L);

    assertEquals(1L, response.getId());
    assertEquals("watch movie", response.getName());
    assertEquals(false, response.isCompleted());
  }

  @Test
  void shouldThrowExceptionWhenTaskDoesNotExistOnGet() {
    when(taskRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(RuntimeException.class, () -> taskService.getTaskById(1L));
  }

  @Test
  void shouldReturnPaginatedTaskResponses() {
    Task task1 = new Task(1L, "watch movie", false);
    Task task2 = new Task(2L, "read book", true);

    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
    Page<Task> taskPage = new PageImpl<>(List.of(task1, task2), pageable, 2);

    TaskPageFilters filters = new TaskPageFilters();

    when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(taskPage);

    Page<TaskResponse> result = taskService.getAllTasks(pageable, filters);

    // Verify the results
    assertEquals(2, result.getContent().size());
    assertEquals("watch movie", result.getContent().get(0).getName());
    assertEquals("read book", result.getContent().get(1).getName());
  }

  @Test
  void shouldReturnEmptyPageWhenNoTasksExist() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
    Page<Task> emptyPage = new PageImpl<>(List.of(), pageable, 0);

    when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

    TaskPageFilters filters = new TaskPageFilters();
    Page<TaskResponse> result = taskService.getAllTasks(pageable, filters);

    // Verify the results
    assertEquals(0, result.getContent().size());
  }

  @Test
  void shouldReturnTasksWithTheFilteredTitle() {
    Task task1 = new Task(1L, "watch movie", false);

    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
    Page<Task> taskPage = new PageImpl<>(List.of(task1), pageable, 1);

    when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(taskPage);
    TaskPageFilters filters = new TaskPageFilters();
    filters.setName("watch");

    Page<TaskResponse> result = taskService.getAllTasks(pageable, filters);

    // Verify the results
    assertEquals(1, result.getContent().size());
    assertEquals("watch movie", result.getContent().get(0).getName());
  }

  // The following tests are for the toggleCompleted method
  @Test
  void shouldToggleCompletedStatus() {
    Task task = new Task(1L, "watch movie", false);
    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
    when(taskRepository.save(any(Task.class))).thenReturn(new Task(1L, "watch movie", true));

    TaskResponse response = taskService.toggleCompleted(1L);
    assertEquals(1L, response.getId());
    assertEquals("watch movie", response.getName());
    assertEquals(true, response.isCompleted());
  }

  @Test
  void shouldThrowExceptionWhenTaskDoesNotExistOnToggleCompleted() {
    when(taskRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(NotFoundTaskException.class, () -> taskService.toggleCompleted(1L));
  }
}