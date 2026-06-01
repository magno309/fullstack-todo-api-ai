package com.example.backend.task.service;

import com.example.backend.task.dto.TaskCreateRequest;
import com.example.backend.task.dto.TaskResponse;
import com.example.backend.task.dto.TaskUpdateRequest;
import java.util.List;

public interface TaskService {

    List<TaskResponse> getAll();

    TaskResponse getById(Long id);

    TaskResponse create(TaskCreateRequest request);

    TaskResponse update(Long id, TaskUpdateRequest request);

    void delete(Long id);
}
