package edu.marcio.todo_app.dto.task;

import lombok.Data;

@Data
public class TaskRequest {
  private String name;

  public TaskRequest(String name) {
    this.name = name;
  }
}
