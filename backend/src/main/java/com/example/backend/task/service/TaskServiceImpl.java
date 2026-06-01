package com.example.backend.task.service;

import com.example.backend.task.dto.TaskCreateRequest;
import com.example.backend.task.dto.TaskResponse;
import com.example.backend.task.dto.TaskUpdateRequest;
import com.example.backend.task.exception.TaskNotFoundException;
import com.example.backend.task.model.Task;
import com.example.backend.task.repository.TaskRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<TaskResponse> getAll() {
        return taskRepository.findAll().stream()
            .sorted(Comparator.comparing(Task::getCreatedAt).reversed())
            .map(this::toResponse)
            .toList();
    }

    @Override
    public TaskResponse getById(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        return toResponse(task);
    }

    @Override
    public TaskResponse create(TaskCreateRequest request) {
        Instant now = Instant.now();
        Task task = new Task(
            null,
            request.getTitle().trim(),
            request.getDescription() == null ? null : request.getDescription().trim(),
            false,
            now,
            now
        );

        return toResponse(taskRepository.save(task));
    }

    @Override
    public TaskResponse update(Long id, TaskUpdateRequest request) {
        Task existing = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));

        existing.setTitle(request.getTitle().trim());
        existing.setDescription(request.getDescription() == null ? null : request.getDescription().trim());
        existing.setCompleted(request.getCompleted());
        existing.setUpdatedAt(Instant.now());

        return toResponse(taskRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        Task existing = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.deleteById(existing.getId());
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.isCompleted(),
            task.getCreatedAt(),
            task.getUpdatedAt()
        );
    }
}
