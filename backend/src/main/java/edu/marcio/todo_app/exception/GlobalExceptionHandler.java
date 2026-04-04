package edu.marcio.todo_app.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import edu.marcio.todo_app.dto.error.ErrorResponse;
import edu.marcio.todo_app.exception.task.DuplicatedNameException;
import edu.marcio.todo_app.exception.task.NotFoundTaskException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArguments(IllegalArgumentException e) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(
            new ErrorResponse(400, "Bad request", e.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(DuplicatedNameException.class)
  public ResponseEntity<ErrorResponse> handleDuplicatedName(DuplicatedNameException e) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(
            new ErrorResponse(409, "Conflict", e.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(NotFoundTaskException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NotFoundTaskException e) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(
            new ErrorResponse(404, "Not Found", e.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleInternalErrorServer(Exception e) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            new ErrorResponse(500, "Internal Server Error", "An unexpected error occurred", LocalDateTime.now()));
  }
}
