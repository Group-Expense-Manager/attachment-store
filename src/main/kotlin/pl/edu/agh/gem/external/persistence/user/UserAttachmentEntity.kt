package pl.edu.agh.gem.external.persistence.user

import org.bson.types.Binary
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import pl.edu.agh.gem.external.persistence.AttachmentHistoryEntity
import pl.edu.agh.gem.external.persistence.toDomain
import pl.edu.agh.gem.external.persistence.toEntity
import pl.edu.agh.gem.internal.model.UserAttachment
import java.time.Instant

@Document("users")
data class UserAttachmentEntity(
    @Id
    val id: String,
    val userId: String,
    val contentType: String,
    val sizeInBytes: Long,
    val file: Binary,
    val createdAt: Instant,
    val updatedAt: Instant,
    val attachmentHistory: List<AttachmentHistoryEntity>,
)

fun UserAttachment.toEntity() = UserAttachmentEntity(
    id = id,
    userId = userId,
    contentType = contentType,
    sizeInBytes = sizeInBytes,
    file = file,
    createdAt = createdAt,
    updatedAt = updatedAt,
    attachmentHistory = attachmentHistory.map { it.toEntity() },
)

fun UserAttachmentEntity.toDomain() = UserAttachment(
    id = id,
    userId = userId,
    contentType = contentType,
    sizeInBytes = sizeInBytes,
    file = file,
    createdAt = createdAt,
    updatedAt = updatedAt,
    attachmentHistory = attachmentHistory.map { it.toDomain() },
)
