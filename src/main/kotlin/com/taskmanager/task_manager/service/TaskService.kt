package com.taskmanager.task_manager.service

import com.taskmanager.task_manager.dto.CreateTaskRequest
import com.taskmanager.task_manager.dto.PageResponse
import com.taskmanager.task_manager.dto.TaskResponse
import com.taskmanager.task_manager.dto.UpdateStatusRequest
import com.taskmanager.task_manager.dto.toResponse
import com.taskmanager.task_manager.exception.TaskNotFoundException
import com.taskmanager.task_manager.model.Task
import com.taskmanager.task_manager.model.TaskStatus
import com.taskmanager.task_manager.repository.TaskRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import kotlin.math.ceil

@Service
class TaskService(private val taskRepository: TaskRepository) {

    fun createTask(request: CreateTaskRequest): Mono<TaskResponse> =
        Mono.fromCallable {
            val task = Task(title = request.title, description = request.description)
            taskRepository.save(task).toResponse()
        }.subscribeOn(Schedulers.boundedElastic())

    fun getTaskById(id: Long): Mono<TaskResponse> =
        Mono.fromCallable {
            taskRepository.findById(id) ?: throw TaskNotFoundException(id)
        }.map { it.toResponse() }
            .subscribeOn(Schedulers.boundedElastic())

    fun getTasks(page: Int, size: Int, status: TaskStatus?): Mono<PageResponse<TaskResponse>> =
        Mono.fromCallable {
            val tasks = taskRepository.findAll(page, size, status).map { it.toResponse() }
            val total = taskRepository.countAll(status)
            val totalPages = ceil(total.toDouble() / size).toInt()
            PageResponse(content = tasks, page = page, size = size, totalElements = total, totalPages = totalPages)
        }.subscribeOn(Schedulers.boundedElastic())

    fun updateStatus(id: Long, request: UpdateStatusRequest): Mono<TaskResponse> =
        Mono.fromCallable {
            val updated = taskRepository.updateStatus(id, request.status)
            if (updated == 0) throw TaskNotFoundException(id)
            taskRepository.findById(id) ?: throw TaskNotFoundException(id)
        }.map { it.toResponse() }
            .subscribeOn(Schedulers.boundedElastic())

    fun deleteTask(id: Long): Mono<Void> =
        Mono.fromCallable {
            val deleted = taskRepository.deleteById(id)
            if (deleted == 0) throw TaskNotFoundException(id)
        }.subscribeOn(Schedulers.boundedElastic()).then()
}
