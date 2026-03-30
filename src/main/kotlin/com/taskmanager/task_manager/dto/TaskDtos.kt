package com.taskmanager.task_manager.dto

import com.taskmanager.task_manager.model.TaskStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CreateTaskRequest(
    @field:NotBlank(message = "Title must not be blank")
    @field:Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    val title: String,
    val description: String? = null
)

data class UpdateStatusRequest(
    val status: TaskStatus
)

data class TaskResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val status: TaskStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)
