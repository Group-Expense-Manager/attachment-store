package pl.edu.agh.gem.external.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
class GroupDefaultConfig

@ConfigurationProperties(prefix = "default")
data class DefaultImagesProperties(
    val group: Set<String>,
    val user: Set<String>,
    val blank: Set<String>,
)
