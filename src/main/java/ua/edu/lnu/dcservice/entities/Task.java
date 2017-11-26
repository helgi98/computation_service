package ua.edu.lnu.dcservice.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.lnu.dcservice.entities.enums.TaskStatus;
import ua.edu.lnu.dcservice.entities.enums.TaskType;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_id_seq")
    @SequenceGenerator(name = "task_id_seq", sequenceName = "task_id_seq")
    private long id;

    private String taskName;

    @Enumerated(value = EnumType.STRING)
    private TaskStatus taskStatus;

    @Enumerated(value = EnumType.STRING)
    private TaskType taskType;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private ApplicationUser createdBy;
}
