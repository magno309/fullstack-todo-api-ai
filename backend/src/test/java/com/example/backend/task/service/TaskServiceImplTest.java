package com.example.backend.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.backend.task.dto.TaskCreateRequest;
import com.example.backend.task.dto.TaskUpdateRequest;
import com.example.backend.task.exception.TaskNotFoundException;
import com.example.backend.task.model.Task;
import com.example.backend.task.repository.TaskRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl(taskRepository);
    }

    @Test
    void createShouldReturnCreatedTask() {
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Learn Spring Boot");
        request.setDescription("Create service and tests");

        Task saved = new Task(1L, "Learn Spring Boot", "Create service and tests", false, Instant.now(), Instant.now());
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        var result = taskService.create(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Learn Spring Boot");
        assertThat(result.completed()).isFalse();
    }

    @Test
    void getAllShouldReturnSortedDescendingByCreationDate() {
        Task oldTask = new Task(1L, "Old", null, false, Instant.parse("2026-01-01T00:00:00Z"), Instant.now());
        Task newTask = new Task(2L, "New", null, false, Instant.parse("2026-02-01T00:00:00Z"), Instant.now());
        when(taskRepository.findAll()).thenReturn(List.of(oldTask, newTask));

        var result = taskService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().id()).isEqualTo(2L);
        assertThat(result.getLast().id()).isEqualTo(1L);
    }

    @Test
    void updateShouldThrowWhenTaskDoesNotExist() {
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Updated");
        request.setDescription("Updated description");
        request.setCompleted(true);

        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.update(99L, request))
            .isInstanceOf(TaskNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    void deleteShouldCallRepositoryDeleteWhenTaskExists() {
        Task existing = new Task(7L, "Delete me", null, false, Instant.now(), Instant.now());
        when(taskRepository.findById(7L)).thenReturn(Optional.of(existing));

        taskService.delete(7L);

        verify(taskRepository).deleteById(7L);
    }
}
