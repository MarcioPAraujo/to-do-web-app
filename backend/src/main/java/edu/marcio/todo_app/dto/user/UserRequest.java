package edu.marcio.todo_app.dto.user;

import lombok.Data;

@Data
public class UserRequest {
  private String email;
  private String password;
  private String role;
  private String name;
}
