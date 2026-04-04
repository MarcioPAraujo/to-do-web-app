package edu.marcio.todo_app.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import edu.marcio.todo_app.dto.task.TaskRequest;
import edu.marcio.todo_app.dto.task.TaskRequestEdit;
import edu.marcio.todo_app.dto.task.TaskResponse;
import edu.marcio.todo_app.exception.task.DuplicatedNameException;
import edu.marcio.todo_app.exception.task.NotFoundTaskException;
import edu.marcio.todo_app.model.Task;
import edu.marcio.todo_app.repository.TaskRepository;
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

    if (taskRepository.existsByName(name)) {
      throw new DuplicatedNameException(name);
    }

    Task task = new Task(null, name, false);
    Optional<Task> saved = Optional.ofNullable(taskRepository.save(task));
    if (saved.isEmpty()) {
      throw new RuntimeException("Failed to save task");
    }
    Task savedTask = saved.get();
    return new TaskResponse(savedTask.getId(), savedTask.getName(), savedTask.isCompleted());
  }

  public TaskResponse edit(TaskRequestEdit request) {
    Long id = request.getId();
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
    task.setCompleted(request.getCompleted());
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

  public Page<TaskResponse> getAllTasks(Pageable pageable) {
    return taskRepository.findAll(pageable)
        .map(task -> new TaskResponse(task.getId(), task.getName(), task.isCompleted()));
  }
}
