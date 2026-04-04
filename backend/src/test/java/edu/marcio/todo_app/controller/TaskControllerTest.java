package edu.marcio.todo_app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.marcio.todo_app.dto.task.TaskResponse;
import edu.marcio.todo_app.exception.task.DuplicatedNameException;
import edu.marcio.todo_app.exception.task.NotFoundTaskException;
import edu.marcio.todo_app.service.TaskService;

@WebMvcTest(TaskController.class)
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
}
