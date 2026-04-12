package edu.marcio.todo_app.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import edu.marcio.todo_app.filters.JwtFilter;
import edu.marcio.todo_app.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  JwtFilter jwtFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()
            .requestMatchers(HttpMethod.POST, "/register").permitAll()
            .anyRequest().authenticated());
    http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();

  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
    corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "OPTIONS"));
    corsConfig.setAllowedHeaders(Arrays.asList("*"));
    corsConfig.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
    corsSource.registerCorsConfiguration("/**", corsConfig);
    return corsSource;
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return new CustomUserDetailsService();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authManager(UserDetailsService userDetailsService) {
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    return new ProviderManager(daoAuthenticationProvider);
  }

}
