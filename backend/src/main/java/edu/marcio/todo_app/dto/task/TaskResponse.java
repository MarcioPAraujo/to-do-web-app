package edu.marcio.todo_app.dto.task;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskResponse {
  private Long id;
  private String name;
  private boolean completed;
}
