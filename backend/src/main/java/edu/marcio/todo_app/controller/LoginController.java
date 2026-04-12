package edu.marcio.todo_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

import edu.marcio.todo_app.dto.login.LoginRequest;
import edu.marcio.todo_app.dto.login.LoginResponse;
import edu.marcio.todo_app.dto.user.UserRequest;
import edu.marcio.todo_app.model.User;
import edu.marcio.todo_app.service.CustomUserDetailsService;
import edu.marcio.todo_app.service.UserService;
import edu.marcio.todo_app.util.JwtUtil;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class LoginController {

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  JwtUtil jwtUtil;

  @Autowired
  UserService userService;

  @Autowired
  CustomUserDetailsService customUserDetailsService;

  @PostMapping("/auth/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

    authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    customUserDetailsService.loadUserByUsername(request.getEmail());

    return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(jwtUtil.generateToken(request.getEmail())));
  }

  @PostMapping("/register")
  public ResponseEntity<User> registerUser(@RequestBody UserRequest request) {
    User newUser = userService.registerNewUser(request);
    return ResponseEntity.status(HttpStatus.OK).body(newUser);
  }

}
