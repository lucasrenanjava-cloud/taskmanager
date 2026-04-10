package com.lucasrenan.taskmanager.service;

import com.lucasrenan.taskmanager.dto.request.LoginRequest;
import com.lucasrenan.taskmanager.dto.request.RegisterRequest;
import com.lucasrenan.taskmanager.dto.response.LoginResponse;
import com.lucasrenan.taskmanager.exception.EmailJaCadastradoException;
import com.lucasrenan.taskmanager.model.entity.User;
import com.lucasrenan.taskmanager.repository.UserRepository;
import com.lucasrenan.taskmanager.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException("E-mail já cadastrado: " + request.email());
        }

        User user = new User();
        user.setNome(request.nome());
        user.setEmail(request.email());
        user.setSenha(passwordEncoder.encode(request.senha()));

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = jwtService.gerarToken(user);
        return new LoginResponse(token);
    }
}
