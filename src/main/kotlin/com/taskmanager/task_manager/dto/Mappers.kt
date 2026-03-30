package com.taskmanager.task_manager.dto

import com.taskmanager.task_manager.model.Task

fun Task.toResponse() = TaskResponse(
    id = this.id,
    title = this.title,
    description = this.description,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)
