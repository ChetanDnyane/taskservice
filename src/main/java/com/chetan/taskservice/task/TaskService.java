package com.chetan.taskservice.task;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponse createTask(CreateTaskRequest request) {

        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Task task = new Task();

        task.setUserId(userId);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setPriority(
                request.priority() != null
                        ? request.priority()
                        : TaskPriority.MEDIUM
        );
        task.setStatus(TaskStatus.TODO);
        task.setDueDate(request.dueDate());

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    public List<TaskResponse> getMyTasks() {

        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return taskRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TaskResponse getTask(Long taskId) {

        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Task task = taskRepository
                .findByIdAndUserId(taskId, userId)
                .orElseThrow(() ->
                        new TaskNotFoundException(taskId)
                );

        return toResponse(task);
    }

    public TaskResponse updateTask(
            Long taskId,
            UpdateTaskRequest request) {

        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Task task = taskRepository
                .findByIdAndUserId(taskId, userId)
                .orElseThrow(() ->
                        new TaskNotFoundException(taskId)
                );

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    public void deleteTask(Long taskId) {

        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Task task = taskRepository
                .findByIdAndUserId(taskId, userId)
                .orElseThrow(() ->
                        new TaskNotFoundException(taskId)
                );

        taskRepository.delete(task);
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}