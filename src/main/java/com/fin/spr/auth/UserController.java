package com.fin.spr.auth;

import com.fin.spr.controllers.payload.security.AuthenticationPayload;
import com.fin.spr.controllers.payload.security.ChangePasswordPayload;
import com.fin.spr.controllers.payload.security.RegistrationPayload;
import com.fin.spr.services.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        authenticationService.logout(authentication);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("change-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody ChangePasswordPayload changePasswordRequest,
            Authentication authentication
    ) {
        authenticationService.changePassword(changePasswordRequest, authentication);
        return ResponseEntity.ok().build();
    }
}
