package ua.edu.lnu.dcservice.controllers;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.lnu.dcservice.broadcasting.services.BroadcastService;
import ua.edu.lnu.dcservice.dto.TaskDto;
import ua.edu.lnu.dcservice.entities.enums.TaskStatus;
import ua.edu.lnu.dcservice.entities.enums.TaskType;
import ua.edu.lnu.dcservice.services.TaskService;
import ua.edu.lnu.dcservice.tasks.TaskExecutionHandler;

import java.util.List;

@RestController
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskExecutionHandler taskExecutionHandler;
    @Autowired
    private BroadcastService broadcastService;

    @PostMapping("/tasks")
    public TaskDto addTask(@RequestParam String taskName,
                           @RequestParam TaskType taskType,
                           @RequestPart("file") MultipartFile file) {
        return taskService.addTask(taskName, taskType, file);
    }

    @PutMapping("/tasks/{id}/run")
    public void runTask(@PathVariable long id) {
        taskService.executeTask(id);
    }

    @PutMapping("tasks/{id}/kill")
    public void killTask(@PathVariable long id) {
        taskService.killTask(id);
    }

    @GetMapping("tasks")
    public List<TaskDto> getTasks() {
        return taskService.getUserTasks();
    }

    @GetMapping("/tasks/{id}/status")
    public DeferredResult<Pair<TaskStatus, Double>> getTaskStatus(@PathVariable long id) {
        return broadcastService.getTaskStatus(id);
    }
}
