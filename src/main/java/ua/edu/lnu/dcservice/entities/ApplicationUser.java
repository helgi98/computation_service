package ua.edu.lnu.dcservice.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NamedQueries({
        @NamedQuery(name = ApplicationUser.FIND_BY_USERNAME, query = "SELECT au FROM ApplicationUser au WHERE au.username = :username"),
        @NamedQuery(name = ApplicationUser.CHECK_IF_UNIQUE, query = "SELECT (CASE WHEN COUNT(au) > 0 THEN FALSE ELSE TRUE END) " +
                "FROM ApplicationUser au WHERE au.username = :username OR au.email = :email")
})
@EqualsAndHashCode(exclude = {"roles", "tasks"})
@ToString(exclude = {"roles", "tasks"})
public class ApplicationUser {
    public static final String FIND_BY_USERNAME = "findByUsername";
    public static final String CHECK_IF_UNIQUE = "checkIfUnique";
    public static final String LOAD_BASKET = "loadBasket";
    public static final String ADD_TO_BASKET = "addToBasket";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq")
    private long id;

    private String username;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "USER_ROLE", joinColumns = {
            @JoinColumn(name = "USER_ID")
    }, inverseJoinColumns = {
            @JoinColumn(name = "ROLE_ID")
    })
    private List<Role> roles;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Task> tasks;
}
