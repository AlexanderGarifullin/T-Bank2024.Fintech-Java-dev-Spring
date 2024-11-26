package com.fin.spr.services.security;

import com.fin.spr.auth.JwtAuthenticationResponse;
import com.fin.spr.auth.UserDetails;
import com.fin.spr.controllers.payload.security.AuthenticationPayload;
import com.fin.spr.controllers.payload.security.RegistrationPayload;
import com.fin.spr.exceptions.UserAlreadyRegisterException;
import com.fin.spr.exceptions.UserNotFoundException;
import com.fin.spr.models.security.Role;
import com.fin.spr.models.security.Token;
import com.fin.spr.models.security.User;
import com.fin.spr.repository.security.TokenRepository;
import com.fin.spr.repository.security.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse register(@NotNull RegistrationPayload registrationRequest) {
        userRepository.findByLogin(registrationRequest.login())
                .ifPresent(user -> {
                    throw new UserAlreadyRegisterException(user.getLogin());
                });

        User user = User.builder()
                .name(registrationRequest.name())
                .login(registrationRequest.login())
                .role(Role.USER)
                .hashedPassword(passwordEncoder.encode(registrationRequest.password()))
                .build();

        String jwtToken = jwtService.generateToken(new UserDetails(user), false);

        Token token = Token.builder()
                .token(jwtToken)
                .user(user)
                .build();

        userRepository.save(user);
        tokenRepository.save(token);

        return new JwtAuthenticationResponse(jwtToken);
    }

    public JwtAuthenticationResponse login(@NotNull AuthenticationPayload authenticationPayload) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationPayload.login(),
                authenticationPayload.password()
        ));

        var user = userRepository.findByLogin(authenticationPayload.login())
                .orElseThrow(() -> new UserNotFoundException(authenticationPayload.login()));

        var tokens = tokenRepository.findAllByUserAndRevoked(user, false);
        tokens.forEach(token -> token.setRevoked(true));
        tokenRepository.saveAll(tokens);

        String jwtToken = jwtService.generateToken(new UserDetails(user), authenticationPayload.rememberMe());

        Token token = Token.builder()
                .token(jwtToken)
                .user(user)
                .build();
        tokenRepository.save(token);

        return new JwtAuthenticationResponse(jwtToken);
    }

    public void logout(@NotNull Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();

    }
}

