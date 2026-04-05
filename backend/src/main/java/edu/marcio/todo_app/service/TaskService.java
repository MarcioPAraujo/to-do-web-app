package edu.marcio.todo_app.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import edu.marcio.todo_app.dto.filter.TaskPageFilters;
import edu.marcio.todo_app.dto.task.TaskRequest;
import edu.marcio.todo_app.dto.task.TaskResponse;
import edu.marcio.todo_app.exception.task.DuplicatedNameException;
import edu.marcio.todo_app.exception.task.NotFoundTaskException;
import edu.marcio.todo_app.model.Task;
import edu.marcio.todo_app.repository.TaskRepository;
import edu.marcio.todo_app.specifications.TaskSpecification;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

  private final TaskRepository taskRepository;

  public TaskResponse create(TaskRequest request) {
    String name = request.getName();
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Task name cannot be blank");
    }

    if (name.length() > 255) {
      throw new IllegalArgumentException("Task name cannot be longer than 255 characters");
    }

    Boolean exists = taskRepository.existsByName(name);

    if (exists) {
      throw new DuplicatedNameException(name);
    }

    Task task = new Task();
    task.setName(name);
    task.setCompleted(false);

    Optional<Task> saved = Optional.ofNullable(taskRepository.save(task));

    if (saved.isEmpty()) {
      throw new RuntimeException("Failed to save task");
    }
    Task savedTask = saved.get();
    return new TaskResponse(savedTask.getId(), savedTask.getName(), savedTask.isCompleted());
  }

  public TaskResponse edit(Long id, TaskRequest request) {
    Optional<Task> taskOpt = taskRepository.findById(id);
    if (taskOpt.isEmpty()) {
      throw new NotFoundTaskException(id);
    }
    Task task = taskOpt.get();
    String name = request.getName();

    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Task name cannot be blank");
    }

    if (name.length() > 255) {
      throw new IllegalArgumentException("Task name cannot be longer than 255 characters");
    }

    if (taskRepository.existsByNameAndIdNot(name, id)) {
      throw new DuplicatedNameException(name);
    }
    task.setName(name);
    task.setCompleted(task.isCompleted());
    Optional<Task> saved = Optional.ofNullable(taskRepository.save(task));
    if (saved.isEmpty()) {
      throw new RuntimeException("Failed to save task");
    }

    Task savedTask = saved.get();
    return new TaskResponse(savedTask.getId(), savedTask.getName(), savedTask.isCompleted());
  }

  public TaskResponse toggleCompleted(Long id) {
    Optional<Task> taskOpt = taskRepository.findById(id);
    if (taskOpt.isEmpty()) {
      throw new NotFoundTaskException(id);
    }
    Task task = taskOpt.get();
    task.setCompleted(!task.isCompleted());
    Optional<Task> saved = Optional.ofNullable(taskRepository.save(task));
    if (saved.isEmpty()) {
      throw new RuntimeException("Failed to save task");
    }
    Task savedTask = saved.get();
    return new TaskResponse(savedTask.getId(), savedTask.getName(), savedTask.isCompleted());
  }

  public TaskResponse delete(Long id) {
    Optional<Task> optTask = taskRepository.findById(id);
    if (optTask.isEmpty()) {
      throw new NotFoundTaskException(id);
    }

    taskRepository.deleteById(id);

    Task task = optTask.get();

    return new TaskResponse(task.getId(), task.getName(), task.isCompleted());
  }

  public TaskResponse getTaskById(Long id) {
    Optional<Task> optTask = taskRepository.findById(id);
    if (optTask.isEmpty()) {
      throw new NotFoundTaskException(id);
    }

    Task task = optTask.get();

    return new TaskResponse(task.getId(), task.getName(), task.isCompleted());
  }

  public Page<TaskResponse> getAllTasks(Pageable pageable, TaskPageFilters filters) {

    if (pageable.getPageNumber() < 0) {
      throw new IllegalArgumentException("Page number cannot be negative");
    }

    if (pageable.getPageSize() <= 0) {
      throw new IllegalArgumentException("Page size must be greater than zero");
    }

    Specification<Task> spec = Specification.where(TaskSpecification.hasTitle(filters.getName()))
        .and(TaskSpecification.hasCompleted(filters.getCompleted()));
    return taskRepository.findAll(spec, pageable)
        .map(task -> new TaskResponse(task.getId(), task.getName(), task.isCompleted()));
  }
}
