package com.siescuchas.controllers

import com.siescuchas.models.SpotifyMe
import com.siescuchas.models.SpotifyToken
import com.siescuchas.services.SpotifyLoginService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.reactive.result.view.RedirectView
import reactor.core.publisher.Mono
import java.util.*

@CrossOrigin("http://localhost:8081")
@Controller
@RequestMapping("")
class SpotifyLoginController @Autowired constructor(
        val spotifyLoginService: SpotifyLoginService
) {
    private val logger: Logger = LoggerFactory.getLogger(SpotifyLoginController::class.java)

    @GetMapping("/spotify")
    fun login(
            request: ServerHttpRequest,
            response: ServerHttpResponse,
            @CookieValue("access_token", required = false)
            accessToken: String?
    ): String {
        val (uriString, state) = spotifyLoginService.getLoginURI();

        val stateCookie = ResponseCookie
                .from("state", state)
                .httpOnly(true)
                .build()
        response.addCookie(stateCookie)

        return "redirect:$uriString"
    }

    @GetMapping("/callback")
    suspend fun callback(request: ServerHttpRequest,
                         response: ServerHttpResponse,
                 @RequestParam("code") code: String,
                 @RequestParam("state") state: String,
                 @CookieValue("state") stateCookie: String
    ): String {
        response.addCookie(ResponseCookie
                .from("state", stateCookie)
                .httpOnly(true)
                .maxAge(0)
                .build())

        val accessToken = spotifyLoginService.getAccessToken(code)

        accessToken.refreshToken?.let {
            response.addCookie(ResponseCookie
                    .from("refresh_token", accessToken.refreshToken)
                    .httpOnly(true)
                    .build())
        }

        response.addCookie(ResponseCookie
                .from("access_token", accessToken.accessToken)
                .httpOnly(true)
                .build())

        val webAppUrl = "http://localhost:8081/"
        val homeUrl = "$webAppUrl/home"
        return "redirect:$homeUrl"
    }

    @GetMapping("/refresh_token")
    @ResponseBody
    suspend fun refreshToken(response: ServerHttpResponse, @CookieValue("refresh_token") refreshToken: String): SpotifyToken {
        return spotifyLoginService.getRefreshToken(refreshToken)
    }

    @GetMapping("/me")
    suspend fun getInfo(model: Model,
                @CookieValue("access_token") accessToken: String
    ): SpotifyMe {
        return spotifyLoginService.getMe(accessToken)
    }
}