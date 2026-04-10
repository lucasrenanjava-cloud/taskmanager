package com.lucasrenan.taskmanager.service;

import com.lucasrenan.taskmanager.dto.request.LoginRequest;
import com.lucasrenan.taskmanager.dto.request.RegisterRequest;
import com.lucasrenan.taskmanager.dto.response.LoginResponse;
import com.lucasrenan.taskmanager.exception.EmailJaCadastradoException;
import com.lucasrenan.taskmanager.model.entity.User;
import com.lucasrenan.taskmanager.repository.UserRepository;
import com.lucasrenan.taskmanager.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Test
    void deveCadastrarUsuarioComSucesso() {
        RegisterRequest request = new RegisterRequest("Lucas", "lucas@email.com", "123456");

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.senha())).thenReturn("senhaEncriptada");

        authService.register(request);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        RegisterRequest request = new RegisterRequest("Lucas", "lucas@email.com", "123456");

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () -> authService.register(request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deveRealizarLoginComSucesso() {
        LoginRequest request = new LoginRequest("lucas@email.com", "123456");

        User user = new User();
        user.setEmail("lucas@email.com");
        user.setSenha("senhaEncriptada");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(jwtService.gerarToken(user)).thenReturn("token-jwt-mock");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("token-jwt-mock", response.token());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoNoLogin() {
        LoginRequest request = new LoginRequest("naoexiste@email.com", "123456");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }
}