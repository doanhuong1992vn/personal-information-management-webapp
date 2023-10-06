package com.user_service.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

public record UserDetailsHolder (UserDetails userDetails, Set<String> loggedTokensMemory) {
}
