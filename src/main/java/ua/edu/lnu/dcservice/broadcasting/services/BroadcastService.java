package ua.edu.lnu.dcservice.broadcasting.services;

import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import ua.edu.lnu.dcservice.broadcasting.TaskBroadcast;
import ua.edu.lnu.dcservice.entities.enums.TaskStatus;
import ua.edu.lnu.dcservice.tasks.TaskExecutionHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class BroadcastService {
    private final Map<Long, TaskBroadcast> broadcastMap = Collections.synchronizedMap(new HashMap<>());

    @Autowired
    private TaskExecutionHandler taskExecutionHandler;

    private TaskBroadcast getTaskBroadcast(long taskId) {
        synchronized (broadcastMap) {
            if (broadcastMap.containsKey(taskId)) {
                return broadcastMap.get(taskId);
            }

            val taskBroadcast = new TaskBroadcast(taskExecutionHandler, taskId);
            broadcastMap.put(taskId, taskBroadcast);

            return taskBroadcast;
        }
    }

    public DeferredResult<Pair<TaskStatus, Double>> getTaskStatus(Long taskId) {
        val taskBroadcast = getTaskBroadcast(taskId);
        val deferredResult = new DeferredResult<Pair<TaskStatus, Double>>();
        taskBroadcast.addSubscriber(deferredResult);

        return deferredResult;
    }

    @Scheduled(fixedDelay = 300_000)
    public void killBroadcasts() {
        broadcastMap.forEach((taskId, taskBroadcast) -> taskBroadcast.killIfNotInProgress());
    }
}
