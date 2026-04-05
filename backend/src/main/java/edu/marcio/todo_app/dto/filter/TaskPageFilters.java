package edu.marcio.todo_app.dto.filter;

import lombok.Data;

@Data
public class TaskPageFilters {

  private String name;

  private Boolean completed;
}
