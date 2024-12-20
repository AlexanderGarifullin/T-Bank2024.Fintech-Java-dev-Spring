package com.fin.spr.auth;

import com.fin.spr.BaseIntegrationTest;
import com.fin.spr.controllers.payload.security.AuthenticationPayload;
import com.fin.spr.controllers.payload.security.ChangePasswordPayload;
import com.fin.spr.controllers.payload.security.RegistrationPayload;
import com.fin.spr.models.security.User;
import com.fin.spr.repository.security.TokenRepository;
import com.fin.spr.repository.security.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ActiveProfiles("test")
class UserControllerIntegrationTest extends BaseIntegrationTest {

    private static final String uri = "/api/v1/auth";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Test
    public void register_success() throws Exception {
        RegistrationPayload payload = RegistrationPayload.builder()
                .login("register-login")
                .name("register-name")
                .password("register-password")
                .build();

        var jwtResponse = register(payload);

        var user = userRepository.findByLogin("register-login");
        var token = tokenRepository.findByToken(jwtResponse.token());

        assertAll(
                () -> assertThat(jwtResponse.token()).isNotEmpty(),

                () -> assertThat(token).isPresent(),
                () -> assertThat(token.get().isRevoked()).isFalse(),

                () -> assertThat(user).isPresent()
        );

        deleteUserFromDb(user.get());
    }

    @Test
    public void login_success() throws Exception {
        RegistrationPayload payloadToRegister = RegistrationPayload.builder()
                .login("register-login")
                .name("register-name")
                .password("register-password")
                .build();

        var jwtResponse = register(payloadToRegister);

        var payloadToLogin = AuthenticationPayload.builder()
                .login("register-login")
                .password("register-password")
                .rememberMe(true)
                .build();

        var response = mockMvc.perform(post(uri + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payloadToLogin)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        var jwtFromLogin =  objectMapper.readValue(response.getContentAsString(), JwtAuthenticationResponse.class);

        var oldToken = tokenRepository.findByToken(jwtResponse.token());
        var newToken = tokenRepository.findByToken(jwtFromLogin.token());

        assertAll(
                () -> assertThat(jwtFromLogin.token()).isNotNull(),

                () -> assertThat(jwtResponse.token()).isNotNull(),
                () -> assertThat(jwtFromLogin.token()).isNotEqualTo(jwtResponse.token()),

                () -> assertThat(oldToken).isPresent(),
                () -> assertThat(newToken).isPresent(),

                () -> assertThat(oldToken.get().isRevoked()).isTrue(),
                () -> assertThat(newToken.get().isRevoked()).isFalse(),

                () -> assertThat(oldToken.get()).isNotEqualTo(newToken.get())
        );

        deleteUserFromDb(userRepository.findByLogin("register-login").get());
    }

    @Test
    public void logout_success() throws Exception {
        RegistrationPayload payloadToRegister = RegistrationPayload.builder()
                .login("register-login")
                .name("register-name")
                .password("register-password")
                .build();

        var jwtResponse = register(payloadToRegister);

        mockMvc.perform(post(uri + "/logout")
                        .header("Authorization", "Bearer %s".formatted(jwtResponse.token())))
                .andExpectAll(
                        status().isOk())
                .andReturn()
                .getResponse();

        var oldToken = tokenRepository.findByToken(jwtResponse.token());

        assertAll(
                () -> assertThat(oldToken).isPresent(),
                () -> assertThat(oldToken.get().isRevoked()).isTrue()
        );

        deleteUserFromDb(userRepository.findByLogin("register-login").get());
    }

    @Test
    public void changePassword_success() throws Exception {
        RegistrationPayload payloadToRegister = RegistrationPayload.builder()
                .login("register-login")
                .name("register-name")
                .password("register-password")
                .build();

        var jwtResponse = register(payloadToRegister);

        var oldUser = userRepository.findByLogin("register-login");

        var changePasswordPayload = ChangePasswordPayload.builder()
                .newPassword("new-password")
                .twoFactorCode("0000")
                .build();

        mockMvc.perform(patch(uri + "/change-password")
                        .header("Authorization", "Bearer %s".formatted(jwtResponse.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordPayload)))
                .andExpectAll(
                        status().isOk())
                .andReturn()
                .getResponse();

        var newUser = userRepository.findByLogin("register-login");

        assertAll(
                () -> assertThat(oldUser).isPresent(),
                () -> assertThat(newUser).isPresent(),

                () -> assertThat(oldUser.get()).isNotEqualTo(newUser.get()),
                () -> assertThat(oldUser.get().getHashedPassword()).isNotEqualTo(newUser.get().getHashedPassword())
        );

        var payloadToLogin = AuthenticationPayload.builder()
                .login("register-login")
                .password("new-password")
                .rememberMe(true)
                .build();

        var loginResponse = mockMvc.perform(post(uri + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payloadToLogin)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        var jwtFromLogin =  objectMapper.readValue(loginResponse.getContentAsString(), JwtAuthenticationResponse.class);

        var loginUser = userRepository.findByLogin("register-login");
        var loginToken=  tokenRepository.findByToken(jwtFromLogin.token());
        var oldToken=  tokenRepository.findByToken(jwtResponse.token());

        assertAll(
                () -> assertThat(jwtFromLogin.token()).isNotEmpty(),

                () ->  assertThat(loginToken).isPresent(),
                () ->  assertThat(oldToken).isPresent(),

                () ->  assertThat(loginToken.get().isRevoked()).isFalse(),
                () ->  assertThat(oldToken.get().isRevoked()).isTrue(),

                () -> assertThat(loginToken.get()).isNotEqualTo(oldToken),

                () -> assertThat(loginUser).isPresent(),
                () -> assertThat(loginUser.get()).isEqualTo(newUser.get())
        );

        deleteUserFromDb(loginUser.get());
    }

    private JwtAuthenticationResponse register(RegistrationPayload payload) throws Exception {
        var response = mockMvc.perform(post(uri + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        return objectMapper.readValue(response.getContentAsString(), JwtAuthenticationResponse.class);
    }

    private void deleteUserFromDb(User user) {
        userRepository.delete(user);
    }
}