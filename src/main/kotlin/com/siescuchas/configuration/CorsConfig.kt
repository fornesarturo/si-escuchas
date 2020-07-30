package com.siescuchas.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.reactive.result.view.MustacheViewResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.ViewResolverRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@EnableWebFlux
@Configuration
class CorsConfig(val resolver: MustacheViewResolver): WebFluxConfigurer {

    @Value("\${cors.allowed_origin}")
    lateinit var corsOrigin: String

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
                .allowedOrigins(corsOrigin)
                .allowedMethods(
                        "OPTIONS",
                        "GET",
                        "POST",
                        "PUT",
                        "HEAD",
                        "DELETE",
                        "PATCH")
                .allowCredentials(true)
    }

    override fun configureViewResolvers(registry: ViewResolverRegistry) {
        registry.viewResolver(resolver)
    }
}