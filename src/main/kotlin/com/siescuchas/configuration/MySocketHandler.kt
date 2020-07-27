package com.siescuchas.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import reactor.core.publisher.UnicastProcessor
import java.util.concurrent.ConcurrentLinkedQueue

class MySocketHandler: WebSocketHandler {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(MySocketHandler::class.java)

        var socketMap: MutableMap<String, UnicastProcessor<WebSocketMessage>> = LinkedHashMap()
    }
    override fun handle(session: WebSocketSession): Mono<Void> {
        println("Interaction: ${session.id}");
        var processor = UnicastProcessor<WebSocketMessage>(ConcurrentLinkedQueue<WebSocketMessage>());
        socketMap.putIfAbsent(session.id, processor)
        var input = session.receive()
                .doOnNext {
                    println("IN HERE!!!")
                    val payload = it.payloadAsText
                    println(payload)
                    processor.sink()
                            .next(WebSocketMessage(
                                    WebSocketMessage.Type.TEXT,
                                    session.bufferFactory()
                                            .wrap("Hey".toByteArray()))
                            )
                }.subscribe()
        processor
                .doOnNext {
                    val payload = it.payloadAsText
                    println("Processor has message --> $payload <--")
                }
        return session.send(processor.publish())
        // return session.send(input)
    }
}