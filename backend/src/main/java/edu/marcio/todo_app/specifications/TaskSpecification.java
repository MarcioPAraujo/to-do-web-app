package edu.marcio.todo_app.specifications;

import org.springframework.data.jpa.domain.Specification;

import edu.marcio.todo_app.model.Task;

public final class TaskSpecification {
  public static Specification<Task> hasTitle(String name) {
    return (root, query, criteriaBuilder) -> {
      if (name == null || name.isBlank()) {
        return null;
      }
      return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    };
  }

  public static Specification<Task> hasCompleted(Boolean completed) {
    return (root, query, criteriaBuilder) -> {
      if (completed == null) {
        return null;
      }
      return criteriaBuilder.equal(root.get("completed"), completed);
    };
  }
}
