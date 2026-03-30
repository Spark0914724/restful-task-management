package com.taskmanager.task_manager.exception

class TaskNotFoundException(id: Long) : RuntimeException("Task with id $id not found")
