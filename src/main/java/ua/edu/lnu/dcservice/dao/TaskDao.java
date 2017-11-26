package ua.edu.lnu.dcservice.dao;

import ua.edu.lnu.dcservice.entities.Task;
import ua.edu.lnu.dcservice.entities.enums.TaskStatus;

public interface TaskDao extends GenericDao<Task, Long> {
    void updateStatus(long id, TaskStatus taskStatus);
}
