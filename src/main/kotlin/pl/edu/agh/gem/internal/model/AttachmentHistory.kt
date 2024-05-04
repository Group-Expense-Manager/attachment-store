package pl.edu.agh.gem.internal.model

import java.time.Instant
import java.time.Instant.now

data class AttachmentHistory(
    val updatedBy: String,
    val updatedAt: Instant,
    val sizeInBytes: Long,
)

fun createNewAttachmentHistory(updatedBy: String, sizeInBytes: Long) = AttachmentHistory(
    updatedBy = updatedBy,
    updatedAt = now(),
    sizeInBytes = sizeInBytes,
)
