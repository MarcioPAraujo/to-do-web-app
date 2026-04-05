package edu.marcio.todo_app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.marcio.todo_app.dto.filter.TaskPageFilters;
import edu.marcio.todo_app.dto.task.TaskResponse;
import edu.marcio.todo_app.exception.GlobalExceptionHandler;
import edu.marcio.todo_app.exception.task.DuplicatedNameException;
import edu.marcio.todo_app.exception.task.NotFoundTaskException;
import edu.marcio.todo_app.service.TaskService;

@WebMvcTest({ TaskController.class, GlobalExceptionHandler.class })
public class TaskControllerTest {
  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  TaskService taskService;

  private String baseURI() {
    return "/api/tasks";
  }

  private String getRequestJSON(String name) {
    return String.format("{\"name\": \"%s\"}", name);
  }

  private String getEditRequestJSON(Long id, String name) {
    return String.format("{\"id\": %d, \"name\": \"%s\"}", id, name);
  }

  private String getPageParams(int page, int size) {
    return String.format("?page=%d&size=%d", page, size);
  }

  @Test
  void shouldReturn201WhenCreateTask() throws Exception {
    when(taskService.create(any())).thenReturn(new TaskResponse(1L, "watch movie", false));

    mockMvc.perform(post(baseURI())
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\": \"watch movie\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("watch movie"))
        .andExpect(jsonPath("$.completed").value(false));
  }

  @Test
  void shouldReturn409WhenCreateTaskWithDuplicatedName() throws Exception {
    when(taskService.create(any())).thenThrow(new DuplicatedNameException("watch movie"));

    mockMvc.perform(post(baseURI())
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\": \"watch movie\"}"))
        .andExpect(status().isConflict());
  }

  @Test
  void shouldReturn400WhenCreateTaskWithBlankName() throws Exception {
    when(taskService.create(any())).thenThrow(new IllegalArgumentException("Task name cannot be blank"));

    mockMvc.perform(post(baseURI())
        .contentType(MediaType.APPLICATION_JSON)
        .content(getRequestJSON("")))
        .andExpect(status().isBadRequest());

  }

