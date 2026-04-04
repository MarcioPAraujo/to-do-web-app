package edu.marcio.todo_app.exception.task;

public class DuplicatedNameException extends RuntimeException {

  public DuplicatedNameException(String name) {
    super(String.format("Task with name '%s' already exists", name));
  }

}
