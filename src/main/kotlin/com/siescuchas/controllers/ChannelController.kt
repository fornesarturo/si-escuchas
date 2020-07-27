package com.siescuchas.controllers

import com.siescuchas.models.Channel
import com.siescuchas.models.Message
import com.siescuchas.repositories.ChannelRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
// https://si-escuchas.netlify.app
@CrossOrigin("https://si-escuchas.netlify.app", allowCredentials = "true")
@RestController
@RequestMapping("/channel")
class ChannelController @Autowired constructor(
        val channelRepository: ChannelRepository
) {
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun createChannel(@Validated @RequestBody channel: Channel): Mono<Channel> {
        return channelRepository.save(channel)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getChannel(@PathVariable id: String): Mono<Channel> {
        return channelRepository.findById(id)
    }

}