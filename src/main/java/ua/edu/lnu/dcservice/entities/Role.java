package ua.edu.lnu.dcservice.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@NamedQueries({
        @NamedQuery(name = Role.FIND_ROLE_BY_NAME, query = "SELECT r FROM Role r WHERE r.role = :role")
})
public class Role {
    public static final String FIND_ROLE_BY_NAME = "findRoleByName";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_seq")
    @SequenceGenerator(name = "role_id_seq", sequenceName = "role_id_seq")
    private long id;

    private String role;

}
