package com.project.services.details;

import com.project.model.entitys.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.model.repositorys.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private final Usuario user;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = userR.getRole().name(); // ex: MANAGER
        String springRole = "ROLE_" + role;
        return List.of(new SimpleGrantedAuthority(springRole));
    }


}