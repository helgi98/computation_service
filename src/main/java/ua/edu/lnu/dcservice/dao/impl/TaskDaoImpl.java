package ua.edu.lnu.dcservice.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ua.edu.lnu.dcservice.dao.TaskDao;
import ua.edu.lnu.dcservice.entities.Task;
import ua.edu.lnu.dcservice.entities.enums.TaskStatus;

@Slf4j
@Repository
public class TaskDaoImpl extends HibernateDao<Task, Long> implements TaskDao {
    public TaskDaoImpl() {
        super(Task.class);
    }

    @Override
    public void updateStatus(long id, TaskStatus taskStatus) {
        Task task = get(id);
        task.setTaskStatus(taskStatus);
        merge(task);
    }
}
