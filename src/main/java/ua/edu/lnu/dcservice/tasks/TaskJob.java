package ua.edu.lnu.dcservice.tasks;

import ua.edu.lnu.dcservice.dto.TaskDto;
import ua.edu.lnu.dcservice.exceptions.ServiceException;
import ua.edu.lnu.dcservice.services.TaskService;

public abstract class TaskJob {
    protected final TaskService taskService;

    protected final TaskExecutionHandler taskExecutionHandler;

    protected final TaskDto task;

    protected long taskSize;

    protected long donePart;

    public TaskJob(TaskDto task, TaskService taskService, TaskExecutionHandler taskExecutionHandler) {
        this.task = task;
        this.taskService = taskService;
        this.taskExecutionHandler = taskExecutionHandler;
    }

    public static TaskJob createTaskJob(TaskDto task, TaskService taskService, TaskExecutionHandler taskExecutionHandler) {
        switch (task.getTaskType()) {
            case LINEAR_EQUATIONS_SYSTEM:
                return LinearEquationsTaskJob.createJob(task, taskService, taskExecutionHandler);
            default:
                throw new ServiceException("No task job found for this task type {}", task.getTaskType());
        }
    }

    public double taskReadyStatus() {
        return ((double) donePart) / taskSize;
    }

    public abstract void run();
}
