package ua.edu.lnu.dcservice.services.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.lnu.dcservice.dao.ApplicationUserDao;
import ua.edu.lnu.dcservice.dao.TaskDao;
import ua.edu.lnu.dcservice.dto.TaskDto;
import ua.edu.lnu.dcservice.entities.Task;
import ua.edu.lnu.dcservice.entities.enums.TaskStatus;
import ua.edu.lnu.dcservice.entities.enums.TaskType;
import ua.edu.lnu.dcservice.exceptions.ServiceException;
import ua.edu.lnu.dcservice.exceptions.ValidationException;
import ua.edu.lnu.dcservice.services.FileService;
import ua.edu.lnu.dcservice.services.TaskService;
import ua.edu.lnu.dcservice.tasks.TaskExecutionHandler;
import ua.edu.lnu.dcservice.tasks.TaskJob;
import ua.edu.lnu.dcservice.utils.SecurityUtils;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static ua.edu.lnu.dcservice.utils.CollectionUtils.mapToList;

@Slf4j
@Repository
public class TaskServiceImpl implements TaskService {
    private static final String TASK_OUTPUT_DIRECTORY = "tasks/output";
    private static final String TASK_INPUT_DIRECTORY = "tasks/input";

    private static final String OUTPUT_PREFIX = "output_";
    private static final String INPUT_PREFIX = "input_";

    @Autowired
    private TaskDao taskDao;
    @Autowired
    private FileService fileService;
    @Autowired
    private ApplicationUserDao userDao;
    @Autowired
    private TaskExecutionHandler taskExecutionHandler;


    @Override
    public Optional<TaskDto> getTask(long taskId) {
        val task = taskDao.get(taskId);

        if (isNull(task)) {
            return Optional.empty();
        }

        return Optional.of(TaskDto.from(task));
    }

    @Override
    public List<TaskDto> getUserTasks() {
        val user = userDao.findByUsername(SecurityUtils.getUsername())
                .orElseThrow(() -> new ServiceException("Our security is not working"));

        return mapToList(user.getTasks(), TaskDto::from);
    }

    @Override
    public void updateStatus(long id, TaskStatus taskStatus) {
        taskDao.updateStatus(id, taskStatus);
    }

    @Override
    @Transactional
    public TaskDto addTask(String taskName, TaskType taskType, MultipartFile multipartFile) {
        val task = new Task();
        task.setTaskStatus(TaskStatus.ADDED);
        task.setCreatedBy(userDao.findByUsername(SecurityUtils.getUsername())
                .orElseThrow(() -> new ServiceException("Our security is not working")));
        task.setTaskName(taskName);
        task.setTaskType(taskType);

        taskDao.persist(task);

        fileService.saveFile(TASK_INPUT_DIRECTORY, getTaskInputFileName(task.getId()), multipartFile);

        return TaskDto.from(task);
    }

    @Override
    @Transactional
    public void executeTask(Long taskId) {
        val task = getAndValidateTask(taskId);

        if (task.getTaskStatus() == TaskStatus.IN_PROGRESS) {
            throw new ValidationException("Task is already being executed");
        } else if (task.getTaskStatus() == TaskStatus.DONE) {
            throw new ValidationException("Task was already finished");
        }

        val taskJob = TaskJob.createTaskJob(TaskDto.from(task), this, taskExecutionHandler);

        Thread t = new Thread(taskJob::run);
        t.setDaemon(true);
        t.start();
    }

    @Override
    @Transactional
    public void killTask(Long taskId) {
        val task = getAndValidateTask(taskId);
        if (task.getTaskStatus() != TaskStatus.IN_PROGRESS) {
            throw new ValidationException("Can't cancel task. It is not in progress");
        }
        task.setTaskStatus(TaskStatus.CANCELLED);
        taskDao.merge(task);

        taskExecutionHandler.updateTaskExecution(taskId, TaskStatus.CANCELLED, 0);
    }

    @Override
    @Transactional
    public File loadTaskInputFile(long id) {
        return fileService.getFile(TASK_INPUT_DIRECTORY, getTaskInputFileName(id));
    }

    @Override
    @Transactional
    public File createTaskOutputFile(long id) {
        return fileService.createFile(TASK_OUTPUT_DIRECTORY, getTaskOutputFileName(id));
    }

    private Task getAndValidateTask(long taskId) {
        val user = userDao.findByUsername(SecurityUtils.getUsername())
                .orElseThrow(() -> new ServiceException("Our security is not working"));
        val task = taskDao.get(taskId);

        if (isNull(task) || task.getCreatedBy().getId() != user.getId()) {
            throw new ValidationException("Can't find such task");
        }

        return task;
    }

    private String getTaskOutputFileName(long id) {
        return String.format("%s%s.txt", OUTPUT_PREFIX, id);
    }

    private String getTaskInputFileName(long id) {
        return String.format("%s%s.txt", INPUT_PREFIX, id);
    }
}
