package com.cdac.cls_services.auth;

import com.cdac.cls_services.call_logs.models.UserModel;
import com.cdac.cls_services.call_logs.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Find the user in user_m by email
        // If not found, throw exception — Spring Security handles the 401

        UserModel user = userRepo.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("User not found with this email"));

        // Convert role_id integer into a Spring Security "authority"
        // Spring Security expects roles as strings like "ROLE_1", "ROLE_2" etc.
        // Prefix with "ROLE_" by convention — used later for role-based access
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("Role_" + user.getRole());

        // Build and return a Spring Security UserDetails object
        return new User(user.getEmail(), user.getPassword(), List.of(authority));
    }
}
