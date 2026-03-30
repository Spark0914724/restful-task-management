package com.taskmanager.task_manager.service

import com.taskmanager.task_manager.dto.CreateTaskRequest
import com.taskmanager.task_manager.dto.UpdateStatusRequest
import com.taskmanager.task_manager.exception.TaskNotFoundException
import com.taskmanager.task_manager.model.Task
import com.taskmanager.task_manager.model.TaskStatus
import com.taskmanager.task_manager.repository.TaskRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

class TaskServiceTest {

    private val taskRepository = mockk<TaskRepository>()
    private val taskService = TaskService(taskRepository)

    private val sampleTask = Task(id = 1L, title = "Test Task", description = "desc", status = TaskStatus.NEW)

    @Test
    fun `should create task successfully`() {
        every { taskRepository.save(any()) } returns sampleTask

        StepVerifier.create(taskService.createTask(CreateTaskRequest("Test Task", "desc")))
            .expectNextMatches { it.title == "Test Task" && it.status == TaskStatus.NEW }
            .verifyComplete()
    }

    @Test
    fun `should get task by id`() {
        every { taskRepository.findById(1L) } returns sampleTask

        StepVerifier.create(taskService.getTaskById(1L))
            .expectNextMatches { it.id == 1L && it.title == "Test Task" }
            .verifyComplete()
    }

    @Test
    fun `should throw TaskNotFoundException when task not found`() {
        every { taskRepository.findById(99L) } returns null

        StepVerifier.create(taskService.getTaskById(99L))
            .expectError(TaskNotFoundException::class.java)
            .verify()
    }

    @Test
    fun `should update task status`() {
        val updatedTask = sampleTask.copy(status = TaskStatus.DONE)
        every { taskRepository.updateStatus(1L, TaskStatus.DONE) } returns 1
        every { taskRepository.findById(1L) } returns updatedTask

        StepVerifier.create(taskService.updateStatus(1L, UpdateStatusRequest(TaskStatus.DONE)))
            .expectNextMatches { it.status == TaskStatus.DONE }
            .verifyComplete()
    }

    @Test
    fun `should delete task`() {
        every { taskRepository.deleteById(1L) } returns 1

        StepVerifier.create(taskService.deleteTask(1L))
            .verifyComplete()

        verify { taskRepository.deleteById(1L) }
    }

    @Test
    fun `should get tasks with pagination`() {
        every { taskRepository.findAll(0, 10, null) } returns listOf(sampleTask)
        every { taskRepository.countAll(null) } returns 1L

        StepVerifier.create(taskService.getTasks(0, 10, null))
            .expectNextMatches { it.totalElements == 1L && it.content.size == 1 }
            .verifyComplete()
    }

    @Test
    fun `should get tasks filtered by status`() {
        every { taskRepository.findAll(0, 10, TaskStatus.NEW) } returns listOf(sampleTask)
        every { taskRepository.countAll(TaskStatus.NEW) } returns 1L

        StepVerifier.create(taskService.getTasks(0, 10, TaskStatus.NEW))
            .expectNextMatches { it.content[0].status == TaskStatus.NEW }
            .verifyComplete()
    }
}
