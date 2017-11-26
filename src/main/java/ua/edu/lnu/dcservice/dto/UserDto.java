package ua.edu.lnu.dcservice.dto;

import lombok.Data;
import ua.edu.lnu.dcservice.entities.ApplicationUser;
import ua.edu.lnu.dcservice.entities.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

import static ua.edu.lnu.dcservice.utils.CollectionUtils.mapToList;

@Data
public class UserDto {

    private long id;

    @Size(min = 6, max = 64)
    private String username;

    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private List<String> roles;

    public static UserDto from(ApplicationUser user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setUsername(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setRoles(mapToList(user.getRoles(), Role::getRole));

        return userDto;
    }

    public static ApplicationUser to(UserDto userDto) {
        ApplicationUser user = new ApplicationUser();

        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());

        return user;
    }
}
