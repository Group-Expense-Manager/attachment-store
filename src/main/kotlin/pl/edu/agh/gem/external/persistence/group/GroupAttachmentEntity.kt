package pl.edu.agh.gem.external.persistence.group

import org.bson.types.Binary
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import pl.edu.agh.gem.external.persistence.AttachmentHistoryEntity
import pl.edu.agh.gem.external.persistence.toDomain
import pl.edu.agh.gem.external.persistence.toEntity
import pl.edu.agh.gem.internal.model.GroupAttachment
import java.time.Instant

@Document("groups")
data class GroupAttachmentEntity(
    @Id
    val id: String,
    val groupId: String,
    val uploadedByUser: String,
    val contentType: String,
    val sizeInBytes: Long,
    val file: Binary,
    val strictAccess: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
    val attachmentHistory: List<AttachmentHistoryEntity>,
)

fun GroupAttachment.toEntity() =
    GroupAttachmentEntity(
        id = id,
        groupId = groupId,
        uploadedByUser = uploadedByUser,
        contentType = contentType,
        sizeInBytes = sizeInBytes,
        file = file,
        strictAccess = strictAccess,
        createdAt = createdAt,
        updatedAt = updatedAt,
        attachmentHistory = attachmentHistory.map { it.toEntity() },
    )

fun GroupAttachmentEntity.toDomain() =
    GroupAttachment(
        id = id,
        groupId = groupId,
        uploadedByUser = uploadedByUser,
        contentType = contentType,
        sizeInBytes = sizeInBytes,
        file = file,
        strictAccess = strictAccess,
        createdAt = createdAt,
        updatedAt = updatedAt,
        attachmentHistory = attachmentHistory.map { it.toDomain() },
    )
