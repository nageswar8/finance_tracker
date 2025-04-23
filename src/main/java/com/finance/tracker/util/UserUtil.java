package com.finance.tracker.util;

import com.finance.tracker.entity.User;
import com.finance.tracker.repository.UserRepository;

import java.security.Principal;

public class UserUtil {

    public User getUser(Principal principal, UserRepository userRepository) {
        String email = principal.getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found"));
    }

}
