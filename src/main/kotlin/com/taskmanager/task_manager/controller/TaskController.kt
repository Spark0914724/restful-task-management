package com.taskmanager.task_manager.controller

import com.taskmanager.task_manager.dto.CreateTaskRequest
import com.taskmanager.task_manager.dto.UpdateStatusRequest
import com.taskmanager.task_manager.model.TaskStatus
import com.taskmanager.task_manager.service.TaskService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val taskService: TaskService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTask(@Valid @RequestBody request: CreateTaskRequest) =
        taskService.createTask(request)

    @GetMapping
    fun getTasks(
        @RequestParam page: Int,
        @RequestParam size: Int,
        @RequestParam(required = false) status: TaskStatus?
    ) = taskService.getTasks(page, size, status)

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: Long) =
        taskService.getTaskById(id)

    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateStatusRequest
    ) = taskService.updateStatus(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTask(@PathVariable id: Long): Mono<Void> =
        taskService.deleteTask(id)
}
