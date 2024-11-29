package com.fin.spr.services.security;

import com.fin.spr.repository.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<com.fin.spr.models.security.User> user = userRepository.findByLogin(login);
        if (user.isPresent())  return new com.fin.spr.auth.UserDetails(user.get());
        else throw new UsernameNotFoundException(login);
    }
}
