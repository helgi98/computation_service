package ua.edu.lnu.dcservice.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static java.util.Objects.nonNull;

public class SecurityUtils {
    public static String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (nonNull(authentication)) {
            return authentication.getName();
        }

        return null;
    }
}
