package com.taskmanager.task_manager.exception

import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException

@RestControllerAdvice
class `GlobalExceptionHandler.kt` {

    @ExceptionHandler(TaskNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(ex: TaskNotFoundException): Map<String, String> =
        mapOf("error" to (ex.message ?: "Not found"))

    @ExceptionHandler(WebExchangeBindException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidation(ex: WebExchangeBindException): Map<String, Any> {
        val errors = ex.bindingResult.allErrors.associate {
            (it as FieldError).field to (it.defaultMessage ?: "Invalid value")
        }
        return mapOf("errors" to errors)
    }
}
