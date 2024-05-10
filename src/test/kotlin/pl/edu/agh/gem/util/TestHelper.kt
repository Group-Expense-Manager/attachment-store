package pl.edu.agh.gem.util

import org.bson.types.Binary
import org.springframework.http.MediaType.IMAGE_JPEG_VALUE
import pl.edu.agh.gem.external.persistence.AttachmentHistoryEntity
import pl.edu.agh.gem.external.persistence.group.GroupAttachmentEntity
import pl.edu.agh.gem.internal.model.AttachmentHistory
import pl.edu.agh.gem.internal.model.GroupAttachment
import pl.edu.agh.gem.util.ResourceLoader.loadResourceAsByteArray
import pl.edu.agh.gem.util.TestHelper.SMALL_FILE
import java.time.Instant
import java.time.Instant.parse

object TestHelper {
    val SMALL_FILE = loadResourceAsByteArray("small-image.jpg")
    val LARGE_FILE = loadResourceAsByteArray("large-image.jpg")
    val CSV_FILE = loadResourceAsByteArray("example.csv")
    val EMPTY_FILE = byteArrayOf()
}

fun createGroupAttachment(
    id: String = "id",
    groupId: String = "groupId",
    uploadedByUser: String = "uploadedByUser",
    contentType: String = IMAGE_JPEG_VALUE,
    sizeInBytes: Long = 1,
    file: Binary = Binary(SMALL_FILE),
    createdAt: Instant = parse("2023-01-01T00:00:00Z"),
    updatedAt: Instant? = null,
    updatedByHistory: String? = null,
    updatedAtHistory: Instant? = null,
    updatedSizeInBytes: Long? = null,
) = GroupAttachment(
    id = id,
    groupId = groupId,
    uploadedByUser = uploadedByUser,
    contentType = contentType,
    sizeInBytes = sizeInBytes,
    file = file,
    createdAt = createdAt,
    updatedAt = updatedAt ?: createdAt,
    attachmentHistory = listOf(
        createAttachmentHistory(
            updatedBy = updatedByHistory ?: uploadedByUser,
            updatedAt = updatedAtHistory ?: createdAt,
            sizeInBytes = updatedSizeInBytes ?: sizeInBytes,
        ),
    ),
)

fun createGroupAttachmentEntity(
    id: String = "id",
    groupId: String = "groupId",
    uploadedByUser: String = "uploadedByUser",
    contentType: String = IMAGE_JPEG_VALUE,
    sizeInBytes: Long = 1,
    file: Binary = Binary(SMALL_FILE),
    createdAt: Instant = parse("2023-01-01T00:00:00Z"),
    updatedAt: Instant? = null,
    updatedByHistory: String? = null,
    updatedAtHistory: Instant? = null,
    updatedSizeInBytes: Long? = null,
) = GroupAttachmentEntity(
    id = id,
    groupId = groupId,
    uploadedByUser = uploadedByUser,
    contentType = contentType,
    sizeInBytes = sizeInBytes,
    file = file,
    createdAt = createdAt,
    updatedAt = updatedAt ?: createdAt,
    attachmentHistory = listOf(
        createAttachmentHistoryEntity(
            updatedBy = updatedByHistory ?: uploadedByUser,
            updatedAt = updatedAtHistory ?: createdAt,
            sizeInBytes = updatedSizeInBytes ?: sizeInBytes,
        ),
    ),
)

fun createAttachmentHistory(
    updatedBy: String = "updatedBy",
    updatedAt: Instant = parse("2023-01-01T00:00:00Z"),
    sizeInBytes: Long = 1,
) = AttachmentHistory(
    updatedBy = updatedBy,
    updatedAt = updatedAt,
    sizeInBytes = sizeInBytes,
)

fun createAttachmentHistoryEntity(
    updatedBy: String = "updatedBy",
    updatedAt: Instant = parse("2023-01-01T00:00:00Z"),
    sizeInBytes: Long = 1,
) = AttachmentHistoryEntity(
    updatedBy = updatedBy,
    updatedAt = updatedAt,
    sizeInBytes = sizeInBytes,
)
