package com.siescuchas.controllers

import com.siescuchas.errors.HttpError
import com.siescuchas.models.SpotifyMe
import com.siescuchas.models.SpotifySearch
import com.siescuchas.models.SpotifyToken
import com.siescuchas.services.SpotifyLoginService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
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
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.util.*

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
                .sameSite("None")
                .secure(true)
                .build()
        response.addCookie(stateCookie)

        return "redirect:$uriString"
    }

    @GetMapping("/callback")
    suspend fun callback(request: ServerHttpRequest,
                         response: ServerHttpResponse,
                 @RequestParam("code") code: String
    ): String {

        val accessToken = spotifyLoginService.getAccessToken(code)

        accessToken.refreshToken?.let {
            response.addCookie(ResponseCookie
                    .from("refresh_token", accessToken.refreshToken)
                    .httpOnly(true)
                    .sameSite("None")
                    .secure(true)
                    .build())
        }

        response.addCookie(ResponseCookie
                .from("access_token", accessToken.accessToken)
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .build())

        val webAppUrl = "http://localhost:8081"
        val homeUrl = "$webAppUrl/home"

        val queryMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        queryMap.add("access_token", accessToken.accessToken)

        val uriComponents: UriComponents = UriComponentsBuilder
                .fromHttpUrl(homeUrl)
                .queryParams(queryMap)
                .encode()
                .build()
        val uriString = uriComponents.toUri().toURL().toString()

        return "redirect:$uriString"
    }

    @GetMapping("/refresh_token")
    @ResponseBody
    suspend fun refreshToken(response: ServerHttpResponse, @CookieValue("refresh_token") refreshToken: String): SpotifyToken {
        return coroutineScope {
            val token = spotifyLoginService.getRefreshToken(refreshToken)
            response.addCookie(ResponseCookie
                    .from("access_token", token.accessToken)
                    .httpOnly(true)
                    .sameSite("None")
                    .secure(true)
                    .build())
            token
        }
    }

    @GetMapping("/me")
    @ResponseBody
    suspend fun getInfo(
            @CookieValue("access_token") accessToken: String
    ): SpotifyMe {
        return spotifyLoginService.getMe(accessToken)
    }

    @GetMapping("/search")
    @ResponseBody
    suspend fun getSearch(
            @CookieValue("access_token") accessToken: String,
            @RequestParam("query") query: String
    ): SpotifySearch {
        return coroutineScope {
            val searchSuspend = async { spotifyLoginService.getSearch(accessToken, query) }
            searchSuspend.await()
        }
    }

    @ExceptionHandler
    fun handleHttpError(e: HttpError): ResponseEntity<String> {
        val status = e.status ?: 500
        return ResponseEntity(e.message, HttpStatus.valueOf(status))
    }
}