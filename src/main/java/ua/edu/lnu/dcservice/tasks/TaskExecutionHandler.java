package ua.edu.lnu.dcservice.tasks;

import org.apache.commons.lang3.tuple.Pair;
import ua.edu.lnu.dcservice.entities.enums.TaskStatus;

public interface TaskExecutionHandler {
    void updateTaskExecution(Long taskId, TaskStatus taskStatus, double accomplishment);

    Pair<TaskStatus, Double> getTaskStatus(Long taskId);

    void addTask(Long taskId);

    boolean contains(long taskId);
}