  @Test
  void shouldReturn400WhenCreateTaskWithNullName() throws Exception {
    when(taskService.create(any())).thenThrow(new IllegalArgumentException("Task name cannot be blank"));

    mockMvc.perform(post(baseURI())
        .contentType(MediaType.APPLICATION_JSON)
        .content(getRequestJSON(null)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenCreateTaskWithNameOnlyHaveSpaces() throws Exception {
    when(taskService.create(any())).thenThrow(new IllegalArgumentException("Task name cannot be blank"));

    mockMvc.perform(post(baseURI())
        .contentType(MediaType.APPLICATION_JSON)
        .content(getRequestJSON("   ")))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenCreateTaskWithNameTooLong() throws Exception {
    String longName = "a".repeat(256);
    when(taskService.create(any()))
        .thenThrow(new IllegalArgumentException("Task name cannot be longer than 255 characters"));

    mockMvc.perform(post(baseURI())
        .contentType(MediaType.APPLICATION_JSON)
        .content(getRequestJSON(longName)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn200WhenEditTask() throws Exception {
    TaskResponse response = new TaskResponse(1L, "watch movie", false);
    when(taskService.edit(any())).thenReturn(response);

    mockMvc.perform(put(baseURI() + "/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(getEditRequestJSON(1L, "watch movie")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("watch movie"))
        .andExpect(jsonPath("$.completed").value(false));
  }

  @Test
  void shouldReturn400WhenEditTaskWithBlankName() throws Exception {
    when(taskService.edit(any())).thenThrow(new IllegalArgumentException("Task name cannot be blank"));

    mockMvc.perform(put(baseURI() + "/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(getEditRequestJSON(1L, "")))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenEditTaskWithNullName() throws Exception {
    when(taskService.edit(any())).thenThrow(new IllegalArgumentException("Task name cannot be blank"));

    mockMvc.perform(put(baseURI() + "/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(getEditRequestJSON(1L, null)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenEditTaskWithNameOnlyHaveSpaces() throws Exception {
    when(taskService.edit(any())).thenThrow(new IllegalArgumentException("Task name cannot be blank"));

    mockMvc.perform(put(baseURI() + "/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(getEditRequestJSON(1L, "   ")))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenEditTaskWithNameTooLong() throws Exception {
    String longName = "a".repeat(256);
    when(taskService.edit(any()))
        .thenThrow(new IllegalArgumentException("Task name cannot be longer than 255 characters"));

    mockMvc.perform(put(baseURI() + "/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(getEditRequestJSON(1L, longName)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn409WhenEditTaskWithDuplicatedName() throws Exception {
    when(taskService.edit(any())).thenThrow(new DuplicatedNameException("watch movie"));

    mockMvc.perform(put(baseURI() + "/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(getEditRequestJSON(1L, "watch movie")))
        .andExpect(status().isConflict());
  }

  @Test
  void shouldReturn404WhenEditTaskThatDoesNotExist() throws Exception {
    when(taskService.edit(any())).thenThrow(new NotFoundTaskException(999l));

    mockMvc.perform(put(baseURI() + "/999")
        .contentType(MediaType.APPLICATION_JSON)
        .content(getEditRequestJSON(999L, "watch movie")))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn200WhenNameKeepTheSameOnEdit() throws Exception {
    TaskResponse response = new TaskResponse(1L, "watch movie", false);
    when(taskService.edit(any())).thenReturn(response);

    mockMvc.perform(put(baseURI() + "/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(getEditRequestJSON(1L, "watch movie")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("watch movie"))
        .andExpect(jsonPath("$.completed").value(false));
  }

  @Test
  void shouldReturn200WhenToggleTask() throws Exception {
    TaskResponse response = new TaskResponse(1L, "watch movie", true);
    when(taskService.toggleCompleted(any())).thenReturn(response);

    mockMvc.perform(put(baseURI() + "/toggleTask/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("watch movie"))
        .andExpect(jsonPath("$.completed").value(true));
  }

  @Test
  void shouldReturn404WhenToggleTaskThatDoesNotExist() throws Exception {
    when(taskService.toggleCompleted(any())).thenThrow(new NotFoundTaskException(999l));

    mockMvc.perform(put(baseURI() + "/toggleTask/999")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn200WhenDeleteTask() throws Exception {
    TaskResponse response = new TaskResponse(1L, "watch movie", false);
    when(taskService.delete(any())).thenReturn(response);

    mockMvc.perform(delete(baseURI() + "/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("watch movie"))
        .andExpect(jsonPath("$.completed").value(false));
  }

  @Test
  void shouldReturn404WhenDeleteTaskThatDoesNotExist() throws Exception {
    when(taskService.delete(any())).thenThrow(new NotFoundTaskException(999l));

    mockMvc.perform(delete(baseURI() + "/999")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn200WhenGetTask() throws Exception {
    TaskResponse response = new TaskResponse(1L, "watch movie", false);
    when(taskService.getTaskById(any())).thenReturn(response);

    mockMvc.perform(get(baseURI() + "/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("watch movie"))
        .andExpect(jsonPath("$.completed").value(false));
  }

  @Test
  void shouldReturn404WhenGetTaskThatDoesNotExist() throws Exception {
    when(taskService.getTaskById(any())).thenThrow(new NotFoundTaskException(999l));

    mockMvc.perform(get(baseURI() + "/999")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn200WhenGetListOfTask() throws Exception {
    when(taskService.getAllTasks(any(Pageable.class), any(TaskPageFilters.class)))
        .thenReturn(Page.empty());
    mockMvc.perform(get(baseURI())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void shouldReturn200WhenGetListOfTaskWithPageParams() throws Exception {
    mockMvc.perform(get(baseURI() + getPageParams(1, 10))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void shouldReturn400WhenGetListOfTaskWithInvalidPageParams() throws Exception {
    when(taskService.getAllTasks(any(Pageable.class), any(TaskPageFilters.class)))
        .thenThrow(new IllegalArgumentException("Page number cannot be negative"));

    mockMvc.perform(get(baseURI() + getPageParams(-1, 10))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn200WithFilteredListofTasks() throws Exception {
    TaskResponse response1 = new TaskResponse(1L, "watch movie", false);
    TaskResponse response2 = new TaskResponse(2L, "watch movie", true);

    when(taskService.getAllTasks(any(Pageable.class), any(TaskPageFilters.class)))
        .thenReturn(new PageImpl<>(List.of(response1, response2)));

    mockMvc.perform(get(baseURI() + getPageParams(0, 10) + "&name=watch&completed=false")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1L))
        .andExpect(jsonPath("$.content[0].name").value("watch movie"))
        .andExpect(jsonPath("$.content[0].completed").value(false));
  }
}
