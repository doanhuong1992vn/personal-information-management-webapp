package com.user_service.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class UserContextHolder {
    private static final HashMap<String, UserDetailsHolder> loggedUsersHolder = new HashMap<>();

    private static final UserContextHolder userContextHolder = new UserContextHolder();

    private UserContextHolder() {
    }

    public static UserContextHolder getInstance() {
        return userContextHolder;
    }

    public boolean removeBearerToken(String username, String token) {
        if (loggedUsersHolder.containsKey(username)) {
            UserDetailsHolder userDetailsHolder = loggedUsersHolder.get(username);
            Set<String> loggedTokensMemory = userDetailsHolder.loggedTokensMemory();
            if (loggedTokensMemory.remove(token)) {
                loggedUsersHolder.put(username, userDetailsHolder);
                return true;
            }
        }
        return false;
    }


    public void saveToken(String username, String token) {
        if (loggedUsersHolder.containsKey(username)) {
            UserDetailsHolder userDetailsHolder = loggedUsersHolder.get(username);
            Set<String> loggedTokensMemory = userDetailsHolder.loggedTokensMemory();
            loggedTokensMemory.add(token);
            loggedUsersHolder.put(username, userDetailsHolder);
        }
    }


    public void saveUserDetails (UserDetails userDetails) {
        String username = userDetails.getUsername();
        if (!loggedUsersHolder.containsKey(username)) {
            loggedUsersHolder.put(username, new UserDetailsHolder(userDetails, new HashSet<>()));
        }
    }


    public boolean isValidLoggedUser (String username, String token) {
        if (loggedUsersHolder.containsKey(username)) {
            UserDetailsHolder userDetailsHolder = loggedUsersHolder.get(username);
            Set<String> loggedTokensMemory = userDetailsHolder.loggedTokensMemory();
            return loggedTokensMemory.contains(token);
        }
        return false;
    }


    public UserDetails getUserDetails(String username) {
        if (loggedUsersHolder.containsKey(username)) {
            return loggedUsersHolder.get(username).userDetails();
        }
        return null;
    }

}
