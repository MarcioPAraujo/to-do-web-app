package edu.marcio.todo_app.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException e) {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponse(
            401,
            "Unauthorized",
            "Invalid email or password",
            LocalDateTime.now()));
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUsernameNotFound(UsernameNotFoundException e) {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponse(
            401,
            "Unauthorized",
            "Invalid email or password",
            LocalDateTime.now()));
  }

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<ErrorResponse> handleDisabled(DisabledException e) {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponse(
            401,
            "Unauthorized",
            "Account is disabled",
            LocalDateTime.now()));
  }

  @ExceptionHandler(LockedException.class)
  public ResponseEntity<ErrorResponse> handleLocked(LockedException e) {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponse(
            401,
            "Unauthorized",
            "Account is locked",
            LocalDateTime.now()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleInternalErrorServer(Exception e) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            new ErrorResponse(500, "Internal Server Error", "An unexpected error occurred", LocalDateTime.now()));
  }
}
