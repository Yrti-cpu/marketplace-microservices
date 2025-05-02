package org.yrti.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.yrti.auth.dto.AuthResponse;
import org.yrti.auth.dto.LoginRequest;
import org.yrti.auth.dto.RegisterRequest;
import org.yrti.auth.model.Role;
import org.yrti.auth.model.User;
import org.yrti.auth.repository.UserRepository;
import org.yrti.auth.security.JwtService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("Пользователь с таким email уже существует");
    }
    var user = User.builder()
        .name(request.getName())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.CUSTOMER) // по умолчанию покупатель
        .build();

    userRepository.save(user);
    log.debug("Регистрация пользователя: email={}", request.getEmail());

    var jwtToken = jwtService.generateToken(user);
    return new AuthResponse(jwtToken);
  }

  public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );

    var user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

    log.debug("Авторизация пользователя: email={}", request.getEmail());

    var jwtToken = jwtService.generateToken(user);
    return new AuthResponse(jwtToken);
  }
}