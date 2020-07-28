package com.siescuchas

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.config.EnableWebFlux
import java.text.NumberFormat


@EnableWebFlux
@SpringBootApplication
@ComponentScan
class ApiApplication

fun main(args: Array<String>) {
	runApplication<ApiApplication>(*args)

	val logger: Logger = LoggerFactory.getLogger(ApiApplication::class.java)

	val runtime = Runtime.getRuntime()

	val format: NumberFormat = NumberFormat.getInstance()

	val maxMemory: Long = runtime.maxMemory()
	val allocatedMemory: Long = runtime.totalMemory()
	val freeMemory: Long = runtime.freeMemory()
	val mb: Long = 1024 * 1024
	val mega = " MB"

	logger.info("========================== Memory Info ==========================")
	logger.info("Free memory: " + format.format(freeMemory / mb).toString() + mega)
	logger.info("Allocated memory: " + format.format(allocatedMemory / mb).toString() + mega)
	logger.info("Max memory: " + format.format(maxMemory / mb).toString() + mega)
	logger.info("Total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / mb).toString() + mega)
	logger.info("=================================================================\n")
}

@ControllerAdvice
class ControllerExceptionHandler() {
	@ExceptionHandler()
	protected fun handleConflict(error: Error): ResponseEntity<String> {
		val bodyOfResponse = error.toString()
		return ResponseEntity(bodyOfResponse, HttpStatus.BAD_REQUEST)
	}
}
