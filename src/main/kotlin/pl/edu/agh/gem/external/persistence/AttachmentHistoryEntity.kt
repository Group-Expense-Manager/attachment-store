package pl.edu.agh.gem.external.persistence

import pl.edu.agh.gem.internal.model.AttachmentHistory
import java.time.Instant

data class AttachmentHistoryEntity(
    val updatedBy: String,
    val updatedAt: Instant,
    val sizeInBytes: Long,
    val contentType: String,
)

fun AttachmentHistoryEntity.toDomain() = AttachmentHistory(
    updatedBy = this.updatedBy,
    updatedAt = this.updatedAt,
    sizeInBytes = this.sizeInBytes,
    contentType = this.contentType,
)

fun AttachmentHistory.toEntity() = AttachmentHistoryEntity(
    updatedBy = this.updatedBy,
    updatedAt = this.updatedAt,
    sizeInBytes = this.sizeInBytes,
    contentType = this.contentType,
)
