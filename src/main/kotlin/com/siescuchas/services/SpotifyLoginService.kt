package com.siescuchas.services

import com.siescuchas.errors.HttpError
import com.siescuchas.models.SpotifyMe
import com.siescuchas.models.SpotifySearch
import com.siescuchas.models.SpotifyToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.io.File
import java.util.*

@Configuration
@Service
class SpotifyLoginService() {
    companion object {
        val BASE64_ENCODER: Base64.Encoder = Base64.getEncoder()

        private val logger: Logger = LoggerFactory.getLogger(SpotifyLoginService::class.java)

        private fun generateRandomString(length: Int): String {
            var text = ""
            val possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            val random = Random(System.nanoTime())
            for (i in 0..length) {
                val randomIndex = random.nextInt(possible.length)
                text += possible[randomIndex]
            }
            return text
        };

        fun handle400(response: ClientResponse): Mono<Error> {
            return response.bodyToMono(HttpError::class.java)
        }

        fun handle500(response: ClientResponse): Mono<Error> {
            return response.bodyToMono(HttpError::class.java)
        }
    }

    @Value("\${spotify.CLIENT_ID}")
    lateinit var CLIENT_ID: String

    @Value("\${spotify.CLIENT_SECRET}")
    lateinit var CLIENT_SECRET: String

    @Value("\${spotify.REDIRECT_URI}")
    lateinit var REDIRECT_URI: String

    fun getLoginURI(): Pair<String, String> {
        val scope: String = "user-read-private user-read-email streaming"
        val state: String = generateRandomString(16)

        val queryMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        queryMap.add("response_type", "code")
        queryMap.add("client_id", CLIENT_ID)
        queryMap.add("scope", scope)
        queryMap.add("redirect_uri", REDIRECT_URI)
        queryMap.add("state", state)

        val uriComponents: UriComponents = UriComponentsBuilder
                .fromHttpUrl("https://accounts.spotify.com/authorize")
                .queryParams(queryMap)
                .encode()
                .build()
        val uriString = uriComponents.toUri().toURL().toString()
        println(uriString)
        return Pair(uriString, state)
    }

    suspend fun getAccessToken(code: String): SpotifyToken {
        val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()

        bodyMap.add("code", code)
        bodyMap.add("redirect_uri", REDIRECT_URI)
        bodyMap.add("grant_type", "authorization_code")

        var authString = "$CLIENT_ID:$CLIENT_SECRET".let {
            BASE64_ENCODER.encodeToString(it.toByteArray())
        }
        val spotifyWebClient = WebClient.create("https://accounts.spotify.com/api/token")
        return spotifyWebClient
                .post()
                .header("Authorization", "Basic $authString")
                .body(BodyInserters.fromFormData(bodyMap))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, ::handle400)
                .onStatus(HttpStatus::is5xxServerError, ::handle500)
                .awaitBody<SpotifyToken>()

    }

    suspend fun getRefreshToken(refreshToken: String): SpotifyToken {
        val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        bodyMap.add("refresh_token", refreshToken)
        bodyMap.add("grant_type", "refresh_token")
        var authString = "$CLIENT_ID:$CLIENT_SECRET".let {
            BASE64_ENCODER.encodeToString(it.toByteArray())
        }
        val spotifyWebClient = WebClient.create("https://accounts.spotify.com/api/token")
        return spotifyWebClient
                .post()
                .header("Authorization", "Basic $authString")
                .body(BodyInserters.fromFormData(bodyMap))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, ::handle400)
                .onStatus(HttpStatus::is5xxServerError, ::handle500)
                .awaitBody<SpotifyToken>()
    }

    suspend fun getMe(accessToken: String): SpotifyMe {
        val spotifyWebClient = WebClient.create("https://api.spotify.com/v1/me")
        return spotifyWebClient
                .get()
                .header("Authorization", "Bearer $accessToken")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, ::handle400)
                .onStatus(HttpStatus::is5xxServerError, ::handle500)
                .awaitBody<SpotifyMe>()
    }

    suspend fun getSearch(accessToken: String, query: String): SpotifySearch {
        val queryMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        queryMap.add("q", query)
        queryMap.add("market", "from_token")
        queryMap.add("limit", "10")
        queryMap.add("type", "track")

        val uriComponents: UriComponents = UriComponentsBuilder
                .fromHttpUrl("https://api.spotify.com/v1/search")
                .queryParams(queryMap)
                .encode()
                .build()
        val uriString = uriComponents.toUri().toURL().toString()
        val spotifyWebClient = WebClient.create(uriString)
        var res = spotifyWebClient
                .get()
                .header("Authorization", "Bearer ${accessToken}")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, ::handle400)
                .onStatus(HttpStatus::is5xxServerError, ::handle500)
                .awaitBody<SpotifySearch>()
        println(res)
        return res
    }
}