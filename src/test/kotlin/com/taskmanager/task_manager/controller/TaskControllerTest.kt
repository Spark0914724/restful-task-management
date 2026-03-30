package com.taskmanager.task_manager.controller

import com.taskmanager.task_manager.dto.PageResponse
import com.taskmanager.task_manager.dto.TaskResponse
import com.taskmanager.task_manager.exception.TaskNotFoundException
import com.taskmanager.task_manager.model.TaskStatus
import com.taskmanager.task_manager.service.TaskService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@WebFluxTest(TaskController::class)
class TaskControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var taskService: TaskService

    @TestConfiguration
    class Config {
        @Bean
        fun taskService(): TaskService = mockk()
    }

    private val sampleResponse = TaskResponse(
        id = 1L,
        title = "Test Task",
        description = "desc",
        status = TaskStatus.NEW,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @Test
    fun `POST api-tasks should return 201`() {
        every { taskService.createTask(any()) } returns Mono.just(sampleResponse)

        webTestClient.post().uri("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"title": "Test Task", "description": "desc"}""")
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.title").isEqualTo("Test Task")
    }

    @Test
    fun `POST api-tasks with blank title should return 400`() {
        webTestClient.post().uri("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"title": ""}""")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `POST api-tasks with short title should return 400`() {
        webTestClient.post().uri("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"title": "ab"}""")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `GET api-tasks-id should return 200`() {
        every { taskService.getTaskById(1L) } returns Mono.just(sampleResponse)

        webTestClient.get().uri("/api/tasks/1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
    }

    @Test
    fun `GET api-tasks-id should return 404 when not found`() {
        every { taskService.getTaskById(99L) } returns Mono.error(TaskNotFoundException(99L))

        webTestClient.get().uri("/api/tasks/99")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `PATCH api-tasks-id-status should return 200`() {
        val updated = sampleResponse.copy(status = TaskStatus.DONE)
        every { taskService.updateStatus(1L, any()) } returns Mono.just(updated)

        webTestClient.patch().uri("/api/tasks/1/status")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"status": "DONE"}""")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.status").isEqualTo("DONE")
    }

    @Test
    fun `DELETE api-tasks-id should return 204`() {
        every { taskService.deleteTask(1L) } returns Mono.empty()

        webTestClient.delete().uri("/api/tasks/1")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `GET api-tasks should return page response`() {
        val page = PageResponse(listOf(sampleResponse), 0, 10, 1L, 1)
        every { taskService.getTasks(0, 10, null) } returns Mono.just(page)

        webTestClient.get().uri("/api/tasks?page=0&size=10")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.totalElements").isEqualTo(1)
    }
}
