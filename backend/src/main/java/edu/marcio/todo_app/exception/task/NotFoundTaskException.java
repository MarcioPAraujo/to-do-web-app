package edu.marcio.todo_app.exception.task;

public class NotFoundTaskException extends RuntimeException {

  public NotFoundTaskException(Long id) {
    super(String.format("Task with id '%d' not found", id));
  }

  public NotFoundTaskException(String name) {
    super(String.format("Task with name '%s' not found", name));
  }
}
