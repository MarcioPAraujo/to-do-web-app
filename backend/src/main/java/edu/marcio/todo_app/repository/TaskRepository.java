package edu.marcio.todo_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import edu.marcio.todo_app.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
  boolean existsByName(String name);

  Task findById(String name);

  void deleteById(Long id);

  boolean existsByNameAndIdNot(String name, Long id);

  Page<Task> findAll(Pageable pageable);
}
