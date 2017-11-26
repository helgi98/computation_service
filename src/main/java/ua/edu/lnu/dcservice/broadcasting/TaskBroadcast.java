package ua.edu.lnu.dcservice.broadcasting;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.context.request.async.DeferredResult;
import ua.edu.lnu.dcservice.entities.enums.TaskStatus;
import ua.edu.lnu.dcservice.tasks.TaskExecutionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;
import static ua.edu.lnu.dcservice.utils.MathUtils.ZERO;

@Slf4j
public class TaskBroadcast {
    private final List<DeferredResult<Pair<TaskStatus, Double>>> subscribers =
            Collections.synchronizedList(new ArrayList<>());

    private long taskId;
    private Thread t;
    private TaskExecutionHandler taskExecutionHandler;
    private Pair<TaskStatus, Double> previousTaskEntry;

    public TaskBroadcast(TaskExecutionHandler taskExecutionHandler, long taskId) {
        this.taskExecutionHandler = taskExecutionHandler;
        this.taskId = taskId;
        previousTaskEntry = null;

        startBroadcasting();
    }

    public void killIfNotInProgress() {
        val newTaskEntry = taskExecutionHandler.getTaskStatus(taskId);
        if (newTaskEntry.getKey() != TaskStatus.IN_PROGRESS) {
            t.interrupt();
        }
    }

    private void inform(Pair<TaskStatus, Double> entry) {
        synchronized (subscribers) {
            for (val subscriber : subscribers) {
                subscriber.setResult(entry);
            }
            subscribers.clear();
        }
    }

    public void addSubscriber(DeferredResult<Pair<TaskStatus, Double>> subscriber) {
        synchronized (subscribers) {
            subscribers.add(subscriber);
        }
    }

    private boolean taskEntriesEqual(Pair<TaskStatus, Double> entry1, Pair<TaskStatus, Double> entry2) {
        return nonNull(entry1) && nonNull(entry2) &&
                entry1.getKey() == entry2.getKey() &&
                Math.abs(entry1.getValue() - entry2.getValue()) <= ZERO;
    }

    private void startBroadcasting() {
        t = new Thread(() -> {
            while (true) {
                try {
                    val newTaskEntry = taskExecutionHandler.getTaskStatus(taskId);

                    if (newTaskEntry.getKey() != TaskStatus.IN_PROGRESS) {
                        inform(newTaskEntry);
                    } else if (!taskEntriesEqual(newTaskEntry, previousTaskEntry)) {
                        previousTaskEntry = newTaskEntry;
                        inform(newTaskEntry);
                    }

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("{}", e);
                    Thread.currentThread().interrupt();
                }
            }
        });

        t.setDaemon(true);
        t.start();
    }
}
