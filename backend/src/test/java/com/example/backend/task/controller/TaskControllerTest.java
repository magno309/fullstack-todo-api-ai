package com.example.backend.task.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.task.dto.TaskResponse;
import com.example.backend.task.exception.TaskNotFoundException;
import com.example.backend.task.service.TaskService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    void getAllShouldReturnOk() throws Exception {
        TaskResponse task = new TaskResponse(1L, "Task", "Desc", false, Instant.now(), Instant.now());
        when(taskService.getAll()).thenReturn(List.of(task));

        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Tasks retrieved"))
            .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void createShouldReturnCreated() throws Exception {
        TaskResponse task = new TaskResponse(1L, "Task", "Desc", false, Instant.now(), Instant.now());
        when(taskService.create(any())).thenReturn(task);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "Task",
                      "description": "Desc"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value(201))
            .andExpect(jsonPath("$.data.title").value("Task"));
    }

    @Test
    void createShouldReturnBadRequestWhenValidationFails() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "",
                      "description": "Desc"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void updateShouldReturnNotFoundWhenTaskDoesNotExist() throws Exception {
        when(taskService.update(any(), any())).thenThrow(new TaskNotFoundException(55L));

        mockMvc.perform(put("/api/tasks/55")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "Task",
                      "description": "Desc",
                      "completed": true
                    }
                    """))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deleteShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Task deleted"));
    }
}
