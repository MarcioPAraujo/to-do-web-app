package edu.marcio.todo_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import edu.marcio.todo_app.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
  boolean existsByName(String name);

  void deleteById(Long id);

  boolean existsByNameAndIdNot(String name, Long id);

  Page<Task> findAll(Pageable pageable);
}
