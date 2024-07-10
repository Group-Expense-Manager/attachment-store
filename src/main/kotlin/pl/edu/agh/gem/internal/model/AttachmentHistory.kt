package pl.edu.agh.gem.internal.model

import java.time.Instant
import java.time.Instant.now

data class AttachmentHistory(
    val updatedBy: String,
    val updatedAt: Instant,
    val sizeInBytes: Long,
    val contentType: String,
)

fun createNewAttachmentHistory(
    updatedBy: String,
    sizeInBytes: Long,
    mediaType: String,
) = AttachmentHistory(
    updatedBy = updatedBy,
    updatedAt = now(),
    sizeInBytes = sizeInBytes,
    contentType = mediaType,
)
