package ua.edu.lnu.dcservice.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import ua.edu.lnu.dcservice.dto.UserDto;

public interface ApplicationUserService extends UserDetailsService {
    void saveUser(UserDto userDto);

    UserDto findUserByUsername(String username);
}
