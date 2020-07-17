package com.siescuchas.services

import com.siescuchas.models.SpotifyMe
import com.siescuchas.models.SpotifyToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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

@Service
class SpotifyLoginService() {
    companion object {
        private var properties: Properties

        init {
            val propertiesFile = File(javaClass.classLoader.getResource("local.properties").file)
            val inputStream = propertiesFile.inputStream()
            properties = Properties().apply {
                load(inputStream)
            }
        }

        val CLIENT_ID: String = properties.getProperty("CLIENT_ID", "") // Your client id
        val CLIENT_SECRET: String = properties.getProperty("CLIENT_SECRET", "") // Your secret
        val REDIRECT_URI: String = properties.getProperty("REDIRECT_URI", "") // Your redirect uri

        val BASE64_ENCODER: Base64.Encoder = Base64.getEncoder()
        val AUTH_STRING = "$CLIENT_ID:$CLIENT_SECRET".let {
            BASE64_ENCODER.encodeToString(it.toByteArray())
        }

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
            response.bodyToMono(String::class.java).subscribe(
                    { value -> logger.error(value) },
                    { error -> error.message?.let{ println(it) } }
            )
            return Mono.just(Error("Your Bad"))
        }

        fun handle500(response: ClientResponse): Mono<Error> {
            response.bodyToMono(String::class.java).subscribe(
                    { value -> logger.error(value) },
                    { error -> error.message?.let{ println(it) } }
            )
            return Mono.just(Error("Your Bad"))
        }
    }

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
                .build()
        val uriString = uriComponents.toUri().toURL().toString()

        return Pair(uriString, state)
    }

    suspend fun getAccessToken(code: String): SpotifyToken {
        val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()

        bodyMap.add("code", code)
        bodyMap.add("redirect_uri", REDIRECT_URI)
        bodyMap.add("grant_type", "authorization_code")

        val spotifyWebClient = WebClient.create("https://accounts.spotify.com/api/token")
        return spotifyWebClient
                .post()
                .header("Authorization", "Basic $AUTH_STRING")
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
        val spotifyWebClient = WebClient.create("https://accounts.spotify.com/api/token")
        return spotifyWebClient
                .post()
                .header("Authorization", "Basic $AUTH_STRING")
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
}