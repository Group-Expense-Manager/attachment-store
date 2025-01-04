package pl.edu.agh.gem.external.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private const val KB_MULTIPLIER = 1024
private const val MB_MULTIPLIER = 1024 * 1024
private const val GB_MULTIPLIER = 1024 * 1024 * 1024
private const val DEFAULT_MULTIPLIER = 0

private const val UNIT_SIZE = 2

@Configuration
class AttachmentConfig {
    @Bean
    fun attachmentsLimits(attachmentsProperties: AttachmentProperties): AttachmentsLimits {
        return attachmentsProperties.toAttachmentsLimits()
    }
}

@ConfigurationProperties(prefix = "attachment")
data class AttachmentProperties(
    val maxSize: String,
    val allowedType: List<String>,
)

data class AttachmentsLimits(
    val maxSizeInBytes: Long,
)

private fun parseFileSize(size: String): Long {
    val value = size.trim().take(size.length - UNIT_SIZE).toLong()
    val unit = size.trim().takeLast(UNIT_SIZE)
    val multiplier =
        when (unit) {
            "KB" -> KB_MULTIPLIER
            "MB" -> MB_MULTIPLIER
            "GB" -> GB_MULTIPLIER
            else -> DEFAULT_MULTIPLIER
        }

    return value * multiplier
}

private fun AttachmentProperties.toAttachmentsLimits() =
    AttachmentsLimits(
        maxSizeInBytes = parseFileSize(this.maxSize),
    )
