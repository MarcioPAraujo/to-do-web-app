package edu.marcio.todo_app.model;

import org.hibernate.validator.constraints.UniqueElements;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Task name cannot be blank")
  @UniqueElements(message = "Task name must be unique")
  private String name;

  private boolean completed;
}
