package com.fin.spr.auth;

import com.fin.spr.controllers.payload.security.AuthenticationPayload;
import com.fin.spr.controllers.payload.security.RegistrationPayload;
import com.fin.spr.services.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public JwtAuthenticationResponse register(@RequestBody RegistrationPayload registrationRequest) {
        return authenticationService.register(registrationRequest);
    }

    @PostMapping("/login")
    public JwtAuthenticationResponse login(@RequestBody AuthenticationPayload authenticationPayload) {
        return authenticationService.login(authenticationPayload);
    }
}
