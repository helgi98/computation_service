package ua.edu.lnu.dcservice.services;

import org.springframework.web.multipart.MultipartFile;
import ua.edu.lnu.dcservice.dto.TaskDto;
import ua.edu.lnu.dcservice.entities.enums.TaskStatus;
import ua.edu.lnu.dcservice.entities.enums.TaskType;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface TaskService {
    Optional<TaskDto> getTask(long taskId);

    List<TaskDto> getUserTasks();

    void updateStatus(long id, TaskStatus taskStatus);

    TaskDto addTask(String taskName, TaskType taskType, MultipartFile file);

    void executeTask(Long taskId);

    void killTask(Long taskId);

    File loadTaskInputFile(long id);

    File createTaskOutputFile(long id);
}
