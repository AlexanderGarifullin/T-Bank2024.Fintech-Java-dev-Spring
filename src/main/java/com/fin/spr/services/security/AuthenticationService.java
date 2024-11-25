package com.fin.spr.services.security;

import com.fin.spr.auth.AuthenticationResponse;
import com.fin.spr.auth.RegistrationRequest;
import com.fin.spr.exceptions.UserAlreadyRegisterException;
import com.fin.spr.models.security.Role;
import com.fin.spr.models.security.User;
import com.fin.spr.repository.security.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse register(@NotNull RegistrationRequest registrationRequest) {
        userRepository.findByLogin(registrationRequest.login())
                .ifPresent(user  -> {
                    throw new UserAlreadyRegisterException(user.getLogin());
                });

        User user = User.builder()
                .name(registrationRequest.name())
                .login(registrationRequest.login())
                .role(Role.USER)
                .hashedPassword(passwordEncoder.encode(registrationRequest.password()))
                .build();
        userRepository.save(user);

        return new AuthenticationResponse();
    }
}

