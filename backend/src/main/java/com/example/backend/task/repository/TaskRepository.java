package com.example.backend.task.repository;

import com.example.backend.task.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    List<Task> findAll();

    Optional<Task> findById(Long id);

    Task save(Task task);

    void deleteById(Long id);
}
