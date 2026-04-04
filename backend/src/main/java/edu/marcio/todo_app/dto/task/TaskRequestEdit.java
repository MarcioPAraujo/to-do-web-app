package edu.marcio.todo_app.dto.task;

import lombok.Data;

@Data
public class TaskRequestEdit {
  private Long id;
  private String name;
  private Boolean completed;

  public TaskRequestEdit(Long id, String name, Boolean completed) {
    this.name = name;
    this.completed = completed;
    this.id = id;
  }
}
