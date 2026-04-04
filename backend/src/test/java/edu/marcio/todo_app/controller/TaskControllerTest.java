package edu.marcio.todo_app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

  private String getJSON(String name) {
    return String.format("{\"name\": \"%s\"}", name);
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
        .content(getJSON("")))
        .andExpect(status().isBadRequest());

  }
}
