package ua.edu.lnu.dcservice.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.lnu.dcservice.dao.ApplicationUserDao;
import ua.edu.lnu.dcservice.entities.ApplicationUser;
import ua.edu.lnu.dcservice.entities.Role;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ApplicationUserDaoImpl extends HibernateDao<ApplicationUser, Long> implements ApplicationUserDao {
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String ROLE_NAME = "role";
    private static final String USER_ID = "userId";
    private static final String ARTICLE_ID = "articleId";

    public ApplicationUserDaoImpl() {
        super(ApplicationUser.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApplicationUser> findByUsername(String username) {
        TypedQuery<ApplicationUser> query = session().createNamedQuery(ApplicationUser.FIND_BY_USERNAME, ApplicationUser.class);
        query.setParameter(USERNAME, username);

        List<ApplicationUser> users = query.getResultList();

        return users.stream().findFirst();

    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUnique(String username, String email) {
        TypedQuery<Boolean> query = session().createNamedQuery(ApplicationUser.CHECK_IF_UNIQUE, Boolean.class)
                .setParameter(USERNAME, username)
                .setParameter(EMAIL, email);

        return query.getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findRoleByName(String roleName) {
        TypedQuery<Role> query = session().createNamedQuery(Role.FIND_ROLE_BY_NAME, Role.class);
        query.setParameter(ROLE_NAME, roleName);

        List<Role> roles = query.getResultList();

        return roles.stream().findFirst();
    }
}
