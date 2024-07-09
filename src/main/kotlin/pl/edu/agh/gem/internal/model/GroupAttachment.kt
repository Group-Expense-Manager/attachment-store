package pl.edu.agh.gem.internal.model

import org.bson.types.Binary
import java.time.Instant
import java.time.Instant.now
import java.util.UUID.randomUUID

data class GroupAttachment(
    val id: String = randomUUID().toString(),
    val groupId: String,
    val uploadedByUser: String,
    val contentType: String,
    val sizeInBytes: Long,
    val file: Binary,
    val strictAccess: Boolean,
    val createdAt: Instant = now(),
    val updatedAt: Instant = now(),
    val attachmentHistory: List<AttachmentHistory>,
)
