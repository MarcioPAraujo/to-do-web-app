package edu.marcio.todo_app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.marcio.todo_app.dto.user.UserRequest;
import edu.marcio.todo_app.model.User;
import edu.marcio.todo_app.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
  @Autowired
  private UserRepository userRepository;

  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public User registerNewUser(UserRequest userRequest) {
    User user = new User();
    user.setEmail(userRequest.getEmail());
    user.setName(userRequest.getName());
    user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
    user.setRole(userRequest.getRole());
    Optional<User> optUser = Optional.of(userRepository.save(user));

    if (optUser.isEmpty()) {
      throw new RuntimeException("failed in save user");
    }

    return user;
  }
}
