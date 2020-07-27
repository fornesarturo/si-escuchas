package com.siescuchas.controllers

import com.siescuchas.models.Message
import com.siescuchas.repositories.MessageRepository
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.Valid
@CrossOrigin("https://si-escuchas.netlify.app", allowCredentials = "true")
@RestController
@RequestMapping("/msg")
class MessageController @Autowired constructor(
        val messageRepository: MessageRepository
) {
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun createMessage(@Validated @RequestBody message: Message): Mono<Message> {
        return messageRepository.save(message)
    }

    @GetMapping("/channel/{channelId}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamMessages(@PathVariable channelId: String): Flux<Message> {
        return messageRepository.findWithTailableCursorByChannelId(channelId)
    }

    @GetMapping("/sse/{channelId}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamEvents(@PathVariable channelId: String): Flux<ServerSentEvent<Message>> {
        return messageRepository.findRecentMessagesByChannelId(channelId)
                .map {
                    ServerSentEvent.builder<Message>()
                            .id(it.id!!)
                            .data(it)
                            .event(it.message)
                            .build()
                }

//        return messageRepository.findWithTailableCursorByChannelId(channelId)
//                .map {
//                    ServerSentEvent.builder<Message>()
//                            .id(it.id!!)
//                            .data(it)
//                            .event(it.message)
//                            .build()
//                }
    }
}