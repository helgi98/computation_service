package ua.edu.lnu.dcservice.tasks;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ua.edu.lnu.dcservice.dto.TaskDto;
import ua.edu.lnu.dcservice.entities.enums.TaskStatus;
import ua.edu.lnu.dcservice.entities.enums.TaskType;
import ua.edu.lnu.dcservice.exceptions.ValidationException;
import ua.edu.lnu.dcservice.services.TaskService;
import ua.edu.lnu.dcservice.tasks.exceptions.TaskCancelException;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

import static java.util.Objects.isNull;
import static ua.edu.lnu.dcservice.utils.MathUtils.ZERO;


@Slf4j
class LinearEquationsTaskJob extends TaskJob {
    private static final int MAX_TASK_SIZE = 20_000;

    private Matrix matrix;
    private double[] values;

    private LinearEquationsTaskJob(TaskDto task, TaskService taskService, TaskExecutionHandler taskExecutionHandler) {
        super(task, taskService, taskExecutionHandler);
    }

    static LinearEquationsTaskJob createJob(TaskDto task, TaskService taskService, TaskExecutionHandler taskExecutionHandler) {
        if (isNull(task) || task.getTaskType() != TaskType.LINEAR_EQUATIONS_SYSTEM) {
            throw new ValidationException("Invalid task type");
        }
        return new LinearEquationsTaskJob(task, taskService, taskExecutionHandler);
    }

    private void gaussStep(Matrix matrix, double[] values, int iteration) {
        double value = matrix.get(iteration, iteration);

        int count = iteration + 1;
        while (Math.abs(value) <= ZERO) {
            if (count == matrix.getSize()) {
                throw new ArithmeticException("Undetermined matrix");
            }

            value = matrix.get(count++, iteration);
        }

        for (int i = 0; i < matrix.getSize(); ++i) {
            if (Math.abs(matrix.get(i, iteration)) <= ZERO) {
                continue;
            }

            double factor = value / matrix.get(i, iteration);

            if (i == iteration) {
                continue;
            }

            for (int j = iteration; j < matrix.getSize(); ++j) {
                matrix.set(i, j, matrix.get(i, j) * factor);
                matrix.set(i, j, matrix.get(iteration, j) - matrix.get(i, j));
            }
            values[i] = values[iteration] - values[i] * factor;
        }
    }

    private boolean singleSolutionExists(Matrix m) {
        for (int i = 0; i < m.getSize(); ++i) {
            boolean allZeroes = true;

            for (int j = 0; j < m.getSize(); ++j) {
                if (Math.abs(m.get(i, j)) >= ZERO) {
                    allZeroes = false;
                    break;
                }
            }

            if (allZeroes) {
                return false;
            }
        }

        return true;
    }

    private double findSolution(double[] row, int index, double[] values, double[] solution) {
        double value = values[index] / row[index];
        for (int i = index + 1; i < row.length; ++i) {
            value -= row[i] * solution[i];
        }

        return value;
    }

    private double[] gauss() {
        if (matrix.getSize() != values.length) {
            throw new ArithmeticException("Invalid sizes");
        }
        int size = matrix.getSize();

        double[] solution = new double[matrix.getSize()];

        for (int i = 0; i < size - 1; ++i) {
            gaussStep(matrix, values, i);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            donePart = (long) (taskSize * ((i + 1) / ((double) size - 1)));
            taskExecutionHandler.updateTaskExecution(task.getId(), TaskStatus.IN_PROGRESS, taskReadyStatus());
        }

        if (singleSolutionExists(matrix)) {
            for (int i = matrix.getSize() - 1; i >= 0; --i) {
                solution[i] = findSolution(matrix.getRow(i), i, values, solution);
            }

            return solution;
        }

        throw new ArithmeticException("The system has more than one solution");
    }

    private void readData() throws IOException {
        val file = taskService.loadTaskInputFile(task.getId());
        try (val dataStream = new FileInputStream(file);
             Scanner scanner = new Scanner(dataStream)) {
            taskSize = scanner.nextLong();

            if (taskSize > MAX_TASK_SIZE) {
                throw new ValidationException("Too large task. Should be <= %s", MAX_TASK_SIZE);
            }
            matrix = new Matrix((int) taskSize);
            values = new double[(int) taskSize];


            for (int i = 0; i < taskSize; ++i) {
                for (int j = 0; j < taskSize; ++j) {
                    double el = scanner.nextDouble();
                    matrix.set(i, j, el);
                }
            }

            for (int i = 0; i < taskSize; ++i) {
                values[i] = scanner.nextDouble();
            }
        }
    }

    private void saveData(double[] result) throws IOException {
        File file = taskService.createTaskOutputFile(task.getId());
        try (val dataStream = new BufferedWriter(new FileWriter(file))) {
            dataStream.write("-- Solution --\n");
            for (int i = 0; i < taskSize; ++i) {
                dataStream.write(result[i] + "\n");
            }
            dataStream.write("-- End --");
        }
    }

    @Override
    public void run() {
        try {
            task.setTaskStatus(TaskStatus.IN_PROGRESS);
            taskService.updateStatus(task.getId(), TaskStatus.IN_PROGRESS);
            taskExecutionHandler.addTask(task.getId());

            readData();
            val result = gauss();
            saveData(result);

            task.setTaskStatus(TaskStatus.DONE);
        } catch (TaskCancelException taskCancelException) {
            log.debug("Task {} was cancelled", task.getId());
            task.setTaskStatus(TaskStatus.CANCELLED);
        } catch (Exception rex) {
            log.error("Exception during task execution {}", rex);
        } finally {
            if (task.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                task.setTaskStatus(TaskStatus.FAILED);
            }

            taskService.updateStatus(task.getId(), task.getTaskStatus());
            taskExecutionHandler.updateTaskExecution(task.getId(), task.getTaskStatus(), taskReadyStatus());
        }
    }

    private static class Matrix {
        private static Random random = new Random();

        private int size;
        private double[][] data;

        Matrix(Matrix matrix) {
            this.size = matrix.getSize();
            this.data = new double[this.size][this.size];

            for (int i = 0; i < size; ++i) {
                System.arraycopy(matrix.data[i], 0, this.data[i], 0, size);
            }
        }

        Matrix(int size) {
            this.size = size;
            data = new double[size][size];
        }

        static Matrix randomFilledMatrix(int size) {
            Matrix matrix = new Matrix(size);

            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    matrix.data[i][j] = random.nextInt(100) + 1;
                }
            }

            return matrix;
        }

        int getSize() {
            return size;
        }

        double get(int i, int j) {
            return data[i][j];
        }

        double[] getRow(int i) {
            return data[i];
        }

        void set(int i, int j, double value) {
            data[i][j] = value;
        }
    }
}
