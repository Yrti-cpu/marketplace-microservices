package org.yrti.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.yrti.auth.dto.AuthResponse;
import org.yrti.auth.dto.LoginRequest;
import org.yrti.auth.dto.RegisterRequest;
import org.yrti.auth.model.Role;
import org.yrti.auth.model.User;
import org.yrti.auth.repository.UserRepository;
import org.yrti.auth.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;
  private JwtService jwtService;
  private AuthenticationManager authenticationManager;
  private AuthService authService;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    passwordEncoder = mock(PasswordEncoder.class);
    jwtService = mock(JwtService.class);
    authenticationManager = mock(AuthenticationManager.class);

    authService = new AuthService(
        userRepository,
        passwordEncoder,
        jwtService,
        authenticationManager
    );
  }

  @Test
  @DisplayName("Успешная регистрация")
  void testRegister_shouldSaveUserAndReturnToken() {
    // Подготовка
    RegisterRequest request = RegisterRequest.builder()
        .name("yrti")
        .email("yrti@example.com")
        .password("123456")
        .build();

    when(passwordEncoder.encode("123456")).thenReturn("encoded-pass");
    when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

    // Выполнение
    AuthResponse response = authService.register(request);

    // Проверка
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-pass");
    assertThat(response.getAccessToken()).isEqualTo("jwt-token");
  }

  @Test
  @DisplayName("Успешная авторизация")
  void testLogin_shouldReturnTokenIfValidCredentials() {
    // Подготовка
    String email = "yrti@example.com";
    String password = "123456";

    LoginRequest request = LoginRequest.builder()
        .email(email)
        .password(password)
        .build();

    User user = User.builder()
        .email(email)
        .password("encoded-pass")
        .role(Role.CUSTOMER)
        .build();

    // Мокаем успешную аутентификацию
    Authentication auth = new UsernamePasswordAuthenticationToken(email, password);
    when(authenticationManager.authenticate(any()))
        .thenReturn(auth);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(jwtService.generateToken(user)).thenReturn("jwt-token");

    // Выполнение
    AuthResponse response = authService.login(request);

    // Проверка
    verify(authenticationManager).authenticate(
        new UsernamePasswordAuthenticationToken(email, password)
    );
    verify(userRepository).findByEmail(email);
    assertThat(response.getAccessToken()).isEqualTo("jwt-token");
  }
}