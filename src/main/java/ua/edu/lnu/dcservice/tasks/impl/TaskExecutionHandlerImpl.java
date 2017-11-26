package ua.edu.lnu.dcservice.tasks.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.lnu.dcservice.entities.enums.TaskStatus;
import ua.edu.lnu.dcservice.exceptions.ServiceException;
import ua.edu.lnu.dcservice.exceptions.ValidationException;
import ua.edu.lnu.dcservice.services.TaskService;
import ua.edu.lnu.dcservice.tasks.TaskExecutionHandler;
import ua.edu.lnu.dcservice.tasks.exceptions.TaskCancelException;

import javax.annotation.PostConstruct;

import static java.util.Objects.isNull;

@Service
public class TaskExecutionHandlerImpl implements TaskExecutionHandler {
    private static final String TASKS_MAP = "tasksMap";

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    private TaskService taskService;

    private IMap<Long, Pair<TaskStatus, Double>> tasksMap;

    @PostConstruct
    private void init() {
        tasksMap = hazelcastInstance.getMap(TASKS_MAP);
    }

    @Override
    public void updateTaskExecution(Long taskId, TaskStatus taskStatus, double accomplishment) {
        if (tasksMap.containsKey(taskId)) {
            tasksMap.remove(taskId);

            if (taskStatus == TaskStatus.IN_PROGRESS) {
                tasksMap.put(taskId, Pair.of(taskStatus, accomplishment));
            }
        } else {
            handleErrorState(taskId);
        }
    }

    @Override
    public void addTask(Long taskId) {
        tasksMap.put(taskId, Pair.of(TaskStatus.IN_PROGRESS, .0));
    }

    @Override
    public Pair<TaskStatus, Double> getTaskStatus(Long taskId) {
        val taskEntry = tasksMap.get(taskId);

        if (isNull(taskEntry)) {
            val task = taskService.getTask(taskId)
                    .orElseThrow(() -> new ValidationException("Can't find task"));
            return Pair.of(task.getTaskStatus(), .0);
        }

        return taskEntry;
    }

    @Override
    public boolean contains(long taskId) {
        return tasksMap.containsKey(taskId);
    }

    private void handleErrorState(long id) {
        val task = taskService.getTask(id)
                .orElseThrow(() -> new ValidationException("Can't find task"));

        if (task.getTaskStatus() == TaskStatus.IN_PROGRESS) {
            tasksMap.put(id, Pair.of(TaskStatus.IN_PROGRESS, .0));
        } else if (task.getTaskStatus() == TaskStatus.CANCELLED) {
            throw new TaskCancelException("Task was cancelled");
        } else {
            throw new ServiceException("Task is not in progress");
        }
    }
}
