package com.taskmanager.task_manager.repository

import com.taskmanager.task_manager.model.Task
import com.taskmanager.task_manager.model.TaskStatus
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime

@Repository
class TaskRepository(private val jdbcClient: JdbcClient) {

    private fun rowMapper(rs: ResultSet) = Task(
        id = rs.getLong("id"),
        title = rs.getString("title"),
        description = rs.getString("description"),
        status = TaskStatus.valueOf(rs.getString("status")),
        createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
        updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
    )

    fun save(task: Task): Task {
        val now = LocalDateTime.now()
        val keyHolder = GeneratedKeyHolder()
        jdbcClient.sql(
            "INSERT INTO tasks (title, description, status, created_at, updated_at) VALUES (:title, :description, :status, :createdAt, :updatedAt)"
        )
            .param("title", task.title)
            .param("description", task.description)
            .param("status", task.status.name)
            .param("createdAt", now)
            .param("updatedAt", now)
            .update(keyHolder)
        val id = keyHolder.key!!.toLong()
        return task.copy(id = id, createdAt = now, updatedAt = now)
    }

    fun findById(id: Long): Task? =
        jdbcClient.sql("SELECT * FROM tasks WHERE id = :id")
            .param("id", id)
            .query { rs, _ -> rowMapper(rs) }
            .optional().orElse(null)

    fun findAll(page: Int, size: Int, status: TaskStatus?): List<Task> {
        val sql = if (status != null)
            "SELECT * FROM tasks WHERE status = :status ORDER BY created_at DESC LIMIT :size OFFSET :offset"
        else
            "SELECT * FROM tasks ORDER BY created_at DESC LIMIT :size OFFSET :offset"

        val spec = jdbcClient.sql(sql)
            .param("size", size)
            .param("offset", page * size)

        return if (status != null)
            spec.param("status", status.name).query { rs, _ -> rowMapper(rs) }.list()
        else
            spec.query { rs, _ -> rowMapper(rs) }.list()
    }

    fun countAll(status: TaskStatus?): Long {
        val sql = if (status != null)
            "SELECT COUNT(*) FROM tasks WHERE status = :status"
        else
            "SELECT COUNT(*) FROM tasks"

        val spec = jdbcClient.sql(sql)

        return if (status != null)
            spec.param("status", status.name).query(Long::class.java).single()
        else
            spec.query(Long::class.java).single()
    }

    fun updateStatus(id: Long, status: TaskStatus): Int =
        jdbcClient.sql("UPDATE tasks SET status = :status, updated_at = :updatedAt WHERE id = :id")
            .param("status", status.name)
            .param("updatedAt", LocalDateTime.now())
            .param("id", id)
            .update()

    fun deleteById(id: Long): Int =
        jdbcClient.sql("DELETE FROM tasks WHERE id = :id")
            .param("id", id)
            .update()
}
