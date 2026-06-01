package com.example.backend.task.controller;

import com.example.backend.common.dto.ApiResponse;
import com.example.backend.task.dto.TaskCreateRequest;
import com.example.backend.task.dto.TaskResponse;
import com.example.backend.task.dto.TaskUpdateRequest;
import com.example.backend.task.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAll() {
        List<TaskResponse> tasks = taskService.getAll();
        return ResponseEntity.ok(ApiResponse.of("Tasks retrieved", HttpStatus.OK.value(), tasks));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> getById(@PathVariable Long id) {
        TaskResponse task = taskService.getById(id);
        return ResponseEntity.ok(ApiResponse.of("Task retrieved", HttpStatus.OK.value(), task));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> create(@Valid @RequestBody TaskCreateRequest request) {
        TaskResponse created = taskService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.of("Task created", HttpStatus.CREATED.value(), created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> update(
        @PathVariable Long id,
        @Valid @RequestBody TaskUpdateRequest request
    ) {
        TaskResponse updated = taskService.update(id, request);
        return ResponseEntity.ok(ApiResponse.of("Task updated", HttpStatus.OK.value(), updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.ok(ApiResponse.of("Task deleted", HttpStatus.OK.value(), null));
    }
}
