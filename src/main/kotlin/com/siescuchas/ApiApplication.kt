package com.siescuchas

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.config.EnableWebFlux


@EnableWebFlux
@SpringBootApplication
@ComponentScan
class ApiApplication

fun main(args: Array<String>) {
	runApplication<ApiApplication>(*args)
}

@ControllerAdvice
class ControllerExceptionHandler() {
	@ExceptionHandler()
	protected fun handleConflict(error: Error): ResponseEntity<String> {
		val bodyOfResponse = error.toString()
		return ResponseEntity(bodyOfResponse, HttpStatus.BAD_REQUEST)
	}
}
