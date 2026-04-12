package edu.marcio.todo_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.marcio.todo_app.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findByEmail(String email);
}
