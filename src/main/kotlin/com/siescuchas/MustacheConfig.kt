package com.siescuchas

import org.springframework.boot.web.reactive.result.view.MustacheViewResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.ViewResolverRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class MustacheConfig(val resolver: MustacheViewResolver): WebFluxConfigurer {
    override fun configureViewResolvers(registry: ViewResolverRegistry) = registry.viewResolver(resolver)
}