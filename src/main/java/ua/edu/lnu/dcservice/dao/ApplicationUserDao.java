package ua.edu.lnu.dcservice.dao;

import ua.edu.lnu.dcservice.entities.ApplicationUser;
import ua.edu.lnu.dcservice.entities.Role;

import java.util.Optional;

public interface ApplicationUserDao extends GenericDao<ApplicationUser, Long> {
    Optional<ApplicationUser> findByUsername(String username);

    boolean isUnique(String username, String email);

    Optional<Role> findRoleByName(String roleName);
}
