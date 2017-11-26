package ua.edu.lnu.dcservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ua.edu.lnu.dcservice.entities.Task;
import ua.edu.lnu.dcservice.entities.enums.TaskStatus;
import ua.edu.lnu.dcservice.entities.enums.TaskType;

@Data
@AllArgsConstructor
public class TaskDto {
    private long id;

    private String name;

    private TaskStatus taskStatus;

    private TaskType taskType;

    private String createdBy;


    public static TaskDto from(Task task) {
        return new TaskDto(task.getId(), task.getTaskName(), task.getTaskStatus(), task.getTaskType(), task.getCreatedBy().getUsername());
    }
}
